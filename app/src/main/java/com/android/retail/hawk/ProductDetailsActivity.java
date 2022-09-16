package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_shopList_query = false;
    public static boolean running_cart_query = false;

    private ViewPager productImagesViewPager;
    private TabLayout productImagesCountIndicator;

    private ConstraintLayout rootLayout;
    private TextView productName;
    private TextView productMrp;
    private TextView productPrice;
    private TextView percentOff;
    private TextView savedRupees;

    private ConstraintLayout descriptionTabContainer;
    private ConstraintLayout singleDesContainer;

    private ViewPager productDescriptionViewPager;
    private TabLayout productDescriptionTabLayout;
    private TextView singleDesBody;

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;

    public static boolean ALREADY_ADDED_TO_SHOP_LIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    public static LottieAnimationView addToShopListAnim;
    public static ImageView addToShopListIcon;
    private LinearLayout addToCartBtn, addedToCartLL;

    public static MenuItem cartItem;

    private Snackbar snackbar;

    private FirebaseFirestore fireStore;
    public static String productId;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentSnapshot documentSnapshot;

    private int qty_of_pd_added = 0;

    private String price;
    private int f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar_pd_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        qty_of_pd_added = 0;

        productImagesViewPager = findViewById(R.id.product_images_view_pager);
        productImagesCountIndicator = findViewById(R.id.product_images_count_indicator);

        addToShopListAnim = findViewById(R.id.like_anim);
        addToShopListIcon = findViewById(R.id.like_icon);

        productDescriptionViewPager = findViewById(R.id.product_description_view_pager);
        productDescriptionTabLayout = findViewById(R.id.product_description_tab_layout);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productMrp = findViewById(R.id.product_mrp);
        percentOff = findViewById(R.id.percent_off);
        savedRupees = findViewById(R.id.saved_rupees);
        rootLayout = findViewById(R.id.constraint_layout);
        descriptionTabContainer = findViewById(R.id.details_tab_layout_container);
        singleDesContainer = findViewById(R.id.pd_single_des_container);
        singleDesBody = findViewById(R.id.pd_des_single_body);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        addedToCartLL = findViewById(R.id.already_in_cart);

        fireStore = FirebaseFirestore.getInstance();

        List<String> productImageList = new ArrayList<>();
        productId = getIntent().getStringExtra("PRODUCT_ID");

        fireStore.collection("PRODUCTS").document(productId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();

                    for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                        productImageList.add(documentSnapshot.get("product_image_" + x).toString());
                    }
                    ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImageList);
                    productImagesViewPager.setAdapter(productImagesAdapter);

                    productName.setText(documentSnapshot.get("product_name").toString() + ", " + documentSnapshot.get("product_detail").toString());

                    String mrp = documentSnapshot.get("product_mrp").toString();
                    price = documentSnapshot.get("product_price").toString();

                    productMrp.setText(mrp);
                    productPrice.setText(price);

                    double a = Double.parseDouble(mrp);
                    double b = Double.parseDouble(price);
                    double c;
                    int d;
                    double e;

                    c = ((a - b) / a) * 100;
                    e = a - b;
                    d = (int) c;
                    f = (int) e;

                    String saved = String.valueOf(f);
                    String percOff = String.valueOf(d);

                    percentOff.setText(percOff);
                    savedRupees.setText(saved);

                    if ((boolean) documentSnapshot.get("use_tab_layout")) {
                        descriptionTabContainer.setVisibility(View.VISIBLE);
                        singleDesContainer.setVisibility(View.GONE);
                        productDescription = documentSnapshot.get("product_description").toString();


                        for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {

                            productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));

                            for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_values") + 1; y++) {
                                productSpecificationModelList.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_spec_name_" + y).toString(), documentSnapshot.get("spec_title_" + x + "_spec_value_" + y).toString()));
                            }
                        }

                    } else {
                        descriptionTabContainer.setVisibility(View.GONE);
                        singleDesContainer.setVisibility(View.VISIBLE);
                        singleDesBody.setText(documentSnapshot.get("product_description").toString());
                    }
                    productDescriptionViewPager.setAdapter(new ProductDescriptionAdapter(getSupportFragmentManager(), productDescriptionTabLayout.getTabCount(), productDescription, productSpecificationModelList));

                    if (DatabaseQueries.shoppingList.size() == 0) {
                        DatabaseQueries.loadShoppingList(ProductDetailsActivity.this, false);
                    }
                    if (DatabaseQueries.cartList.size() == 0) {
                        DatabaseQueries.loadCartList(ProductDetailsActivity.this, false, new TextView(ProductDetailsActivity.this));
                    }

                    if (DatabaseQueries.cartList.contains(productId)) {
                        ALREADY_ADDED_TO_CART = true;
                    } else {
                        ALREADY_ADDED_TO_CART = false;
                    }

                    if (DatabaseQueries.shoppingList.contains(productId)) {
                        ALREADY_ADDED_TO_SHOP_LIST = true;
                        addToShopListAnim.setVisibility(View.VISIBLE);
                        addToShopListIcon.setVisibility(View.GONE);
                    } else {
                        addToShopListAnim.setVisibility(View.GONE);
                        addToShopListIcon.setVisibility(View.VISIBLE);
                        addToShopListIcon.setImageResource(R.drawable.ic_favorite_border);
                        ALREADY_ADDED_TO_SHOP_LIST = false;
                    }

                    if ((boolean) documentSnapshot.get("in_stock")) {

                        if (DatabaseQueries.cartList.contains(productId)) {
                            ALREADY_ADDED_TO_CART = true;
                            addedToCartLL.setVisibility(View.VISIBLE);
                            addToCartBtn.setVisibility(View.GONE);
                        }

                        addToCartBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                addToCartBtn.setVisibility(View.GONE);
                                addedToCartLL.setVisibility(View.VISIBLE);
                                qty_of_pd_added = 1;

                                if (!running_cart_query) {
                                    running_cart_query = true;

                                    if (ALREADY_ADDED_TO_CART) {
                                        running_cart_query = false;

                                    } else {
                                        Map<String, Object> addProduct = new HashMap<>();
                                        addProduct.put("product_" + DatabaseQueries.cartList.size() + "_ID", productId);
                                        addProduct.put("list_size", (long) (DatabaseQueries.cartList.size() + 1));

                                        fireStore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("CART")
                                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    if (DatabaseQueries.cartModelList.size() != 0) {
                                                        DatabaseQueries.cartModelList.add(0, new CartModel(CartModel.CART_ITEM_LAYOUT
                                                                , productId
                                                                , documentSnapshot.get("product_image_1").toString()
                                                                , documentSnapshot.get("product_name").toString()
                                                                , documentSnapshot.get("product_price").toString()
                                                                , documentSnapshot.get("product_mrp").toString()
                                                                , documentSnapshot.get("product_detail").toString()
                                                                , (long) 1
                                                                , (boolean) documentSnapshot.get("in_stock")
                                                                , (long) documentSnapshot.get("max_qty")
                                                                , (long) documentSnapshot.get("stock_quantity")));
                                                    }

                                                    ALREADY_ADDED_TO_CART = true;
                                                    DatabaseQueries.cartList.add(productId);
                                                    invalidateOptionsMenu();
                                                    running_cart_query = false;
                                                    Toast.makeText(ProductDetailsActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    running_cart_query = false;
                                                    String error = task.getException().getMessage();
                                                    snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }


                            }
                        });

                        addedToCartLL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ProductDetailsActivity.this, "This product is already in your cart", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        addedToCartLL.setVisibility(View.GONE);
                        addToCartBtn.setVisibility(View.VISIBLE);
                        addToCartBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                        TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                        outOfStock.setText("Out of Stock");
                        outOfStock.setTextColor(getResources().getColor(R.color.white));
                        outOfStock.setCompoundDrawables(null, null, null, null);
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


        productImagesCountIndicator.setupWithViewPager(productImagesViewPager, true);

        addToShopListAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running_shopList_query) {
                    running_shopList_query = true;

                    if (ALREADY_ADDED_TO_SHOP_LIST) {

                        int index = DatabaseQueries.shoppingList.indexOf(productId);

                        DatabaseQueries.removeFromShopList(index, ProductDetailsActivity.this);

                        addToShopListAnim.setVisibility(View.GONE);
                        addToShopListIcon.setVisibility(View.VISIBLE);
                        addToShopListIcon.setImageResource(R.drawable.ic_favorite_border);
                        snackbar.make(rootLayout, "Product Removed from Shopping List", Snackbar.LENGTH_LONG).show();
                    } else {

                    }
                }
            }
        });

        addToShopListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running_shopList_query) {
                    running_shopList_query = true;
                    if (!ALREADY_ADDED_TO_SHOP_LIST) {
                        addToShopListIcon.setVisibility(View.GONE);
                        addToShopListAnim.setVisibility(View.VISIBLE);
                        addToShopListAnim.playAnimation();
                        Map<String, Object> addProduct = new HashMap<>();
                        addProduct.put("product_" + DatabaseQueries.shoppingList.size() + "_ID", productId);
                        addProduct.put("list_size", (long) (DatabaseQueries.shoppingList.size() + 1));

                        fireStore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("SHOPPING_LIST")
                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (DatabaseQueries.shopListModelList.size() != 0) {
                                        DatabaseQueries.shopListModelList.add(new ShopListModel(productId, documentSnapshot.get("product_image_1").toString()
                                                , documentSnapshot.get("product_name").toString()
                                                , documentSnapshot.get("product_price").toString()
                                                , documentSnapshot.get("product_mrp").toString()
                                                , documentSnapshot.get("product_detail").toString()
                                                , (boolean) documentSnapshot.get("in_stock")));
                                    }

                                    ALREADY_ADDED_TO_SHOP_LIST = true;
                                    addToShopListAnim.setVisibility(View.VISIBLE);
                                    addToShopListIcon.setVisibility(View.GONE);
                                    snackbar.make(rootLayout, "Product Added to Shopping List", Snackbar.LENGTH_LONG).show();
                                    DatabaseQueries.shoppingList.add(productId);

                                } else {
                                    addToShopListAnim.setVisibility(View.INVISIBLE);
                                    addToShopListIcon.setVisibility(View.VISIBLE);
                                    addToShopListIcon.setImageResource(R.drawable.ic_favorite_border);
                                    String error = task.getException().getMessage();
                                    snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
                                }
                                running_shopList_query = false;
                            }
                        });
                    }
                }
            }
        });

        productDescriptionViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDescriptionTabLayout));

        productDescriptionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDescriptionViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (DatabaseQueries.cartList.size() == 0) {
            DatabaseQueries.loadCartList(ProductDetailsActivity.this, false, new TextView(ProductDetailsActivity.this));
            addedToCartLL.setVisibility(View.GONE);
            addToCartBtn.setVisibility(View.VISIBLE);
        }
        if (DatabaseQueries.cartList.contains(productId)) {
            ALREADY_ADDED_TO_CART = true;
            addedToCartLL.setVisibility(View.VISIBLE);
            addToCartBtn.setVisibility(View.GONE);
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        if (DatabaseQueries.shoppingList.contains(productId)) {
            ALREADY_ADDED_TO_SHOP_LIST = true;
            addToShopListAnim.setVisibility(View.VISIBLE);
            addToShopListIcon.setVisibility(View.GONE);
        } else {
            ALREADY_ADDED_TO_SHOP_LIST = false;
            addToShopListAnim.setVisibility(View.GONE);
            addToShopListIcon.setVisibility(View.VISIBLE);
            addToShopListIcon.setImageResource(R.drawable.ic_favorite_border);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_cart, menu);

        cartItem = menu.findItem(R.id.toolbar_home_cart);

        if (DatabaseQueries.cartList.size() > 0) {
            cartItem.setActionView(R.layout.cart_badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.cart_badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_cart);

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
        if (id == R.id.toolbar_search) {

        } else if (id == R.id.toolbar_home_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}