package com.ecommerce.service;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;

import java.util.List;

public class CartService {

    private CartDAO cartDAO = new CartDAO();

    public Cart getCartWithDetails(int userId) {
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart != null) {
            cart.setItems(cartDAO.getCartItemsWithDetails(cart.getId()));
        }
        return cart;
    }

    public boolean addToCart(int userId, int productId, int sizeId, int quantity) {
        if (quantity <= 0) return false;
        // Ensure cart exists for user
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) return false;
        return cartDAO.addItemToCart(cart.getId(), productId, sizeId, quantity);
    }

    public boolean updateQuantity(int userId, int cartItemId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(userId, cartItemId);
        }
        // Verify item belongs to user's cart
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) return false;
        return cartDAO.updateItemQuantity(cart.getId(), cartItemId, quantity);
    }

    public boolean removeFromCart(int userId, int cartItemId) {
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) return false;
        return cartDAO.removeItem(cart.getId(), cartItemId);
    }

    public double calculateSubtotal(List<CartItem> items) {
        double subtotal = 0.0;
        if (items == null) return subtotal;
        for (CartItem item : items) {
            subtotal += item.getItemTotal();
        }
        return subtotal;
    }

    public int getCartItemCount(int userId) {
        return cartDAO.getCartItemCount(userId);
    }
}
