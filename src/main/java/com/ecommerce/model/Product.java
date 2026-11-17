package com.ecommerce.model;

import java.sql.Timestamp;
import java.util.List;

public class Product {

    private int id;
    private int categoryId;
    private String name;
    private String description;
    private String brand;
    private double price;
    private double discountPercent;
    private String color;
    private boolean isSizeAvailable;
    private int stockQuantity;
    private String mainImageUrl;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient fields
    private String categoryName;
    private List<ProductImage> images;
    private List<ProductSize> sizes;

    public Product() {}

    public double getDiscountedPrice() {
        return price * (1 - discountPercent / 100.0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isSizeAvailable() { return isSizeAvailable; }
    public void setSizeAvailable(boolean sizeAvailable) { isSizeAvailable = sizeAvailable; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    public List<ProductSize> getSizes() { return sizes; }
    public void setSizes(List<ProductSize> sizes) { this.sizes = sizes; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', brand='" + brand +
               "', price=" + price + ", discountPercent=" + discountPercent +
               ", stockQuantity=" + stockQuantity + ", isActive=" + isActive + "}";
    }
}
