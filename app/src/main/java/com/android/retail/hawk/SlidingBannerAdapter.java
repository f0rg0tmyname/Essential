package com.android.retail.hawk;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SlidingBannerAdapter extends PagerAdapter {

    private List<SlidingBannerModel> slidingBannerModelList;

    public SlidingBannerAdapter(List<SlidingBannerModel> slidingBannerModelList) {
        this.slidingBannerModelList = slidingBannerModelList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.sliding_banner_image,container,false);
        ConstraintLayout bannerImgContainer = view.findViewById(R.id.sliding_banner_image_container);
        bannerImgContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(slidingBannerModelList.get(position).getBackgroundColor())));
        ImageView banner = view.findViewById(R.id.sliding_banner_image);
        Glide.with(container.getContext()).load(slidingBannerModelList.get(position).getBanner()).apply(new RequestOptions().placeholder(R.drawable.placeholder_2)).into(banner);
        container.addView(view,0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return slidingBannerModelList.size();
    }
}
