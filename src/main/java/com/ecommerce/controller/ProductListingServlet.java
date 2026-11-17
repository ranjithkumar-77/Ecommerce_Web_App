package com.ecommerce.controller;

import com.ecommerce.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProductListingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer categoryId = parseInteger(request.getParameter("categoryId"));
        Double minPrice = parsePrice(request.getParameter("minPrice"));
        Double maxPrice = parsePrice(request.getParameter("maxPrice"));
        if (request.getParameter("categoryId") != null && categoryId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid category.");
            return;
        }

        String error = null;
        if (hasValue(request.getParameter("minPrice")) && minPrice == null
                || hasValue(request.getParameter("maxPrice")) && maxPrice == null) {
            error = "Enter a valid non-negative price.";
            minPrice = null;
            maxPrice = null;
        } else if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            error = "Minimum price must not exceed maximum price.";
            minPrice = null;
            maxPrice = null;
        }
        String sort = request.getParameter("sort");
        request.setAttribute("products",
                productService.getFilteredProducts(categoryId, null, minPrice, maxPrice, sort));
        request.setAttribute("allCategories", productService.getAllCategories());
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("sort", sort);
        request.setAttribute("errorMessage", error);
        request.getRequestDispatcher("/WEB-INF/views/products/product-listing.jsp").forward(request, response);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parsePrice(String value) {
        if (!hasValue(value)) return null;
        try {
            double price = Double.parseDouble(value.trim());
            return Double.isFinite(price) && price >= 0 ? price : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }
}
