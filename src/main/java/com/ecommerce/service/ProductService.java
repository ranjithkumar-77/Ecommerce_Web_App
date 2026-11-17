package com.ecommerce.service;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;

import java.util.List;

public class ProductService {

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    public List<Product> getAllProducts() {
        return productDAO.getAllActiveProducts();
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return productDAO.getProductsByCategory(categoryId);
    }

    public List<Product> getFilteredProducts(Integer categoryId, String keyword,
                                             Double minPrice, Double maxPrice, String sort) {
        return productDAO.getFilteredProducts(categoryId, keyword, minPrice, maxPrice, sort);
    }

    public Product getProductDetails(int productId) {
        Product product = productDAO.getProductById(productId);
        if (product == null) return null;
        product.setSizes(productDAO.getSizesByProductId(productId));
        product.setImages(productDAO.getImagesByProductId(productId));
        return product;
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDAO.searchProducts(keyword.trim());
    }

    public List<Product> getFeaturedProducts() {
        return productDAO.getFeaturedProducts(8);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.getAllActiveCategories();
    }
}
