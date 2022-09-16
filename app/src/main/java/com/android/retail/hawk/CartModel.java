package com.android.retail.hawk;

import java.util.ArrayList;
import java.util.List;

public class CartModel {

    public static final int CART_ITEM_LAYOUT = 0;
    public static final int CART_PRICE_LAYOUT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //cart items
    private String productId;
    private String productImage;
    private String productName;
    private String productPrice;
    private String productMrp;
    private String productPercentOff;
    private String productDetail;
    private long productQuantity;
    private long maxQty;
    private boolean inStock;
    private long stockQuantity;
    private List<String> qtyIDs;

    public CartModel(int type, String productId, String productImage, String productName, String productPrice, String productMrp, String productDetail, long productQuantity, boolean inStock, long maxQty, long stockQuantity) {
        this.type = type;
        this.productId = productId;
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productMrp = productMrp;
        this.productDetail = productDetail;
        this.productQuantity = productQuantity;
        this.inStock = inStock;
        this.stockQuantity = stockQuantity;
        this.maxQty = maxQty;
        qtyIDs = new ArrayList<>();
    }

    public List<String> getQtyIDs() {
        return qtyIDs;
    }

    public void setQtyIDs(List<String> qtyIDs) {
        this.qtyIDs = qtyIDs;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public String getProductPercentOff() {
        return productPercentOff;
    }

    public void setProductPercentOff(String productPercentOff) {
        this.productPercentOff = productPercentOff;
    }

    public long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public long getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(long maxQty) {
        this.maxQty = maxQty;
    }

    //cart items

    //cart price layout

    public CartModel(int type) {
        this.type = type;
    }

    //cart price layout

}
