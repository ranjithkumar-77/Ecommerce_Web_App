package com.ecommerce.model;

import java.sql.Timestamp;

public class OrderStatusUpdate {

    private String status;
    private String remarks;
    private Timestamp changedAt;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Timestamp getChangedAt() { return changedAt; }
    public void setChangedAt(Timestamp changedAt) { this.changedAt = changedAt; }
}
