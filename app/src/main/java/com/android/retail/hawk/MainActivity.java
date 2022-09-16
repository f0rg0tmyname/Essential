package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.collection.LLRBBlackValueNode;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    public boolean onHomeFragment = true;
    private ImageView cartSign, arrowDown;
    private TextView address;
    private LinearLayout addressBtn;
    public static final int SELECT_ADDRESS = 0;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        arrowDown = findViewById(R.id.arrow_down);
        address = findViewById(R.id.address);
        addressBtn = findViewById(R.id.address_btn);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new HomeFragment()).commit();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {

                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            address.setText(addresses.get(0).getSubAdminArea());
                            arrowDown.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "117", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        } else {
            address.setText("Add Location");
            arrowDown.setVisibility(View.VISIBLE);
        }

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), CartActivity.class));
                        break;
                    case R.id.nav_orders:
                        Toast.makeText(MainActivity.this, "Orders", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_profile:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            onHomeFragment = true;
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            onHomeFragment = false;
                            break;
                        case R.id.nav_orders:
                            selectedFragment = new OrdersFragment();
                            onHomeFragment = false;
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            onHomeFragment = false;
                            break;
                    }
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_container, selectedFragment);
                    fragmentTransaction.commit();
                    return true;
                }
            };


    @Override
    public void onBackPressed() {
        if (!onHomeFragment) {
            setFragment(new HomeFragment());
            onHomeFragment = true;
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            return;
        }
        super.onBackPressed();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_home, menu);

        MenuItem cartItem = menu.findItem(R.id.toolbar_home_cart);
        if (DatabaseQueries.cartList.size() > 0) {
            cartItem.setActionView(R.layout.cart_badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.cart_badge_icon);
            cartSign = cartItem.getActionView().findViewById(R.id.cart_is_not_empty);
            badgeIcon.setImageResource(R.drawable.ic_cart);

            if (DatabaseQueries.cartList.size() == 0) {
                DatabaseQueries.loadCartList(this, false, new TextView(this));
                badgeIcon.setVisibility(View.INVISIBLE);
            } else {
                badgeIcon.setVisibility(View.VISIBLE);
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                }
            });

        } else {
            cartItem.setActionView(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_home_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.this.invalidateOptionsMenu();
        if (!address.getText().equals("Ghaziabad") && !address.getText().equals("Add Location")){
            new AlertDialog.Builder(this)
                    .setTitle("FYI")
                    .setMessage("Sorry, we are not currently delivering in your location")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "You can still browse our products tho", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_location)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 44: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location != null) {

                                    try {
                                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        address.setText(addresses.get(0).getSubAdminArea());
                                        arrowDown.setVisibility(View.GONE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(this, "You need to give permission to auto-locate your position", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}