package com.android.retail.hawk;

public class GridLayoutModel {

    private String gridImage;
    private String gridItemTitle;
    private String bgColor;

    public GridLayoutModel(String gridImage, String gridItemTitle, String bgColor) {
        this.gridImage = gridImage;
        this.gridItemTitle = gridItemTitle;
        this.bgColor = bgColor;
    }

    public String getGridImage() {
        return gridImage;
    }

    public void setGridImage(String gridImage) {
        this.gridImage = gridImage;
    }

    public String getGridItemTitle() {
        return gridItemTitle;
    }

    public void setGridItemTitle(String gridItemTitle) {
        this.gridItemTitle = gridItemTitle;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
