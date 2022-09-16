package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecView;
    public static LinearLayout checkoutBtn;
    private LinearLayout emptyCart;
    private TextView totalAmount;

    public static CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cartRecView = findViewById(R.id.cart_items_rec_view);
        checkoutBtn = findViewById(R.id.cart_checkout_btn);
        emptyCart = findViewById(R.id.empty_cart_prompt);
        totalAmount = findViewById(R.id.total_cart_amount);

        DatabaseQueries.firebaseFirestore.collection("USERS").document(DatabaseQueries.firebaseAuth.getUid()).collection("USER_DATA").document("CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if ((long)documentSnapshot.get("list_size") == 0){
                    emptyCart.setVisibility(View.VISIBLE);
                }else {
                    emptyCart.setVisibility(View.GONE);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecView.setLayoutManager(linearLayoutManager);

        cartAdapter = new CartAdapter(DatabaseQueries.cartModelList, totalAmount);
        cartRecView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String total = totalAmount.getText().toString();
                if (DatabaseQueries.addressesModelList.size() == 0) {
                    DatabaseQueries.loadAddresses(CartActivity.this, total);
                }else {
                    startActivity(new Intent(getApplicationContext(), DeliveryActivity.class).putExtra("TOTAL", total));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DatabaseQueries.cartModelList.size() == 0){
            DatabaseQueries.cartList.clear();
            DatabaseQueries.loadCartList(CartActivity.this, true, totalAmount);

        }else {
            if (DatabaseQueries.cartModelList.get(DatabaseQueries.cartModelList.size()-1).getType() == CartModel.CART_PRICE_LAYOUT){
                checkoutBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}