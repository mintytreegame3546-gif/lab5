package server.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public final class PasswordHasher {
    public String hash(String password) {
        Objects.requireNonNull(password, "password must not be null");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte hashedByte : hashed) result.append(String.format("%02x", hashedByte));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-224 hashing is unavailable", e);
        }
    }
}
