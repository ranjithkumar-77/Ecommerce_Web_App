package com.ecommerce.dao;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public boolean updateItemQuantity(int cartId, int cartItemId, int quantity) {
        String lookup = "SELECT ci.product_id, ci.size_id, p.stock_quantity, p.is_size_available " +
                "FROM cart_items ci JOIN products p ON p.id=ci.product_id " +
                "WHERE ci.id=? AND ci.cart_id=? AND p.is_active=1";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int productId;
            int sizeId;
            int available;
            boolean sizeAvailable;
            try (PreparedStatement find = conn.prepareStatement(lookup)) {
                find.setInt(1, cartItemId);
                find.setInt(2, cartId);
                try (ResultSet rs = find.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    productId = rs.getInt("product_id");
                    sizeId = rs.getInt("size_id");
                    if (rs.wasNull()) sizeId = 0;
                    available = rs.getInt("stock_quantity");
                    sizeAvailable = rs.getBoolean("is_size_available");
                }
            }
            if (sizeAvailable) {
                try (PreparedStatement size = conn.prepareStatement(
                        "SELECT stock_quantity FROM product_sizes WHERE id=? AND product_id=? FOR UPDATE")) {
                    size.setInt(1, sizeId);
                    size.setInt(2, productId);
                    try (ResultSet rs = size.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        available = rs.getInt(1);
                    }
                }
            }
            if (quantity <= 0 || quantity > available) {
                conn.rollback();
                return false;
            }
            try (PreparedStatement update = conn.prepareStatement(
                    "UPDATE cart_items SET quantity=? WHERE id=? AND cart_id=?")) {
                update.setInt(1, quantity);
                update.setInt(2, cartItemId);
                update.setInt(3, cartId);
                boolean updated = update.executeUpdate() == 1;
                if (updated) conn.commit();
                else conn.rollback();
                return updated;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeItem(int cartId, int cartItemId) {
        String sql = "DELETE FROM cart_items WHERE id=? AND cart_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            ps.setInt(2, cartId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Cart getCartByUserId(int userId) {
        // Try to get existing cart
        String selectSql = "SELECT * FROM carts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCart(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // Create cart if not exists
        String insertSql = "INSERT INTO carts (user_id) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Cart cart = new Cart();
                    cart.setId(keys.getInt(1));
                    cart.setUserId(userId);
                    return cart;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addItemToCart(int cartId, int productId, int sizeId, int quantity) {
        if (quantity <= 0) return false;
        String productSql = "SELECT stock_quantity, is_size_available FROM products " +
                "WHERE id=? AND is_active=1 FOR UPDATE";
        String sizeSql = "SELECT stock_quantity FROM product_sizes WHERE id=? AND product_id=? FOR UPDATE";
        String cartItemSql = "SELECT id, quantity FROM cart_items WHERE cart_id=? AND product_id=? " +
                "AND (size_id=? OR (size_id IS NULL AND ?=0)) FOR UPDATE";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int available;
            boolean sizeAvailable;
            try (PreparedStatement product = conn.prepareStatement(productSql)) {
                product.setInt(1, productId);
                try (ResultSet rs = product.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    available = rs.getInt("stock_quantity");
                    sizeAvailable = rs.getBoolean("is_size_available");
                }
            }
            if (sizeAvailable) {
                if (sizeId <= 0) {
                    conn.rollback();
                    return false;
                }
                try (PreparedStatement size = conn.prepareStatement(sizeSql)) {
                    size.setInt(1, sizeId);
                    size.setInt(2, productId);
                    try (ResultSet rs = size.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        available = rs.getInt("stock_quantity");
                    }
                }
            } else if (sizeId > 0) {
                conn.rollback();
                return false;
            }

            int itemId = 0;
            int currentQuantity = 0;
            try (PreparedStatement check = conn.prepareStatement(cartItemSql)) {
                check.setInt(1, cartId);
                check.setInt(2, productId);
                check.setInt(3, sizeId);
                check.setInt(4, sizeId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        itemId = rs.getInt("id");
                        currentQuantity = rs.getInt("quantity");
                    }
                }
            }
            int newQuantity = currentQuantity + quantity;
            if (newQuantity > available) {
                conn.rollback();
                return false;
            }
            if (itemId > 0) {
                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE cart_items SET quantity=? WHERE id=? AND cart_id=?")) {
                    update.setInt(1, newQuantity);
                    update.setInt(2, itemId);
                    update.setInt(3, cartId);
                    update.executeUpdate();
                }
            } else {
                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO cart_items (cart_id, product_id, size_id, quantity) VALUES (?, ?, ?, ?)")) {
                    insert.setInt(1, cartId);
                    insert.setInt(2, productId);
                    if (sizeId == 0) insert.setNull(3, Types.INTEGER);
                    else insert.setInt(3, sizeId);
                    insert.setInt(4, quantity);
                    insert.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCartItemQuantity(int cartItemId, int quantity) {
        String sql = "UPDATE cart_items SET quantity=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeCartItem(int cartItemId) {
        String sql = "DELETE FROM cart_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clearCart(int cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<CartItem> getCartItemsWithDetails(int cartId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, p.name AS product_name, p.price AS product_price, " +
                     "p.discount_percent, p.main_image_url AS product_image, p.color AS product_color, " +
                     "ps.size_label " +
                     "FROM cart_items ci " +
                     "JOIN products p ON ci.product_id = p.id " +
                     "LEFT JOIN product_sizes ps ON ci.size_id = ps.id " +
                     "WHERE ci.cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setCartId(rs.getInt("cart_id"));
                    item.setProductId(rs.getInt("product_id"));
                    int sizeId = rs.getInt("size_id");
                    item.setSizeId(rs.wasNull() ? 0 : sizeId);
                    item.setQuantity(rs.getInt("quantity"));
                    item.setAddedAt(rs.getTimestamp("added_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductPrice(rs.getDouble("product_price"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setProductColor(rs.getString("product_color"));
                    item.setSizeLabel(rs.getString("size_label"));
                    double price = rs.getDouble("product_price");
                    double discount = rs.getDouble("discount_percent");
                    item.setDiscountedPrice(price * (1 - discount / 100.0));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public int getCartItemCount(int userId) {
        String sql = "SELECT SUM(ci.quantity) FROM cart_items ci " +
                     "JOIN carts c ON ci.cart_id = c.id WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Cart mapCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setId(rs.getInt("id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setCreatedAt(rs.getTimestamp("created_at"));
        cart.setUpdatedAt(rs.getTimestamp("updated_at"));
        return cart;
    }
}
