package com.android.retail.hawk;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {

            case 1:
                return HomePageModel.SLIDING_BANNER;
            case 2:
                return HomePageModel.AD_STRIP;
            case 3:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 4:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomePageModel.SLIDING_BANNER:
                View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_banner_layout, parent, false);
                return new slidingBannerViewHolder(bannerView);
            case HomePageModel.AD_STRIP:
                View adView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_strip_layout, parent, false);
                return new adStripViewHolder(adView);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new horizontalProductViewHolder(horizontalView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_layout, parent, false);
                return new gridProductViewHolder(gridView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {

            case HomePageModel.SLIDING_BANNER:
                List<SlidingBannerModel> slidingBannerModelList = homePageModelList.get(position).getSliderModelList();
                ((slidingBannerViewHolder) holder).setBannerViewPager(slidingBannerModelList);
                break;

            case HomePageModel.AD_STRIP:
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBgColor();
                ((adStripViewHolder) holder).setAdStrip(resource, color);
                break;

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                String horLayTitle = homePageModelList.get(position).getTitle();
                List<ShopListModel> viewAllPdList = homePageModelList.get(position).getViewAllProductList();
                ((horizontalProductViewHolder) holder).setHorProductLayout(horizontalProductScrollModelList, horLayTitle, viewAllPdList);
                break;

            case HomePageModel.GRID_PRODUCT_VIEW:
                List<GridLayoutModel> gridProductModelList = homePageModelList.get(position).getGridLayoutModelList();
                ((gridProductViewHolder) holder).setGridProductLayout(gridProductModelList);

            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in_anim);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class slidingBannerViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerViewPager;
        private int currentPage;
        private Timer timer;
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        private List<SlidingBannerModel> arrangedList;

        public slidingBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerViewPager = itemView.findViewById(R.id.sliding_banner_view_pager);


        }

        private void setBannerViewPager(List<SlidingBannerModel> sliderModelList) {
            currentPage = 2;
            if (timer != null) {
                timer.cancel();
            }

            arrangedList = new ArrayList<>();

            for (int x = 0; x < sliderModelList.size(); x++) {
                arrangedList.add(x, sliderModelList.get(x));
            }

            arrangedList.add(0, sliderModelList.get(sliderModelList.size() - 2));
            arrangedList.add(1, sliderModelList.get(sliderModelList.size() - 1));
            arrangedList.add(sliderModelList.get(0));
            arrangedList.add(sliderModelList.get(1));

            SlidingBannerAdapter slidingBannerAdapter = new SlidingBannerAdapter(arrangedList);
            bannerViewPager.setAdapter(slidingBannerAdapter);
            bannerViewPager.setClipToPadding(false);
            bannerViewPager.setPageMargin(20);

            bannerViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }
                }
            };
            bannerViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow(arrangedList);

            bannerViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(arrangedList);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<SlidingBannerModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerViewPager.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(List<SlidingBannerModel> sliderModelList) {
            Handler handler = new Handler();
            Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSlideShow() {
            timer.cancel();
        }

    }

    public class adStripViewHolder extends RecyclerView.ViewHolder {

        private ImageView adStripImage;
        private ConstraintLayout adStripContainer;

        public adStripViewHolder(@NonNull View itemView) {
            super(itemView);

            adStripImage = itemView.findViewById(R.id.ad_strip_image);
            adStripContainer = itemView.findViewById(R.id.ad_strip_container);
        }

        private void setAdStrip(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_2)).into(adStripImage);
            adStripContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class horizontalProductViewHolder extends RecyclerView.ViewHolder {

        private TextView horScrollLayTitle;
        private TextView horScrollViewAllBtn;
        private RecyclerView horScrollRecView;

        public horizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            horScrollLayTitle = itemView.findViewById(R.id.hor_scroll_lay_title);
            horScrollViewAllBtn = itemView.findViewById(R.id.hor_scroll_view_all_btn);
            horScrollRecView = itemView.findViewById(R.id.hor_scroll_lay_rec_view);

            horScrollRecView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, List<ShopListModel> viewAllProductList) {

            horScrollLayTitle.setText(title);

            if (horizontalProductScrollModelList.size() > 8) {
                horScrollViewAllBtn.setVisibility(View.VISIBLE);
                horScrollViewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.shopListModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            } else {
                horScrollViewAllBtn.setVisibility(View.INVISIBLE);
            }

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horScrollRecView.setLayoutManager(linearLayoutManager);

            horScrollRecView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }

    public class gridProductViewHolder extends RecyclerView.ViewHolder {
        private GridLayout gridLayout;
        private TextView gridLayoutTitle;

        public gridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayout = itemView.findViewById(R.id.grid_product_lay);
            gridLayoutTitle = itemView.findViewById(R.id.grid_layout_title);
        }

        private void setGridProductLayout(List<GridLayoutModel> gridLayoutModelList) {

            gridLayoutTitle.setText("Essentials delivered to your doorstep");

            for (int x = 0; x < 2; x++) {
                ImageView gridItemImage = gridLayout.getChildAt(x).findViewById(R.id.grid_item_image);
                TextView gridItemTitle = gridLayout.getChildAt(x).findViewById(R.id.grid_item_title);
                ConstraintLayout gridBackground = gridLayout.getChildAt(x).findViewById(R.id.grid_background);

                Glide.with(itemView.getContext()).load(gridLayoutModelList.get(x).getGridImage()).into(gridItemImage);
                gridItemTitle.setText(gridLayoutModelList.get(x).getGridItemTitle());
                gridBackground.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(gridLayoutModelList.get(x).getBgColor())));


                gridLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = gridItemTitle.getText().toString();
                        Intent pdDetailsIntent = new Intent(itemView.getContext(), CategoryActivity.class);
                        pdDetailsIntent.putExtra("gridTitle", title);
                        itemView.getContext().startActivity(pdDetailsIntent);
                    }
                });

            }
        }
    }
}
