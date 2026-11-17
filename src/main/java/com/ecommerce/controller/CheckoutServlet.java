package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CouponService;
import com.ecommerce.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CartService cartService = new CartService();
    private CouponService couponService = new CouponService();
    private OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!hasSavedAddress(loggedUser)) {
            response.sendRedirect(request.getContextPath() + "/address-selection");
            return;
        }

        Cart cart = cartService.getCartWithDetails(loggedUser.getId());
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double subtotal = cartService.calculateSubtotal(cart.getItems());
        double shippingCharge = subtotal >= 500.0 ? 0.0 : 50.0;
        String couponCode = request.getParameter("couponCode");
        Coupon coupon = null;
        double discountAmount = 0.0;
        if (couponCode != null && !couponCode.isBlank()) {
            coupon = couponService.validateCoupon(couponCode.trim(), subtotal);
            if (coupon == null) {
                request.setAttribute("errorMessage", "Invalid or expired coupon code.");
            } else {
                discountAmount = couponService.calculateDiscount(coupon, subtotal);
            }
        }

        request.setAttribute("cart", cart);
        request.setAttribute("user", loggedUser);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("shippingCharge", shippingCharge);
        request.setAttribute("couponCode", couponCode);
        request.setAttribute("discountAmount", discountAmount);
        request.setAttribute("totalAmount", subtotal - discountAmount + shippingCharge);
        request.getRequestDispatcher("/WEB-INF/views/orders/order-summary.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!hasSavedAddress(loggedUser)) {
            response.sendRedirect(request.getContextPath() + "/address-selection");
            return;
        }

        Cart cart = cartService.getCartWithDetails(loggedUser.getId());
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double subtotal = cartService.calculateSubtotal(cart.getItems());
        double shippingCharge = subtotal >= 500.0 ? 0.0 : 50.0;

        // Apply coupon if provided
        String couponCode = request.getParameter("couponCode");
        Coupon coupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            coupon = couponService.validateCoupon(couponCode.trim(), subtotal);
            if (coupon == null) {
                request.setAttribute("errorMessage", "Invalid or expired coupon code.");
                request.setAttribute("cart", cart);
                request.setAttribute("user", loggedUser);
                request.setAttribute("subtotal", subtotal);
                request.setAttribute("shippingCharge", shippingCharge);
                request.setAttribute("couponCode", couponCode);
                request.setAttribute("discountAmount", 0.0);
                request.setAttribute("totalAmount", subtotal + shippingCharge);
                request.getRequestDispatcher("/WEB-INF/views/orders/order-summary.jsp").forward(request, response);
                return;
            }
        }

        int orderId = orderService.placeOrder(loggedUser.getId(), cart, coupon, loggedUser);

        if (orderId > 0) {
            response.sendRedirect(request.getContextPath() + "/payment?orderId=" + orderId);
        } else {
            request.setAttribute("errorMessage", "Failed to place order. Please try again.");
            request.setAttribute("cart", cart);
            request.setAttribute("user", loggedUser);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("shippingCharge", shippingCharge);
            request.setAttribute("couponCode", couponCode);
            request.setAttribute("discountAmount", coupon == null
                    ? 0.0 : couponService.calculateDiscount(coupon, subtotal));
            request.setAttribute("totalAmount", subtotal
                    - (coupon == null ? 0.0 : couponService.calculateDiscount(coupon, subtotal))
                    + shippingCharge);
            request.getRequestDispatcher("/WEB-INF/views/orders/order-summary.jsp").forward(request, response);
        }
    }

    private boolean hasSavedAddress(User user) {
        return user.getAddressLine1() != null && !user.getAddressLine1().isBlank()
                && user.getCity() != null && !user.getCity().isBlank()
                && user.getState() != null && !user.getState().isBlank()
                && user.getPincode() != null && !user.getPincode().isBlank();
    }
}
