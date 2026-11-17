package com.ecommerce.model;

import java.sql.Timestamp;

public class CartItem {

    private int id;
    private int cartId;
    private int productId;
    private int sizeId; // 0 if no size
    private int quantity;
    private Timestamp addedAt;

    // Transient fields
    private String productName;
    private double productPrice;
    private String productImage;
    private String sizeLabel;
    private double discountedPrice;
    private String productColor;

    public CartItem() {}

    public double getItemTotal() {
        return discountedPrice * quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSizeId() { return sizeId; }
    public void setSizeId(int sizeId) { this.sizeId = sizeId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public String getProductColor() { return productColor; }
    public void setProductColor(String productColor) { this.productColor = productColor; }

    @Override
    public String toString() {
        return "CartItem{id=" + id + ", productId=" + productId + ", quantity=" + quantity +
               ", discountedPrice=" + discountedPrice + "}";
    }
}
