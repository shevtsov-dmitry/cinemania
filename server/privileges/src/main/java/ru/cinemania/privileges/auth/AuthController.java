package ru.cinemania.privileges.auth;

import java.security.GeneralSecurityException;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.io.IOException;
import ru.cinemania.privileges.user.UserRepo;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepo userRepo;

    @Value("${google.clientId}")
    private String clientId;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    public AuthController(UserRepo userRepository) {
        this.userRepo = userRepository;
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

                // Find or create user
                User user = userRepo.findByGoogleId(googleId).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    return userRepo.save(newUser);
                });

                // Generate JWT token
                String token = generateJwtToken(user);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying token");
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
