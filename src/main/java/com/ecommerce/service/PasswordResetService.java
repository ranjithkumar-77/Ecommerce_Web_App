package com.ecommerce.service;

import com.ecommerce.dao.PasswordResetDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.EmailUtil;
import com.ecommerce.util.PasswordUtil;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int RESET_TOKEN_EXPIRY_SECONDS = 30 * 60;
    private final PasswordResetDAO tokenDAO = new PasswordResetDAO();
    private final UserDAO userDAO = new UserDAO();

    public void requestReset(String email) {
        User user = userDAO.getUserByEmail(email.trim().toLowerCase());
        if (user == null) {
            return;
        }
        byte[] tokenBytes = new byte[32];
        RANDOM.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        if (tokenDAO.createToken(user.getId(), digest(token), RESET_TOKEN_EXPIRY_SECONDS)) {
            EmailUtil.sendPasswordResetEmail(user.getEmail(), token);
        }
    }

    public boolean isTokenValid(String token) {
        return token != null && !token.isBlank() && tokenDAO.isTokenValid(digest(token));
    }

    public boolean resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 8 || !isTokenValid(token)) {
            return false;
        }
        return tokenDAO.resetPassword(digest(token), PasswordUtil.hashPassword(newPassword));
    }

    private String digest(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
