package com.ecommerce.controller;

import com.ecommerce.service.PasswordResetService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PasswordResetService passwordResetService = new PasswordResetService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        request.setAttribute("token", token);
        if (!passwordResetService.isTokenValid(token)) {
            request.setAttribute("errorMessage", "This reset link is invalid or expired.");
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        request.setAttribute("token", token);
        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "The passwords do not match.");
        } else if (passwordResetService.resetPassword(token, password)) {
            response.sendRedirect(request.getContextPath() + "/login?reset=success");
            return;
        } else {
            request.setAttribute("errorMessage",
                    "The reset link is invalid, expired, or the password is too short (minimum 8 characters).");
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(request, response);
    }
}
