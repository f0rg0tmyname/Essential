package com.android.retail.hawk;

import java.util.List;

public class HomePageModel {

    public static final int SLIDING_BANNER = 1;
    public static final int AD_STRIP = 2;
    public static final int HORIZONTAL_PRODUCT_VIEW = 3;
    public static final int GRID_PRODUCT_VIEW = 4;

    private int type;


    //banner slider
    private List<SlidingBannerModel> sliderModelList;

    public HomePageModel(int type, List<SlidingBannerModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SlidingBannerModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SlidingBannerModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }
    //banner slider

    //ad strip
    private String resource;
    private String bgColor;

    public HomePageModel(int type, String resource, String bgColor) {
        this.type = type;
        this.resource = resource;
        this.bgColor = bgColor;
    }

    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getBgColor() {
        return bgColor;
    }
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    //ad strip

    //Horizontal pd view
    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private List<ShopListModel> viewAllProductList;

    public HomePageModel(int type, String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList, List<ShopListModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public List<ShopListModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<ShopListModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    //Horizontal pd view

    //grid view
    private List<GridLayoutModel> gridLayoutModelList;
    private String gridTitle;

    public HomePageModel(int type,String gridTitle, List<GridLayoutModel> gridLayoutModelList) {
        this.type = type;
        this.gridLayoutModelList = gridLayoutModelList;
        this.gridTitle = gridTitle;
    }

    public List<GridLayoutModel> getGridLayoutModelList() {
        return gridLayoutModelList;
    }

    public void setGridLayoutModelList(List<GridLayoutModel> gridLayoutModelList) {
        this.gridLayoutModelList = gridLayoutModelList;
    }

    //grid view


}

