package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("authPage", Boolean.TRUE);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("authPage", Boolean.TRUE);
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");

        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 8) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            User user = userService.registerUser(firstName, lastName, email, password, phone);
            if (user == null) {
                request.setAttribute("errorMessage", "Enter a valid name, email address, and password.");
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                request.setAttribute("errorMessage", "An account with this email already exists. Sign in or use another email.");
            } else if (e.getMessage() != null && e.getMessage().startsWith("Set DB_URL")) {
                request.setAttribute("errorMessage",
                        "Account creation is unavailable: configure DB_URL, DB_USER, and DB_PASSWORD for Tomcat, then restart it.");
            } else {
                LOGGER.log(Level.SEVERE, "Unable to create customer account.", e);
                request.setAttribute("errorMessage",
                        "Account creation is unavailable because the database connection or schema needs attention. Check the Tomcat log.");
            }
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}
