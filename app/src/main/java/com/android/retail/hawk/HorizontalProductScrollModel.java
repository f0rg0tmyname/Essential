package com.android.retail.hawk;

public class HorizontalProductScrollModel {
    private String productID;
    private String productImage;
    private String productName;
    private String productDetail;
    private String productPrice;
    private String productMrp;
    private String percentOff;

    public HorizontalProductScrollModel(String productID, String productImage, String productName, String productDetail, String productPrice, String productMrp) {
        this.productID = productID;
        this.productImage = productImage;
        this.productName = productName;
        this.productDetail = productDetail;
        this.productPrice = productPrice;
        this.productMrp = productMrp;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(String productMrp) {
        this.productMrp = productMrp;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(String percentOff) {
        this.percentOff = percentOff;
    }
}
