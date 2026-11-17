package com.ecommerce.service;

import com.ecommerce.dao.PaymentDAO;
import com.ecommerce.model.Payment;

public class PaymentService {

    private PaymentDAO paymentDAO = new PaymentDAO();

    public boolean processPayment(int orderId, String method, double amount) {
        if (orderId <= 0 || amount <= 0 || !"COD".equalsIgnoreCase(method)) {
            return false;
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentMethod("COD");
        payment.setAmount(amount);
        payment.setStatus("PENDING");

        Payment existing = paymentDAO.getPaymentByOrderId(orderId);
        if (existing != null) {
            return paymentDAO.updatePaymentStatus(existing.getId(), "PENDING", null)
                    && paymentDAO.updateOrderPaymentStatus(orderId, "PENDING");
        }
        return paymentDAO.createPayment(payment)
                && paymentDAO.updateOrderPaymentStatus(orderId, "PENDING");
    }
}
