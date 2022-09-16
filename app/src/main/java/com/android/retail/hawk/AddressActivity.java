package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.retail.hawk.DeliveryActivity.SELECT_ADDRESS;

public class AddressActivity extends AppCompatActivity {

    private int previousAddress;
    private RecyclerView recyclerView;
    private LinearLayout addAddressBtn, deliverHereBtn;
    private static AddressesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Toolbar toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.saved_address_rec_view);
        addAddressBtn = findViewById(R.id.add_new_address_btn);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);

        previousAddress = DatabaseQueries.selectedAddress;

        int mode = getIntent().getIntExtra("MODE", -1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AddressesAdapter(DatabaseQueries.addressesModelList, mode);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddAddressActivity.class).putExtra("INTENT", "null"));
            }
        });

        if (mode == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE);
        }

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DatabaseQueries.selectedAddress != previousAddress){

                    int previousAddressIndex = previousAddress;

                    Map<String, Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+ (previousAddress + 1), false);
                    updateSelection.put("selected_"+ String.valueOf(DatabaseQueries.selectedAddress+1), true);

                    previousAddress = DatabaseQueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                finish();
                            } else {
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(AddressActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    finish();
                }
            }
        });

    }

    public static void refreshAddressView(int deSelect, int select) {
        adapter.notifyItemChanged(deSelect);
        adapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (DatabaseQueries.selectedAddress != previousAddress){
                DatabaseQueries.addressesModelList.get(DatabaseQueries.selectedAddress).setSelected(false);
                DatabaseQueries.addressesModelList.get(previousAddress).setSelected(true);
                DatabaseQueries.selectedAddress = previousAddress;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (DatabaseQueries.selectedAddress != previousAddress){
            DatabaseQueries.addressesModelList.get(DatabaseQueries.selectedAddress).setSelected(false);
            DatabaseQueries.addressesModelList.get(previousAddress).setSelected(true);
            DatabaseQueries.selectedAddress = previousAddress;
        }
        super.onBackPressed();
    }
}