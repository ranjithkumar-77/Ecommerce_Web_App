package com.ecommerce.dao;

import com.ecommerce.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PasswordResetDAO {

    public boolean createToken(int userId, String token, int expirySeconds) {
        String sql = "INSERT INTO password_reset_tokens (user_id, token, expiry_time) " +
                "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? SECOND))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setInt(3, expirySeconds);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create password reset token", e);
        }
    }

    public boolean isTokenValid(String token) {
        String sql = "SELECT 1 FROM password_reset_tokens WHERE token=? AND used=0 AND expiry_time > NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to validate password reset token", e);
        }
    }

    public boolean resetPassword(String token, String passwordHash) {
        String findSql = "SELECT id, user_id FROM password_reset_tokens " +
                "WHERE token=? AND used=0 AND expiry_time > NOW() FOR UPDATE";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement find = conn.prepareStatement(findSql)) {
                find.setString(1, token);
                try (ResultSet rs = find.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    int tokenId = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    try (PreparedStatement updateUser = conn.prepareStatement(
                                 "UPDATE users SET password_hash=?, updated_at=NOW() WHERE id=?");
                         PreparedStatement markUsed = conn.prepareStatement(
                                 "UPDATE password_reset_tokens SET used=1 WHERE id=?")) {
                        updateUser.setString(1, passwordHash);
                        updateUser.setInt(2, userId);
                        if (updateUser.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                        markUsed.setInt(1, tokenId);
                        if (markUsed.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to reset password", e);
        }
    }
}
