package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.android.retail.hawk.DatabaseQueries.cartModelList;

public class DeliveryActivity extends AppCompatActivity {

    private LinearLayout changeAddressBtn, proceedToPayBtn;
    private TextView totalAmount;

    public static final int SELECT_ADDRESS = 0;
    private TextView fullName, fullAddress, pinCode;
    private FirebaseFirestore firestore;
    private boolean allProductsAvailable = true;
    public static boolean getQtyIDs = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Toolbar toolbar = findViewById(R.id.toolbar_delivery_act);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeAddressBtn = findViewById(R.id.del_change_address_btn);
        proceedToPayBtn = findViewById(R.id.proceed_to_pay_btn);
        fullName = findViewById(R.id.full_name);
        fullAddress = findViewById(R.id.full_address);
        pinCode = findViewById(R.id.pincode);
        totalAmount = findViewById(R.id.total_amount);
        firestore = FirebaseFirestore.getInstance();

        String total = getIntent().getStringExtra("TOTAL");
        totalAmount.setText(total);

        getQtyIDs = true;

        /// accessing quantity
        if (getQtyIDs) {
            for (int x = 0; x < DatabaseQueries.cartModelList.size() - 1; x++) {

                for (int y = 0; y < DatabaseQueries.cartModelList.get(x).getProductQuantity(); y++) {
                    String qtyDocName = UUID.randomUUID().toString().substring(0,20);
                    Map<String, Object> timeStamp = new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    int finalX = x;
                    int finalY = y;
                    firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(x).getProductId())
                            .collection("QUANTITY").document(qtyDocName).set(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseQueries.cartModelList.get(finalX).getQtyIDs().add(qtyDocName);
                            if (finalY + 1 == DatabaseQueries.cartModelList.get(finalX).getProductQuantity()) {
                                firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(finalX).getProductId())
                                        .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                        .limit(DatabaseQueries.cartModelList.get(finalX).getStockQuantity()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    List<String> serverQuantity = new ArrayList<>();

                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                        serverQuantity.add(queryDocumentSnapshot.getId());
                                                    }

                                                    for (String qtyID : DatabaseQueries.cartModelList.get(finalX).getQtyIDs()) {
                                                        if (!serverQuantity.contains(qtyID)) {
                                                            Toast.makeText(DeliveryActivity.this, "Sorry, all products might not be available", Toast.LENGTH_SHORT).show();
                                                            allProductsAvailable = false;
                                                        }
                                                        if (serverQuantity.size() >= DatabaseQueries.cartModelList.get(finalX).getStockQuantity()) {
                                                            firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(finalX).getProductId()).update("in_stock", false);
                                                        }
                                                    }

                                                } else {
                                                    /// error
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }

            }
        } else {
            getQtyIDs = true;
        }
        /// accessing quantity

        changeAddressBtn.setVisibility(View.VISIBLE);
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs = false;
                Intent selectAddressIntent = new Intent(DeliveryActivity.this, AddressActivity.class);
                selectAddressIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(selectAddressIntent);
            }
        });

        proceedToPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allProductsAvailable) {
                    startActivity(new Intent(getApplicationContext(), PaymentMethodActivity.class).putExtra("TOTAL", totalAmount.getText().toString()));
                } else {
                    Toast.makeText(DeliveryActivity.this, "Sorry the item(s) in your cart are not available anymore", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fullName.setText(DatabaseQueries.addressesModelList.get(DatabaseQueries.selectedAddress).getFullName());
        fullAddress.setText(DatabaseQueries.addressesModelList.get(DatabaseQueries.selectedAddress).getAddress());
        pinCode.setText(DatabaseQueries.addressesModelList.get(DatabaseQueries.selectedAddress).getPinCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DeliveryActivity.getQtyIDs) {
            for (int x = 0; x < DatabaseQueries.cartModelList.size() - 1; x++) {
                for (String qtyID : DatabaseQueries.cartModelList.get(x).getQtyIDs()) {
                    int finalX = x;
                    firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(x)
                            .getProductId()).collection("QUANTITY").document(qtyID).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (qtyID.equals(DatabaseQueries.cartModelList.get(finalX).getQtyIDs().get(DatabaseQueries.cartModelList.get(finalX).getQtyIDs().size() - 1))) {
                                        DatabaseQueries.cartModelList.get(finalX).getQtyIDs().clear();
                                        firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(finalX).getProductId())
                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().getDocuments().size() < cartModelList.get(finalX).getProductQuantity()) {
                                                                firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(finalX).getProductId()).update("in_stock", true);
                                                            }
                                                        } else {
                                                            /// error
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        }
    }
}