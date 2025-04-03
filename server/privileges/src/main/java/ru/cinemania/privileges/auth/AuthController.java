package ru.cinemania.privileges.auth;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.security.auth.login.LoginException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.apache.http.Header;
import org.hibernate.annotations.processing.Find;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.IOException;
import ru.cinemania.privileges.user.User;
import ru.cinemania.privileges.user.UserRepo;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${google.clientId}")
    private String clientId;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserRepo userRepo;
    private final EmailAuthService emailAuthService;

    public AuthController(UserRepo userRepository, EmailAuthService emailAuthService) {
        this.userRepo = userRepository;
        this.emailAuthService = emailAuthService;
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> loginWithEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Электронная почта не указана.");
        }
        emailAuthService.loginWithEmail(payload.get("email"));
        return ResponseEntity.internalServerError().body("Не удалось сделать запрос на получение кода.");
    }

    @PostMapping("/login/email/verify")
    public ResponseEntity<String> verifyEmailCode(@RequestBody Map<String, String> payload) {
        try {
            emailAuthService.verifyEmailCode(payload.get("email"), payload.get("code"));
            return ResponseEntity.ok("");
        } catch (LoginException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный код верификации.");
        }
    }

    @PostMapping("/login/google")
    public ResponseEntity<String> loginWithGoogle(@RequestBody String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String googleId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User user = userRepo.findByGoogleId(googleId).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    return userRepo.save(newUser);
                });

                String token = generateJwtToken(user);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying token");
        } catch (java.io.IOException e) {
            LOG.warn("Error verifying token {}", e);
            throw new RuntimeException(e);
        }
    }

    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }
}
