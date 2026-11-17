package com.ecommerce.model;

public class ProductSize {

    private int id;
    private int productId;
    private String sizeLabel;
    private int stockQuantity;

    public ProductSize() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public String toString() {
        return "ProductSize{id=" + id + ", productId=" + productId +
               ", sizeLabel='" + sizeLabel + "', stockQuantity=" + stockQuantity + "}";
    }
}
