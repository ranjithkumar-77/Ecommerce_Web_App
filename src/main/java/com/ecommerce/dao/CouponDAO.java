package com.ecommerce.dao;

import com.ecommerce.model.Coupon;
import com.ecommerce.util.DBConnection;

import java.sql.*;

public class CouponDAO {

    public Coupon getCouponByCode(String code) {
        String sql = "SELECT * FROM coupons WHERE code = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCoupon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean incrementUsedCount(int couponId) {
        String sql = "UPDATE coupons SET used_count = used_count + 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Coupon mapCoupon(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setDiscountType(rs.getString("discount_type"));
        c.setDiscountValue(rs.getDouble("discount_value"));
        c.setMinOrderValue(rs.getDouble("min_order_value"));
        c.setMaxUses(rs.getInt("max_uses"));
        c.setUsedCount(rs.getInt("used_count"));
        c.setValidFrom(rs.getDate("valid_from"));
        c.setValidUntil(rs.getDate("valid_until"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }
}
