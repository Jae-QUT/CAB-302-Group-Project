package com.example.mon.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords with SHA-256.
 * <p>
 * In production, use stronger algorithms (e.g., BCrypt/Argon2).
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Hashes a password with SHA-256 and encodes it as Base64.
     *
     * @param password Raw password text
     * @return SHA-256 hash encoded as Base64
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Verifies a plain password against a stored SHA-256 hash.
     *
     * @param plainPassword The password entered by the user
     * @param storedHash    The hashed password from the database
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(storedHash);
    }
}
