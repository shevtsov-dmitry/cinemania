package ru.video_material;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class COMMON {
    public static final String HOST = "http://localhost";
    public static final String PORT = "8080";
    public static final String HOST_AND_PORT = HOST + ":" + PORT;
    public static final String ASSETS_PATH = "src/test/java/ru/video_material/assets/";

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
