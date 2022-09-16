package com.android.retail.hawk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRecView = view.findViewById(R.id.orders_rec_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        ordersRecView.setLayoutManager(layoutManager);

        List<OrdersModel> ordersModelList = new ArrayList<>();
        ordersModelList.add(new OrdersModel(R.drawable.product_image_3,"Saffola Masala Oats Pouch, 2Kg","Delivered on 15-05-2021"));
        ordersModelList.add(new OrdersModel(R.drawable.product_image_3,"Saffola Masala Oats Pouch, 2Kg","Cancelled"));
        ordersModelList.add(new OrdersModel(R.drawable.product_image_3,"Maggi Masala Noodles - Pack of 12","Delivered on 15-05-2021"));
        ordersModelList.add(new OrdersModel(R.drawable.product_image_3,"Saffola Masala Oats Pouch, 2Kg","Delivered on 15-05-2021"));
        ordersModelList.add(new OrdersModel(R.drawable.cat_flour1,"Maggi Masala Noodles - Pack of 12, Saffola Masala Oats Pouch, 2Kg","Cancelled"));
        ordersModelList.add(new OrdersModel(R.drawable.product_image_3,"Maggi Masala Noodles - Pack of 12","Delivered on 15-05-2021"));
        ordersModelList.add(new OrdersModel(R.drawable.cat_flour1,"Saffola Masala Oats Pouch, 2Kg","Delivered on 15-05-2021"));
        ordersModelList.add(new OrdersModel(R.drawable.cat_flour1,"Maggi Masala Noodles - Pack of 12, Saffola Masala Oats Pouch, 2Kg","Cancelled"));

        OrdersAdapter ordersAdapter = new OrdersAdapter(ordersModelList);
        ordersRecView.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();

        return view;
    }
}
