package com.ecommerce.service;

import com.ecommerce.dao.CouponDAO;
import com.ecommerce.model.Coupon;

import java.time.LocalDate;

public class CouponService {

    private CouponDAO couponDAO = new CouponDAO();

    public Coupon validateCoupon(String code, double orderSubtotal) {
        if (code == null || code.trim().isEmpty()) return null;
        Coupon coupon = couponDAO.getCouponByCode(code.trim().toUpperCase());
        if (coupon == null) return null;
        if (!coupon.isActive()) return null;
        LocalDate today = LocalDate.now();
        if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom().toLocalDate())) return null;
        if (coupon.getValidUntil() != null && today.isAfter(coupon.getValidUntil().toLocalDate())) return null;
        if (coupon.getMaxUses() > 0 && coupon.getUsedCount() >= coupon.getMaxUses()) return null;
        if (orderSubtotal < coupon.getMinOrderValue()) return null;
        return coupon;
    }

    public double calculateDiscount(Coupon coupon, double subtotal) {
        if (coupon == null) return 0.0;
        double discount = 0.0;
        if ("PERCENT".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = subtotal * coupon.getDiscountValue() / 100.0;
        } else if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }
        // Discount cannot exceed subtotal
        if (discount > subtotal) discount = subtotal;
        return discount;
    }
}
