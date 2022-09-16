package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView shopListRecView;
    public static ShopListAdapter adapter;
    private LinearLayout emptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Toolbar toolbar = findViewById(R.id.shop_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emptyList = findViewById(R.id.empty_shop_list);

        DatabaseQueries.firebaseFirestore.collection("USERS").document(DatabaseQueries.firebaseAuth.getUid()).collection("USER_DATA").document("SHOPPING_LIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if ((long) documentSnapshot.get("list_size") == 0){
                    emptyList.setVisibility(View.VISIBLE);
                }else {
                    emptyList.setVisibility(View.GONE);
                }
            }
        });

        shopListRecView = findViewById(R.id.shopping_list_rec_view);

        LinearLayoutManager layoutManager =  new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shopListRecView.setLayoutManager(layoutManager);

        if (DatabaseQueries.shopListModelList.size() == 0){
            DatabaseQueries.shoppingList.clear();
            DatabaseQueries.loadShoppingList(ShoppingListActivity.this, true);
        }

        adapter = new ShopListAdapter(DatabaseQueries.shopListModelList, true);
        shopListRecView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_cart,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.toolbar_cart){
            startActivity(new Intent(getApplicationContext(),CartActivity.class));
        }else if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}