package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("q");
        Double minPrice = parsePrice(request.getParameter("minPrice"));
        Double maxPrice = parsePrice(request.getParameter("maxPrice"));
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            request.setAttribute("errorMessage", "Minimum price must not exceed maximum price.");
            minPrice = null;
            maxPrice = null;
        }
        String sort = request.getParameter("sort");
        List<Product> results = productService.getFilteredProducts(null, keyword, minPrice, maxPrice, sort);
        request.setAttribute("searchResults", results);
        request.setAttribute("keyword", keyword);
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("sort", sort);
        request.getRequestDispatcher("/WEB-INF/views/products/search-results.jsp").forward(request, response);
    }

    private Double parsePrice(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            double price = Double.parseDouble(value.trim());
            return Double.isFinite(price) && price >= 0 ? price : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
