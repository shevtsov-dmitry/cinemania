package ru.cinemania.privileges.auth;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.security.auth.login.LoginException;

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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserRepo userRepo;
    private final EmailAuthService emailAuthService;
    private final GoogleAuthService googleAuthService;

    public AuthController(UserRepo userRepository, EmailAuthService emailAuthService,
            GoogleAuthService googleAuthService) {
        this.userRepo = userRepository;
        this.emailAuthService = emailAuthService;
        this.googleAuthService = googleAuthService;
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
                String token = googleAuthService.loginWithGoogle(idTokenString);
                return ResponseEntity.ok(token);
            } catch (LoginException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неудалось авторизироваться.");
            }
        }
}

}
