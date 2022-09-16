package com.android.retail.hawk;

public class CategoryModel {

    private String categoryImage;
    private String categoryName;
    private String bgColor;

    public CategoryModel(String categoryImage, String categoryName, String bgColor) {
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
        this.bgColor = bgColor;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
