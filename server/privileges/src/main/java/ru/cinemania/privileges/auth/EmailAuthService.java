package ru.cinemania.privileges.auth;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import ru.cinemania.privileges.user.User;
import ru.cinemania.privileges.user.UserRepo;

@Service
public class EmailAuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private final Map<String, String> emailCodes = new ConcurrentHashMap<>();

    private final UserRepo userRepo;
    private final MailSender mailSender;

    public EmailAuthService(UserRepo userRepo, MailSender mailSender) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
    }

    public void loginWithEmail(String email) {
        String code = generateVerificationCode();
        emailCodes.put(email, code);
        sendEmail(email, code);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Ваш код подтверждения от Cinemania");
        message.setText("Привет от Cinemania!\n\n" +
                "Ваш код подтверждения: " + code + "\n\n" +
                "Наслаждайтесь миром кино с нами!");
        mailSender.send(message);
    }

    /**
     * Checks the email verification code.
     *
     * @param email the email address of the user
     * @param code  the verification code
     *
     * @return the JWT token if the code is valid
     *
     * @throws LoginException if the code is invalid
     */
    public String verifyEmailCode(String email, String code) throws LoginException {
        String storedCode = emailCodes.get(email);
        if (storedCode != null && storedCode.equals(code)) {
            User user = userRepo.findByEmail(email).orElseGet(() -> createUser(email));
            emailCodes.remove(email);
            return generateJwtToken(user);
        } else {
            throw new LoginException("Invalid verification code");
        }
    }

    public User createUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        return userRepo.save(newUser);
    }

    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

}
