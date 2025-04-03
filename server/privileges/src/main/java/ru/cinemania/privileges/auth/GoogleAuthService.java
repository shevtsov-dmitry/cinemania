package ru.cinemania.privileges.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Value;

import ru.cinemania.privileges.user.User;
import ru.cinemania.privileges.user.UserRepo;
import ru.cinemania.privileges.util.AuthUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

public class GoogleAuthService {

    @Value("${google.clientId}")
    private String clientId;

    private final UserRepo userRepo;

    public GoogleAuthService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String loginWithGoogle(String idTokenString) throws LoginException {

        var verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
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
            return AuthUtils.generateJwtTokenForUser(user);

        } catch (GeneralSecurityException | IOException e) {
            throw new LoginException(e.getMessage());
        }
    }

}
