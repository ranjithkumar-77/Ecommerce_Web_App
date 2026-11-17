package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private OrderService orderService = new OrderService();
    private PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdParam.trim());
            Order order = orderService.getOrderDetails(orderId, loggedUser.getId());
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/orders/payment.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders");
        }
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

        String orderIdParam = request.getParameter("orderId");
        String paymentMethod = request.getParameter("paymentMethod");
        if (orderIdParam == null || orderIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdParam.trim());
            Order order = orderService.getOrderDetails(orderId, loggedUser.getId());
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }
            if (!paymentService.processPayment(orderId, paymentMethod, order.getTotalAmount())) {
                request.setAttribute("order", order);
                request.setAttribute("errorMessage", "Unable to confirm this payment method. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/orders/payment.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/order-confirmation?orderId=" + orderId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }
}
