package com.android.retail.hawk;

public class OrdersModel {

    private int productImage;
    private String productName;
    private String deliveryStatus;

    public OrdersModel(int productImage, String productName, String deliveryStatus) {
        this.productImage = productImage;
        this.productName = productName;
        this.deliveryStatus = deliveryStatus;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
