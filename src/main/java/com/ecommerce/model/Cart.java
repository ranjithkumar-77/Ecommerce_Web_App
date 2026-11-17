package com.ecommerce.model;

import java.sql.Timestamp;
import java.util.List;

public class Cart {

    private int id;
    private int userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient
    private List<CartItem> items;

    public Cart() {}

    public double getTotal() {
        if (items == null) return 0.0;
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getItemTotal();
        }
        return total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Cart{id=" + id + ", userId=" + userId + ", itemCount=" +
               (items != null ? items.size() : 0) + "}";
    }
}
