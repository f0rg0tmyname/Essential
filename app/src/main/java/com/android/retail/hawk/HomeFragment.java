package com.android.retail.hawk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.retail.hawk.DatabaseQueries.categoryModelList;
import static com.android.retail.hawk.DatabaseQueries.lists;
import static com.android.retail.hawk.DatabaseQueries.loadCategories;
import static com.android.retail.hawk.DatabaseQueries.loadFragmentData;
import static com.android.retail.hawk.DatabaseQueries.loadedCategoryNames;

public class HomeFragment extends Fragment {

    private RecyclerView homePageRecView;
    public static SwipeRefreshLayout swipeRefreshLayout;

    private HomePageAdapter adapter;
    private LottieAnimationView noInternetAnim;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    ////// placeholder lists
    private List<HomePageModel> placeholderList = new ArrayList<>();
    ////// placeholder lists

    private ImageView cartSign;

    public HomeFragment() {
        // Req Empty public Constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetAnim = view.findViewById(R.id.no_internet_anim);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        homePageRecView = view.findViewById(R.id.home_page_rec_view);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.orange), getContext().getResources().getColor(R.color.orange), getContext().getResources().getColor(R.color.orange));

        LinearLayoutManager homePageLayoutManager = new LinearLayoutManager(getContext());
        homePageLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecView.setLayoutManager(homePageLayoutManager);

        ////placeholder list
        List<SlidingBannerModel> sliderPlaceholder = new ArrayList<>();
        sliderPlaceholder.add(new SlidingBannerModel("null", "#ffffff"));
        sliderPlaceholder.add(new SlidingBannerModel("null", "#ffffff"));
        sliderPlaceholder.add(new SlidingBannerModel("null", "#ffffff"));
        sliderPlaceholder.add(new SlidingBannerModel("null", "#ffffff"));

        List<HorizontalProductScrollModel> horizontalScrollPlaceholder = new ArrayList<>();
        horizontalScrollPlaceholder.add(new HorizontalProductScrollModel("", "", "", "", "", ""));
        horizontalScrollPlaceholder.add(new HorizontalProductScrollModel("", "", "", "", "", ""));
        horizontalScrollPlaceholder.add(new HorizontalProductScrollModel("", "", "", "", "", ""));
        horizontalScrollPlaceholder.add(new HorizontalProductScrollModel("", "", "", "", "", ""));
        horizontalScrollPlaceholder.add(new HorizontalProductScrollModel("", "", "", "", "", ""));

        List<GridLayoutModel> gridPlaceHolder = new ArrayList<>();
        gridPlaceHolder.add(new GridLayoutModel("", "", "#ffffff"));
        gridPlaceHolder.add(new GridLayoutModel("", "", "#ffffff"));

        placeholderList.add(new HomePageModel(1, sliderPlaceholder));
        placeholderList.add(new HomePageModel(2, "", "#ffffff"));
        placeholderList.add(new HomePageModel(3, "", horizontalScrollPlaceholder, new ArrayList<ShopListModel>()));
        placeholderList.add(new HomePageModel(4, "Welcome", gridPlaceHolder));
        ////placeholder list

        adapter = new HomePageAdapter(placeholderList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            noInternetAnim.setVisibility(View.GONE);
            homePageRecView.setVisibility(View.VISIBLE);
            if (lists.size() == 0) {
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(homePageRecView, getContext(), 0, "Home", "qpksxGqeA3gvAUwKFDrh");
            } else {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecView.setAdapter(adapter);
        } else {
            noInternetAnim.setVisibility(View.VISIBLE);
            homePageRecView.setVisibility(View.GONE);
        }

        /////////////////////////////// swipe refresh layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                networkInfo = connectivityManager.getActiveNetworkInfo();
                DatabaseQueries.clearData();

                if (networkInfo != null && networkInfo.isConnected()) {

                    homePageRecView.setVisibility(View.VISIBLE);
                    noInternetAnim.setVisibility(View.GONE);
                    homePageRecView.setAdapter(adapter);

                    lists.add(new ArrayList<HomePageModel>());
                    loadFragmentData(homePageRecView, getContext(), 0, "Home", "qpksxGqeA3gvAUwKFDrh");

                } else {
                    homePageRecView.setVisibility(View.GONE);
                    noInternetAnim.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        /////////////////////////////// swipe refresh layout

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}
