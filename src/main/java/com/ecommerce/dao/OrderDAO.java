package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatusUpdate;
import com.ecommerce.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrderWithItems(Order order, List<CartItem> cartItems) {
        String insertOrder = "INSERT INTO orders (user_id, coupon_id, subtotal, discount_amount, " +
                "shipping_charge, total_amount, shipping_name, shipping_phone, shipping_address_line1, " +
                "shipping_address_line2, shipping_city, shipping_state, shipping_pincode, shipping_country, " +
                "status, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItem = "INSERT INTO order_items (order_id, product_id, size_id, product_name, " +
                "product_color, size_label, unit_price, quantity, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertHistory = "INSERT INTO order_status_history (order_id, status, remarks) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            for (CartItem item : cartItems) {
                if (!reserveStock(conn, item)) {
                    conn.rollback();
                    return -1;
                }
            }

            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                if (order.getCouponId() == 0) ps.setNull(2, Types.INTEGER);
                else ps.setInt(2, order.getCouponId());
                ps.setDouble(3, order.getSubtotal());
                ps.setDouble(4, order.getDiscountAmount());
                ps.setDouble(5, order.getShippingCharge());
                ps.setDouble(6, order.getTotalAmount());
                ps.setString(7, order.getShippingName());
                ps.setString(8, order.getShippingPhone());
                ps.setString(9, order.getShippingAddressLine1());
                ps.setString(10, order.getShippingAddressLine2());
                ps.setString(11, order.getShippingCity());
                ps.setString(12, order.getShippingState());
                ps.setString(13, order.getShippingPincode());
                ps.setString(14, order.getShippingCountry());
                ps.setString(15, order.getStatus());
                ps.setString(16, order.getPaymentStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return -1;
                    }
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (CartItem item : cartItems) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getProductId());
                    if (item.getSizeId() == 0) ps.setNull(3, Types.INTEGER);
                    else ps.setInt(3, item.getSizeId());
                    ps.setString(4, item.getProductName());
                    ps.setString(5, item.getProductColor());
                    ps.setString(6, item.getSizeLabel());
                    ps.setDouble(7, item.getDiscountedPrice());
                    ps.setInt(8, item.getQuantity());
                    ps.setDouble(9, item.getItemTotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            try (PreparedStatement ps = conn.prepareStatement(insertHistory)) {
                ps.setInt(1, orderId);
                ps.setString(2, order.getStatus());
                ps.setString(3, "Order placed");
                ps.executeUpdate();
            }
            conn.commit();
            order.setId(orderId);
            return orderId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private boolean reserveStock(Connection conn, CartItem item) throws SQLException {
        if (item.getQuantity() <= 0) return false;
        int stock;
        boolean hasSizes;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT stock_quantity, is_size_available FROM products WHERE id=? AND is_active=1 FOR UPDATE")) {
            ps.setInt(1, item.getProductId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                stock = rs.getInt("stock_quantity");
                hasSizes = rs.getBoolean("is_size_available");
            }
        }
        if (hasSizes) {
            if (item.getSizeId() <= 0) return false;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT stock_quantity FROM product_sizes WHERE id=? AND product_id=? FOR UPDATE")) {
                ps.setInt(1, item.getSizeId());
                ps.setInt(2, item.getProductId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) < item.getQuantity()) return false;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE product_sizes SET stock_quantity=stock_quantity-? WHERE id=? AND product_id=?")) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, item.getSizeId());
                ps.setInt(3, item.getProductId());
                if (ps.executeUpdate() != 1) return false;
            }
        } else if (item.getSizeId() != 0 || stock < item.getQuantity()) {
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET stock_quantity=stock_quantity-? WHERE id=? AND stock_quantity>=?")) {
            ps.setInt(1, item.getQuantity());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            return ps.executeUpdate() == 1;
        }
    }

    public int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, coupon_id, subtotal, discount_amount, " +
                     "shipping_charge, total_amount, shipping_name, shipping_phone, " +
                     "shipping_address_line1, shipping_address_line2, shipping_city, " +
                     "shipping_state, shipping_pincode, shipping_country, status, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            if (order.getCouponId() == 0) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, order.getCouponId());
            }
            ps.setDouble(3, order.getSubtotal());
            ps.setDouble(4, order.getDiscountAmount());
            ps.setDouble(5, order.getShippingCharge());
            ps.setDouble(6, order.getTotalAmount());
            ps.setString(7, order.getShippingName());
            ps.setString(8, order.getShippingPhone());
            ps.setString(9, order.getShippingAddressLine1());
            ps.setString(10, order.getShippingAddressLine2());
            ps.setString(11, order.getShippingCity());
            ps.setString(12, order.getShippingState());
            ps.setString(13, order.getShippingPincode());
            ps.setString(14, order.getShippingCountry());
            ps.setString(15, order.getStatus() != null ? order.getStatus() : "PENDING");
            ps.setString(16, order.getPaymentStatus() != null ? order.getPaymentStatus() : "PENDING");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int orderId = keys.getInt(1);
                    order.setId(orderId);
                    return orderId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean createOrderItems(int orderId, List<CartItem> cartItems) {
        String sql = "INSERT INTO order_items (order_id, product_id, size_id, product_name, " +
                     "product_color, size_label, unit_price, quantity, total_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartItem item : cartItems) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getProductId());
                if (item.getSizeId() == 0) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, item.getSizeId());
                }
                ps.setString(4, item.getProductName());
                ps.setString(5, item.getProductColor());
                ps.setString(6, item.getSizeLabel());
                ps.setDouble(7, item.getDiscountedPrice());
                ps.setInt(8, item.getQuantity());
                ps.setDouble(9, item.getItemTotal());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.main_image_url FROM order_items oi " +
                     "LEFT JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setSizeId(rs.getInt("size_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductColor(rs.getString("product_color"));
                    item.setSizeLabel(rs.getString("size_label"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setTotalPrice(rs.getDouble("total_price"));
                    item.setMainImageUrl(rs.getString("main_image_url"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean addStatusHistory(int orderId, String status, String remarks) {
        String sql = "INSERT INTO order_status_history (order_id, status, remarks) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, status);
            ps.setString(3, remarks);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OrderStatusUpdate> getOrderStatusHistory(int orderId) {
        List<OrderStatusUpdate> history = new ArrayList<>();
        String sql = "SELECT status, remarks, changed_at FROM order_status_history " +
                "WHERE order_id=? ORDER BY changed_at, id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderStatusUpdate update = new OrderStatusUpdate();
                    update.setStatus(rs.getString("status"));
                    update.setRemarks(rs.getString("remarks"));
                    update.setChangedAt(rs.getTimestamp("changed_at"));
                    history.add(update);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setCouponId(rs.getInt("coupon_id"));
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setShippingCharge(rs.getDouble("shipping_charge"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setShippingName(rs.getString("shipping_name"));
        order.setShippingPhone(rs.getString("shipping_phone"));
        order.setShippingAddressLine1(rs.getString("shipping_address_line1"));
        order.setShippingAddressLine2(rs.getString("shipping_address_line2"));
        order.setShippingCity(rs.getString("shipping_city"));
        order.setShippingState(rs.getString("shipping_state"));
        order.setShippingPincode(rs.getString("shipping_pincode"));
        order.setShippingCountry(rs.getString("shipping_country"));
        order.setStatus(rs.getString("status"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        return order;
    }
}
