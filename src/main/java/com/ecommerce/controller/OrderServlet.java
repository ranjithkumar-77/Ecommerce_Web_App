package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatusUpdate;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
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

        String path = request.getServletPath();
        String orderIdParam = request.getParameter("orderId");
        if ("/order-confirmation".equals(path) || "/order-tracking".equals(path)) {
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
                request.setAttribute("order", order);
                if ("/order-confirmation".equals(path)) {
                    if (order.getPayment() == null) {
                        response.sendRedirect(request.getContextPath() + "/payment?orderId=" + orderId);
                        return;
                    }
                    request.getRequestDispatcher("/WEB-INF/views/orders/order-confirmation.jsp")
                            .forward(request, response);
                } else {
                    List<OrderStatusUpdate> history =
                            orderService.getOrderStatusHistory(orderId, loggedUser.getId());
                    request.setAttribute("history", history);
                    request.getRequestDispatcher("/WEB-INF/views/orders/order-tracking.jsp")
                            .forward(request, response);
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
            return;
        }

        if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
            try {
                int orderId = Integer.parseInt(orderIdParam.trim());
                if (orderService.getOrderDetails(orderId, loggedUser.getId()) == null) {
                    response.sendRedirect(request.getContextPath() + "/orders");
                } else {
                    response.sendRedirect(request.getContextPath() + "/order-tracking?orderId=" + orderId);
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
        } else {
            List<Order> orders = orderService.getUserOrders(loggedUser.getId());
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/orders/my-orders.jsp").forward(request, response);
        }
    }
}
