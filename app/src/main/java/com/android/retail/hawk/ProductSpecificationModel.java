package com.android.retail.hawk;

public class ProductSpecificationModel {

    public static final int SPEC_TITLE = 0;
    public static final int SPEC_BODY = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ////Spec title
    private String title;

    public ProductSpecificationModel(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    ////Spec title

    ////Spec Body
    private String specName;
    private String specValue;

    public ProductSpecificationModel(int type, String specName, String specValue) {
        this.type = type;
        this.specName = specName;
        this.specValue = specValue;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }

    ////Spec Body



}
