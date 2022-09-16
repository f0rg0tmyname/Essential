package com.android.retail.hawk;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseQueries {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoryNames = new ArrayList<>();

    public static List<String> shoppingList = new ArrayList<>();
    public static List<ShopListModel> shopListModelList = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartModel> cartModelList = new ArrayList<>();

    public static int selectedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static void loadCategories(final CategoryAdapter categoryAdapter, final Context context) {

        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("image").toString(), documentSnapshot.get("categoryName").toString(), "#ffffff"));
                            }
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(RecyclerView homePageRecView, final Context context, final int index, String categoryName, String document) {

        firebaseFirestore.collection(categoryName.toUpperCase()).document(document).collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if ((long) documentSnapshot.get("view_type") == 1) {
                                    List<SlidingBannerModel> bannerModelList = new ArrayList<>();
                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");

                                    for (long x = 1; x < no_of_banners + 1; x++) {
                                        bannerModelList.add(new SlidingBannerModel(documentSnapshot.get("banner_" + x).toString()
                                                , documentSnapshot.get("banner_" + x + "_bg").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(1, bannerModelList));

                                } else if ((long) documentSnapshot.get("view_type") == 2) {

                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("strip_ad_image").toString(), documentSnapshot.get("ad_bg").toString()));

                                } else if ((long) documentSnapshot.get("view_type") == 3) {

                                    List<ShopListModel> viewAllProductList = new ArrayList<>();

                                    List<HorizontalProductScrollModel> horizontalModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");

                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_" + x + "_ID").toString()
                                                , documentSnapshot.get("product_" + x + "_image").toString()
                                                , documentSnapshot.get("product_" + x + "_name").toString()
                                                , documentSnapshot.get("product_" + x + "_detail").toString()
                                                , documentSnapshot.get("product_" + x + "_price").toString()
                                                , documentSnapshot.get("product_" + x + "_mrp").toString()));

                                        viewAllProductList.add(new ShopListModel(documentSnapshot.get("product_" + x + "_ID").toString(), documentSnapshot.get("product_" + x + "_image").toString()
                                                , documentSnapshot.get("product_" + x + "_name").toString()
                                                , documentSnapshot.get("product_" + x + "_price").toString()
                                                , documentSnapshot.get("product_" + x + "_mrp").toString()
                                                , documentSnapshot.get("product_" + x + "_detail").toString()
                                                , (boolean)documentSnapshot.get("in_stock_" + x)));
                                    }
                                    lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), horizontalModelList, viewAllProductList));

                                } else if ((long) documentSnapshot.get("view_type") == 4) {
                                    List<GridLayoutModel> gridModelList = new ArrayList<>();
                                    long no_of_grid_items = (long) documentSnapshot.get("no_of_grid_item");

                                    for (long x = 1; x < no_of_grid_items + 1; x++) {
                                        gridModelList.add(new GridLayoutModel(documentSnapshot.get("grid_item_" + x + "_image").toString()
                                                , documentSnapshot.get("grid_item_" + x + "_title").toString()
                                                , documentSnapshot.get("grid_item_" + x + "_bg").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(4, documentSnapshot.get("grid_layout_title").toString(), gridModelList));
                                }
                            }
                            HomePageAdapter adapter = new HomePageAdapter(lists.get(index));
                            homePageRecView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadShoppingList(final Context context, final boolean loadProductData) {

        shoppingList.clear();
        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("SHOPPING_LIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    for (long x = 0; x < Long.parseLong(task.getResult().get("list_size").toString()); x++) {
                        shoppingList.add(task.getResult().get("product_" + x + "_ID").toString());

                        if (DatabaseQueries.shoppingList.contains(ProductDetailsActivity.productId)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_SHOP_LIST = true;
                            if (ProductDetailsActivity.addToShopListAnim != null) {
                                ProductDetailsActivity.addToShopListAnim.setVisibility(View.VISIBLE);
                                ProductDetailsActivity.addToShopListIcon.setVisibility(View.GONE);
                            }
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_SHOP_LIST = false;
                            if (ProductDetailsActivity.addToShopListAnim != null) {
                                ProductDetailsActivity.addToShopListAnim.setVisibility(View.GONE);
                                ProductDetailsActivity.addToShopListIcon.setVisibility(View.VISIBLE);
                                ProductDetailsActivity.addToShopListIcon.setImageResource(R.drawable.ic_favorite_border);
                            }
                        }

                        if (loadProductData) {
                            shopListModelList.clear();
                            final String productId = task.getResult().get("product_" + x + "_ID").toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                shopListModelList.add(new ShopListModel(productId, task.getResult().get("product_image_1").toString()
                                                        , task.getResult().get("product_name").toString()
                                                        , task.getResult().get("product_price").toString()
                                                        , task.getResult().get("product_mrp").toString()
                                                        , task.getResult().get("product_detail").toString()
                                                        ,(boolean)task.getResult().get("in_stock")));

                                                ShoppingListActivity.adapter.notifyDataSetChanged();
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void removeFromShopList(int index, final Context context) {

        String removedProductId = shoppingList.get(index);
        shoppingList.remove(index);
        Map<String, Object> updateShopList = new HashMap<>();

        for (int x = 0; x < shoppingList.size(); x++) {
            updateShopList.put("product_" + x + "ID", shoppingList.get(x));
        }
        updateShopList.put("list_size", (long) shoppingList.size());

        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("SHOPPING_LIST")
                .set(updateShopList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if (shopListModelList.size() != 0) {
                                shopListModelList.remove(index);
                                ShoppingListActivity.adapter.notifyDataSetChanged();
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_SHOP_LIST = false;
                        } else {
                            if (ProductDetailsActivity.addToShopListAnim != null) {
                                ProductDetailsActivity.addToShopListAnim.setVisibility(View.GONE);
                                ProductDetailsActivity.addToShopListIcon.setVisibility(View.VISIBLE);
                                ProductDetailsActivity.addToShopListIcon.setImageResource(R.drawable.ic_favorite_border);
                            }
                            shoppingList.add(index, removedProductId);
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_shopList_query = false;
                    }
                });
    }

    public static void loadCartList(final Context context, final boolean loadProductData, final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    for (long x = 0; x < Long.parseLong(task.getResult().get("list_size").toString()); x++) {
                        cartList.add(task.getResult().get("product_" + x + "_ID").toString());

                        ProductDetailsActivity.ALREADY_ADDED_TO_CART = DatabaseQueries.cartList.contains(ProductDetailsActivity.productId);

                        if (loadProductData) {
                            cartModelList.clear();
                            final String productId = task.getResult().get("product_" + x + "_ID").toString();

                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int index = 0;
                                                if (cartList.size() >= 2) {
                                                    index = cartList.size() - 2;
                                                }
                                                cartModelList.add(index, new CartModel(CartModel.CART_ITEM_LAYOUT
                                                        , productId
                                                        , task.getResult().get("product_image_1").toString()
                                                        , task.getResult().get("product_name").toString()
                                                        , task.getResult().get("product_price").toString()
                                                        , task.getResult().get("product_mrp").toString()
                                                        , task.getResult().get("product_detail").toString()
                                                        , 1
                                                        , (boolean)task.getResult().get("in_stock")
                                                        , (long)task.getResult().get("max_qty")
                                                        , (long)task.getResult().get("stock_quantity")));

                                                if (cartList.size() == 1 && (boolean)task.getResult().get("in_stock")) {
                                                    cartModelList.add(new CartModel(CartModel.CART_PRICE_LAYOUT));
                                                    LinearLayout addToCartBtn = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                    addToCartBtn.setVisibility(View.VISIBLE);
                                                }
                                                if (cartList.size() == 0){
                                                    cartModelList.clear();
                                                }
                                                CartActivity.cartAdapter.notifyDataSetChanged();
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void removeFromCart(int index, final Context context,final TextView cartTotalAmount) {

        String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_" + x + "_ID", cartList.get(x));
            updateCartList.put("product_" + x + "_quantity", 0);
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("CART")
                .set(updateCartList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if (cartModelList.size() != 0) {
                                cartModelList.remove(index);
                                CartActivity.cartAdapter.notifyDataSetChanged();
                            }
                            if (cartList.size() == 0){
                                LinearLayout addToCartBtn = (LinearLayout) cartTotalAmount.getParent().getParent();
                                addToCartBtn.setVisibility(View.GONE);
                                cartModelList.clear();
                            }
                        } else {
                            cartList.add(index, removedProductId);
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_cart_query = false;
                    }
                });
    }

    public static void loadAddresses(final Context context, final String totalAmount){

        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    Intent deliveryIntent;
                    if ((long)task.getResult().get("list_size") == 0){
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                        deliveryIntent.putExtra("TOTAL", totalAmount);

                    }else {
                        for (long x = 1; x < (long)task.getResult().get("list_size") + 1; x++){
                            addressesModelList.add(new AddressesModel(task.getResult().get("full_name_"+x).toString()
                                    , task.getResult().get("address_"+x).toString()
                                    , task.getResult().get("pin_code_"+x).toString()
                                    , (boolean)task.getResult().get("selected_"+x)));
                            if ((boolean)task.getResult().get("selected_"+x)){
                                selectedAddress = Integer.parseInt(String.valueOf(x-1));
                            }
                        }
                        deliveryIntent = new Intent(context, DeliveryActivity.class);
                        deliveryIntent.putExtra("TOTAL", totalAmount);
                    }
                    context.startActivity(deliveryIntent);
                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCategoryNames.clear();
        shoppingList.clear();
        shopListModelList.clear();
        cartList.clear();
        cartModelList.clear();
        addressesModelList.clear();
    }

}
