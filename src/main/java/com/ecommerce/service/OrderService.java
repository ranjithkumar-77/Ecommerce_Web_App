package com.ecommerce.service;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.CouponDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.PaymentDAO;
import com.ecommerce.model.Cart;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;

import java.util.List;

public class OrderService {

    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private CouponDAO couponDAO = new CouponDAO();

    public int placeOrder(int userId, Cart cart, Coupon coupon, User user) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) return -1;
        if (user == null || user.getAddressLine1() == null || user.getAddressLine1().isBlank()
                || user.getCity() == null || user.getCity().isBlank()
                || user.getState() == null || user.getState().isBlank()
                || user.getPincode() == null || user.getPincode().isBlank()) return -1;

        CartService cartService = new CartService();
        double subtotal = cartService.calculateSubtotal(cart.getItems());
        double discountAmount = 0.0;
        int couponId = 0;
        if (coupon != null) {
            CouponService couponService = new CouponService();
            discountAmount = couponService.calculateDiscount(coupon, subtotal);
            couponId = coupon.getId();
        }
        double shippingCharge = subtotal >= 500.0 ? 0.0 : 50.0;
        double totalAmount = subtotal - discountAmount + shippingCharge;

        Order order = new Order();
        order.setUserId(userId);
        order.setCouponId(couponId);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setShippingCharge(shippingCharge);
        order.setTotalAmount(totalAmount);
        order.setShippingName(user.getFirstName() + " " + user.getLastName());
        order.setShippingPhone(user.getPhone());
        order.setShippingAddressLine1(user.getAddressLine1());
        order.setShippingAddressLine2(user.getAddressLine2());
        order.setShippingCity(user.getCity());
        order.setShippingState(user.getState());
        order.setShippingPincode(user.getPincode());
        order.setShippingCountry(user.getCountry() == null || user.getCountry().isBlank()
                ? "India" : user.getCountry());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        int orderId = orderDAO.createOrderWithItems(order, cart.getItems());
        if (orderId <= 0) return -1;

        // Increment coupon usage
        if (couponId > 0) {
            couponDAO.incrementUsedCount(couponId);
        }

        // Clear cart
        cartDAO.clearCart(cart.getId());

        return orderId;
    }

    public Order getOrderDetails(int orderId, int userId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null || order.getUserId() != userId) return null;
        order.setItems(orderDAO.getOrderItems(orderId));
        order.setPayment(new PaymentDAO().getPaymentByOrderId(orderId));
        return order;
    }

    public List<Order> getUserOrders(int userId) {
        return orderDAO.getOrdersByUserId(userId);
    }

    public List<com.ecommerce.model.OrderStatusUpdate> getOrderStatusHistory(int orderId, int userId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null || order.getUserId() != userId) return List.of();
        return orderDAO.getOrderStatusHistory(orderId);
    }

    public boolean cancelOrder(int orderId, int userId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null || order.getUserId() != userId) return false;
        if ("SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) return false;
        return orderDAO.updateOrderStatus(orderId, "CANCELLED");
    }
}
