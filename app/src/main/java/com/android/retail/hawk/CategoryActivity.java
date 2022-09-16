package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.android.retail.hawk.DatabaseQueries.lists;
import static com.android.retail.hawk.DatabaseQueries.loadFragmentData;
import static com.android.retail.hawk.DatabaseQueries.loadedCategoryNames;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView catRecView;
    private ShopListAdapter adapter;

    ////// placeholder lists
    private List<ShopListModel> placeholderList = new ArrayList<>();
    ////// placeholder lists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ////placeholder list
        placeholderList.add(new ShopListModel("","", "", "1", "1", "",true));
        placeholderList.add(new ShopListModel("","", "", "1", "1", "", true));
        placeholderList.add(new ShopListModel("","", "", "1", "1", "", true));
        placeholderList.add(new ShopListModel("","", "", "1", "1", "", true));
        ////placeholder list

        TextView toolbarTitle = findViewById(R.id.category_activity_title);
        String title = getIntent().getStringExtra("CategoryName");
        String gridTitle = getIntent().getStringExtra("gridTitle");

        if (title == null){
            toolbarTitle.setText(gridTitle);
        }else if (gridTitle == null){
            toolbarTitle.setText(title);
        }

        catRecView = findViewById(R.id.category_activity_rec_view);

        /////////////////////////////// main Rec View

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        catRecView.setLayoutManager(testingLayoutManager);

        adapter = new ShopListAdapter(placeholderList, false);

        catRecView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /////////////////////////////// Main Rec View
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id==R.id.toolbar_search){

        }else if(id==R.id.toolbar_home_cart){
            startActivity(new Intent(getApplicationContext(),CartActivity.class));
        }else if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}