package com.android.retail.hawk;

public class SlidingBannerModel {
    private String banner;
    private String backgroundColor;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public SlidingBannerModel(String banner, String backgroundColor) {
        this.banner = banner;
        this.backgroundColor = backgroundColor;
    }
}
