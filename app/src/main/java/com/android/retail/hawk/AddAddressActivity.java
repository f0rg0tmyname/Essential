package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private LinearLayout addAddress;
    private TextInputEditText fullName, flatNo, areaColony, landmark, city, pinCode;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addAddress = findViewById(R.id.cart_checkout_btn);
        fullName = findViewById(R.id.aa_full_name);
        flatNo = findViewById(R.id.aa_flat_no);
        areaColony = findViewById(R.id.aa_area_colony);
        landmark = findViewById(R.id.aa_landmark);
        city = findViewById(R.id.aa_city);
        pinCode = findViewById(R.id.aa_pin_code);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(fullName.getText())) {
                    if (!TextUtils.isEmpty(flatNo.getText())) {
                        if (!TextUtils.isEmpty(areaColony.getText())) {
                            if (!TextUtils.isEmpty(city.getText())) {
                                if (!TextUtils.isEmpty(pinCode.getText()) && pinCode.getText().length() == 6) {

                                    Map<String, Object> addAddress = new HashMap();
                                    addAddress.put("list_size", ((long) DatabaseQueries.addressesModelList.size() + 1));
                                    addAddress.put("full_name_" + ((long) DatabaseQueries.addressesModelList.size() + 1), fullName.getText().toString());

                                    if (TextUtils.isEmpty(landmark.getText())) {
                                        addAddress.put("address_" + ((long) DatabaseQueries.addressesModelList.size() + 1), flatNo.getText().toString() + ", " + areaColony.getText().toString() + ", " + city.getText().toString());
                                    } else {
                                        addAddress.put("address_" + ((long) DatabaseQueries.addressesModelList.size() + 1), flatNo.getText().toString() + ", " + areaColony.getText().toString() + ", " + landmark.getText().toString() + ", " + city.getText().toString());
                                    }

                                    addAddress.put("pin_code_" + ((long) DatabaseQueries.addressesModelList.size() + 1), pinCode.getText().toString());
                                    addAddress.put("selected_" + ((long) DatabaseQueries.addressesModelList.size() + 1), true);
                                    if (DatabaseQueries.addressesModelList.size() > 0) {
                                        addAddress.put("selected_" + (DatabaseQueries.selectedAddress + 1), false);
                                    }

                                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                            .document("ADDRESSES")
                                            .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (DatabaseQueries.addressesModelList.size() > 0) {
                                                    DatabaseQueries.addressesModelList.get(DatabaseQueries.selectedAddress).setSelected(false);
                                                }
                                                if (TextUtils.isEmpty(landmark.getText())) {
                                                    DatabaseQueries.addressesModelList.add(new AddressesModel(fullName.getText().toString(), flatNo.getText().toString() + ", " + areaColony.getText().toString() + ", " + city.getText().toString(), pinCode.getText().toString(), true));
                                                } else {
                                                    DatabaseQueries.addressesModelList.add(new AddressesModel(fullName.getText().toString(), flatNo.getText().toString() + ", " + areaColony.getText().toString() + ", " + landmark.getText().toString() + ", " + city.getText().toString(), pinCode.getText().toString(), true));
                                                }

                                                if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                    String total = getIntent().getStringExtra("TOTAL");
                                                    Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                                                    intent.putExtra("TOTAL", total);
                                                    startActivity(intent);
                                                } else {
                                                    AddressActivity.refreshAddressView(DatabaseQueries.selectedAddress, DatabaseQueries.addressesModelList.size() - 1);
                                                }
                                                DatabaseQueries.selectedAddress = DatabaseQueries.addressesModelList.size() - 1;
                                                finish();

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    pinCode.requestFocus();
                                }
                            } else {
                                city.requestFocus();
                            }
                        } else {
                            areaColony.requestFocus();
                        }
                    } else {
                        flatNo.requestFocus();
                    }
                } else {
                    fullName.requestFocus();
                }
            }
        });

    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(AddAddressActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {

                        try {
                            Geocoder geocoder = new Geocoder(AddAddressActivity.this,
                                    Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                            areaColony.setText(addresses.get(0).getSubLocality());

                            city.setText(addresses.get(0).getSubAdminArea());

                            pinCode.setText(addresses.get(0).getPostalCode());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(AddAddressActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(AddAddressActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            if (ActivityCompat.checkSelfPermission(AddAddressActivity.this
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {

                            try {
                                Geocoder geocoder = new Geocoder(AddAddressActivity.this,
                                        Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );

                                areaColony.setText(addresses.get(0).getSubLocality());

                                city.setText(addresses.get(0).getSubAdminArea());

                                pinCode.setText(addresses.get(0).getPostalCode());

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(AddAddressActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
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
}