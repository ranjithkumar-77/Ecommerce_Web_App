package com.ecommerce.controller;

import com.google.gson.Gson;
import com.ecommerce.model.Cart;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (session.getAttribute("cartError") != null) {
            request.setAttribute("errorMessage", session.getAttribute("cartError"));
            session.removeAttribute("cartError");
        }
        Cart cart = cartService.getCartWithDetails(loggedUser.getId());
        request.setAttribute("cart", cart);
        if (cart != null && cart.getItems() != null) {
            double subtotal = cartService.calculateSubtotal(cart.getItems());
            request.setAttribute("subtotal", subtotal);
        }
        request.getRequestDispatcher("/WEB-INF/views/cart/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        HttpSession session = request.getSession(false);
        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (loggedUser == null) {
            if (ajax) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(Map.of(
                        "success", false,
                        "message", "Please sign in to add items to your cart.",
                        "redirect", request.getContextPath() + "/login")));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        boolean updated = false;

        if ("add".equals(action)) {
            int productId = parseIntParam(request.getParameter("productId"), 0);
            int sizeId = parseIntParam(request.getParameter("sizeId"), 0);
            int quantity = parseIntParam(request.getParameter("quantity"), 1);
            updated = cartService.addToCart(loggedUser.getId(), productId, sizeId, quantity);
        } else if ("update".equals(action)) {
            int cartItemId = parseIntParam(request.getParameter("cartItemId"), 0);
            int quantity = parseIntParam(request.getParameter("quantity"), 1);
            updated = cartService.updateQuantity(loggedUser.getId(), cartItemId, quantity);
        } else if ("remove".equals(action)) {
            int cartItemId = parseIntParam(request.getParameter("cartItemId"), 0);
            updated = cartService.removeFromCart(loggedUser.getId(), cartItemId);
        }

        if (!updated && !ajax) {
            session.setAttribute("cartError", "The cart could not be updated. Check product availability and quantity, then try again.");
        }
        if (ajax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (!updated) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new Gson().toJson(Map.of(
                        "success", false,
                        "message", "The cart could not be updated. Check product availability and quantity.")));
            } else if ("add".equals(action)) {
                response.getWriter().write(new Gson().toJson(Map.of(
                        "success", true,
                        "redirect", request.getContextPath() + "/cart")));
            } else {
                response.getWriter().write(new Gson().toJson(Map.of("success", true, "reload", true)));
            }
            return;
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private int parseIntParam(String param, int defaultValue) {
        if (param == null) return defaultValue;
        try {
            return Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
