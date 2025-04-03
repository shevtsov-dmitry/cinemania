package ru.cinemania.privileges.util;

import java.util.Collections;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import ru.cinemania.privileges.user.User;

import org.springframework.beans.factory.annotation.Value;

public class AuthUtils {

    private AuthUtils() {
    }

    @Value("${jwt.secret}")
    private String jwtSecretValue;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMsValue;

    private static String jwtSecret;
    private static long jwtExpirationMs;

    @PostConstruct
    public void init() {
        jwtSecret = jwtSecretValue;
        jwtExpirationMs = jwtExpirationMsValue;
    }

    public static final String generateJwtTokenForUser(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }
}
