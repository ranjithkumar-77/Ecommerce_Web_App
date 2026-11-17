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

@WebServlet("/address")
public class AddressServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedUser(request, response);
        if (user == null) return;
        if ("/address-selection".equals(request.getServletPath()) && !hasSavedAddress(user)) {
            response.sendRedirect(request.getContextPath() + "/address?flow=checkout");
            return;
        }
        if (user.getCountry() == null || user.getCountry().isBlank()) {
            user.setCountry("India");
        }
        request.setAttribute("user", user);
        String view = "/address-selection".equals(request.getServletPath())
                ? "/WEB-INF/views/orders/address-selection.jsp"
                : "/WEB-INF/views/account/address.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getLoggedUser(request, response);
        if (user == null) return;

        String line1 = request.getParameter("addressLine1");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");
        if (line1 == null || line1.isBlank() || city == null || city.isBlank()
                || state == null || state.isBlank() || pincode == null || pincode.isBlank()) {
            request.setAttribute("errorMessage", "Address, city, state, and postal code are required.");
        } else if (userService.updateAddress(user.getId(), line1.trim(),
                value(request.getParameter("addressLine2")), city.trim(), state.trim(),
                pincode.trim(), countryValue(request.getParameter("country")))) {
            user.setAddressLine1(line1.trim());
            user.setAddressLine2(value(request.getParameter("addressLine2")));
            user.setCity(city.trim());
            user.setState(state.trim());
            user.setPincode(pincode.trim());
            user.setCountry(countryValue(request.getParameter("country")));
            request.getSession(false).setAttribute("loggedUser", user);
            if ("checkout".equals(request.getParameter("flow"))) {
                response.sendRedirect(request.getContextPath() + "/address-selection");
                return;
            }
            request.setAttribute("successMessage", "Address saved.");
        } else {
            request.setAttribute("errorMessage", "Unable to save the address. Please try again.");
        }
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/account/address.jsp").forward(request, response);
    }

    private boolean hasSavedAddress(User user) {
        return user.getAddressLine1() != null && !user.getAddressLine1().isBlank()
                && user.getCity() != null && !user.getCity().isBlank()
                && user.getState() != null && !user.getState().isBlank()
                && user.getPincode() != null && !user.getPincode().isBlank();
    }

    private User getLoggedUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        return user;
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }

    private String countryValue(String text) {
        String country = value(text);
        return country.isEmpty() ? "India" : country;
    }
}
