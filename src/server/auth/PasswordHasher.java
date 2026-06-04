package server.auth;

import java.security.MessageDigest;

public class PasswordHasher {
    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] hashed = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hashed) result.append(String.format("%02x", b));
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-224 hashing is unavailable", e);
        }
    }
}
