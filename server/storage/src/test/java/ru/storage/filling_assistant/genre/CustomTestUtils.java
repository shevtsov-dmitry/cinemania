package ru.storage.filling_assistant.genre;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CustomTestUtils {

    public static String generateRandomHash() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[10];
        secureRandom.nextBytes(randomBytes);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(randomBytes);
        byte[] hashBytes = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
