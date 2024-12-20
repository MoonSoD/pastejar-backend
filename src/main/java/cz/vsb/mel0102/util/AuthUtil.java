package cz.vsb.mel0102.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthUtil {
    public static BasicAuth fromHeader(String header) {
        if (header == null) {
            return null;
        }

        var hash = header.substring(6);

        var decoded = Base64.getDecoder().decode(hash);
        var decodedString = new String(decoded);

        if (!decodedString.contains(":")) {
            return null;
        }

        var name = decodedString.split(":")[0];
        var password = decodedString.split(":")[1];

        return new BasicAuth(name, password);
    }

    public static String generateRandomHash() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(randomBytes);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
