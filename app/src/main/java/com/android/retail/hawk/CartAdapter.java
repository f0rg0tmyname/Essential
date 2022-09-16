package com.android.retail.hawk;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.collection.LLRBBlackValueNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartModel> cartModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private int qty;

    public CartAdapter(List<CartModel> cartModelList, TextView cartTotalAmount) {
        this.cartModelList = cartModelList;
        this.cartTotalAmount = cartTotalAmount;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartModelList.get(position).getType()) {
            case 0:
                return CartModel.CART_ITEM_LAYOUT;
            case 1:
                return CartModel.CART_PRICE_LAYOUT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartModel.CART_ITEM_LAYOUT:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(view);
            case CartModel.CART_PRICE_LAYOUT:
                View priceView = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_dist_layout, parent, false);
                return new CartPriceViewHolder(priceView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartModelList.get(position).getType()) {
            case CartModel.CART_ITEM_LAYOUT:
                String productId = cartModelList.get(position).getProductId();
                String resource = cartModelList.get(position).getProductImage();
                String name = cartModelList.get(position).getProductName();
                String price = cartModelList.get(position).getProductPrice();
                String mrp = cartModelList.get(position).getProductMrp();
                String perOff = cartModelList.get(position).getProductPercentOff();
                String detail = cartModelList.get(position).getProductDetail();
                long qty = cartModelList.get(position).getProductQuantity();
                boolean inStock = cartModelList.get(position).isInStock();
                long maxQty = cartModelList.get(position).getMaxQty();

                ((CartItemViewHolder) holder).setCartItems(productId, resource, name, price, mrp, perOff, detail, qty, inStock, position, maxQty);
                break;

            case CartModel.CART_PRICE_LAYOUT:
                int totalItemPrice = 0;
                String delPrice;
                int subTotal;
                int savedAmount = 0;

                for (int x = 0; x < cartModelList.size(); x++) {
                    if (cartModelList.get(x).getType() == CartModel.CART_ITEM_LAYOUT && cartModelList.get(x).isInStock()) {
                        totalItemPrice = totalItemPrice + Integer.parseInt(cartModelList.get(x).getProductPrice());
                    }
                }

                if (totalItemPrice > 299) {
                    delPrice = "0 (FREE)";
                    subTotal = totalItemPrice;
                } else {
                    delPrice = "40";
                    subTotal = totalItemPrice + 40;
                }

                ((CartPriceViewHolder) holder).setCartPrices(totalItemPrice, delPrice, savedAmount, subTotal);
                break;

            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in_anim);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productMrp;
        private TextView percentOff;
        private TextView productDetail;
        private TextView productQuantity;
        private TextView decQuantity;
        private TextView incQuantity;
        private ImageView deleteBtn;
        private LinearLayout percContainer, incDecContainer;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.shoplist_pd_image);
            productName = itemView.findViewById(R.id.shoplist_pd_name);
            productPrice = itemView.findViewById(R.id.cart_pd_price);
            productMrp = itemView.findViewById(R.id.cart_pd_mrp);
            percentOff = itemView.findViewById(R.id.cart_pd_percent_off);
            productDetail = itemView.findViewById(R.id.cart_pd_detail);
            productQuantity = itemView.findViewById(R.id.cart_pd_quantity);
            decQuantity = itemView.findViewById(R.id.cart_pd_dec_quan_btn);
            incQuantity = itemView.findViewById(R.id.cart_pd_inc_quan_btn);
            deleteBtn = itemView.findViewById(R.id.inc_dec_delete);
            percContainer = itemView.findViewById(R.id.perc_container);
            incDecContainer = itemView.findViewById(R.id.inc_dec_container);
        }

        private void setCartItems(String productId, String resource, String pdName, String pdPrice, String pdMrp, String perOff, String pdDetail, long pdQuantity, boolean inStock, int index, long maxQuantity) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_1)).into(productImage);
            productName.setText(pdName);
            productDetail.setText(pdDetail);

            if (inStock) {
                productPrice.setText("₹" + pdPrice);
                productMrp.setText("₹" + pdMrp);
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));

                productQuantity.setText(String.valueOf(pdQuantity));

                String mrp = productMrp.getText().toString().substring(1);
                String price = productPrice.getText().toString().substring(1);

                if (!price.equals(mrp)) {
                    productMrp.setPaintFlags(productMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    double a = Double.parseDouble(mrp);
                    double b = Double.parseDouble(price);
                    double c;
                    int d;

                    c = ((a - b) / a) * 100;
                    d = (int) c;

                    perOff = String.valueOf(d);

                    percentOff.setText(perOff);
                } else {
                    productMrp.setVisibility(View.GONE);
                    percContainer.setVisibility(View.GONE);
                }

            } else {
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                productMrp.setVisibility(View.GONE);
                percContainer.setVisibility(View.GONE);
                incDecContainer.setVisibility(View.GONE);
            }

            productQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals("1")){
                        decQuantity.setVisibility(View.GONE);
                        deleteBtn.setVisibility(View.VISIBLE);
                    }else {
                        decQuantity.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.GONE);
                    }
                }
            });

            qty = (int) pdQuantity;
            incQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Long.parseLong(productQuantity.getText().toString()) < maxQuantity) {
                        qty++;
                        cartModelList.get(index).setProductQuantity((long) qty);
                        productQuantity.setText(String.valueOf(qty));
                    } else {
                        Toast.makeText(itemView.getContext(), "You cannot add more of this product", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            decQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty--;
                    cartModelList.get(index).setProductQuantity((long) qty);
                    productQuantity.setText(String.valueOf(qty));
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DatabaseQueries.removeFromCart(index, itemView.getContext(), cartTotalAmount);
                    }
                }
            });

        }
    }

    class CartPriceViewHolder extends RecyclerView.ViewHolder {

        private TextView totalPrice;
        private TextView deliveryPrice;
        private TextView savedAmount;
        private TextView grandTotal;

        public CartPriceViewHolder(@NonNull View itemView) {
            super(itemView);

            totalPrice = itemView.findViewById(R.id.cart_total_price);
            deliveryPrice = itemView.findViewById(R.id.cart_delivery_price);
            savedAmount = itemView.findViewById(R.id.cart_amount_saved);
            grandTotal = itemView.findViewById(R.id.cart_grand_total);

        }

        private void setCartPrices(int totPrice, String delPrice, int saveAmount, int granTotal) {
            totalPrice.setText(String.valueOf(totPrice));
            deliveryPrice.setText(delPrice);
            savedAmount.setText(String.valueOf(saveAmount));
            grandTotal.setText(String.valueOf(granTotal));
            cartTotalAmount.setText(String.valueOf(granTotal));

            LinearLayout addToCartBtn = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totPrice == 0) {
                cartModelList.remove(cartModelList.size() - 1);
                addToCartBtn.setVisibility(View.GONE);
            } else {
                addToCartBtn.setVisibility(View.VISIBLE);
            }
        }

    }
}
