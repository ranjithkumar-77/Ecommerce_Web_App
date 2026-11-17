package com.ecommerce.model;

public class ProductImage {

    private int id;
    private int productId;
    private String imageUrl;
    private int displayOrder;

    public ProductImage() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    @Override
    public String toString() {
        return "ProductImage{id=" + id + ", productId=" + productId +
               ", imageUrl='" + imageUrl + "', displayOrder=" + displayOrder + "}";
    }
}
