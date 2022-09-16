package com.android.retail.hawk;

import android.content.Intent;
import android.graphics.Paint;
import android.location.GnssAntennaInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {

    List<ShopListModel> shopListModelList;
    private Boolean shopList;
    private int lastPosition = -1;

    public ShopListAdapter(List<ShopListModel> shopListModelList, Boolean shopList) {
        this.shopListModelList = shopListModelList;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public ShopListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListAdapter.ViewHolder holder, int position) {
        String productId = shopListModelList.get(position).getProductId();
        String resource = shopListModelList.get(position).getProductImage();
        String name = shopListModelList.get(position).getProductName();
        String price = shopListModelList.get(position).getProductPrice();
        String mrp = shopListModelList.get(position).getProductMrp();
        String off = shopListModelList.get(position).getPercentOff();
        String detail = shopListModelList.get(position).getProductDetail();
        boolean inStock = shopListModelList.get(position).isInStock();

        holder.setData(productId, resource, name, price, mrp, off, detail, position, inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in_anim);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return shopListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productMrp;
        private TextView percentOff;
        private TextView productDetail;
        private LinearLayout addToCartBtn, percContainer;
        private ImageView removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.shoplist_pd_image);
            productName = itemView.findViewById(R.id.shoplist_pd_name);
            productPrice = itemView.findViewById(R.id.shoplist_pd_price);
            productMrp = itemView.findViewById(R.id.shoplist_pd_mrp);
            percentOff = itemView.findViewById(R.id.shoplist_pd_percent_off);
            productDetail = itemView.findViewById(R.id.shoplist_pd_detail);
            addToCartBtn = itemView.findViewById(R.id.shoplist_add_to_cart_btn);
            removeBtn = itemView.findViewById(R.id.shoplist_remove_btn);
            percContainer = itemView.findViewById(R.id.perc_container);

        }

        private void setData(String productId, String resource, String pdName, String pdPrice, String pdMrp, String percOff, String pdDetail, int index, boolean inStock) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_1)).into(productImage);
            productName.setText(pdName);
            productDetail.setText(pdDetail);

            if (shopList) {
                addToCartBtn.setVisibility(View.VISIBLE);
                removeBtn.setVisibility(View.VISIBLE);
            } else {
                removeBtn.setVisibility(View.GONE);
            }
            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(itemView, "Product added to Cart", Snackbar.LENGTH_LONG).show();
                }
            });
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_shopList_query) {
                        ProductDetailsActivity.running_shopList_query = true;
                        DatabaseQueries.removeFromShopList(index, itemView.getContext());
                        Snackbar.make(itemView, "Product removed from Shopping List", Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            if (inStock) {
                productPrice.setText("₹"+pdPrice);
                productMrp.setText("₹"+pdMrp);
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));

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

                    percOff = String.valueOf(d);

                    percentOff.setText(percOff);
                }else {
                    percContainer.setVisibility(View.GONE);
                    productMrp.setVisibility(View.GONE);
                }
            }else {
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                productMrp.setVisibility(View.GONE);
                addToCartBtn.setVisibility(View.GONE);
                percContainer.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID", productId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

    }
}
