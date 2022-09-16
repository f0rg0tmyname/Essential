package com.android.retail.hawk;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;


    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String name = horizontalProductScrollModelList.get(position).getProductName();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();
        String mrp = horizontalProductScrollModelList.get(position).getProductMrp();
        String detail = horizontalProductScrollModelList.get(position).getProductDetail();
        String percOff = horizontalProductScrollModelList.get(position).getPercentOff();
        String productId = horizontalProductScrollModelList.get(position).getProductID();

        holder.setData(productId, resource, name,detail, price, mrp, percOff);
    }

    @Override
    public int getItemCount() {
        if (horizontalProductScrollModelList.size() > 8) {
            return 8;
        } else {
            return horizontalProductScrollModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productMrp;
        private TextView productDetail;
        private TextView percentOff;
        private LinearLayout percentOffContainer;
        private View mrpStroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.hor_item_product_img);
            productName = itemView.findViewById(R.id.hor_item_name);
            productPrice = itemView.findViewById(R.id.hor_item_product_price);
            productMrp = itemView.findViewById(R.id.hor_item_mrp);
            productDetail = itemView.findViewById(R.id.hor_item_detail);
            percentOff = itemView.findViewById(R.id.hor_item_percent_off);
            percentOffContainer = itemView.findViewById(R.id.percent_off_container);
            mrpStroke = itemView.findViewById(R.id.mrp_stroke);

        }

        private void setData(String productId, String resource,String name, String detail, String price, String mrp, String percOff) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_1)).into(productImage);
            productName.setText(name);
            productDetail.setText(detail);
            productPrice.setText("₹"+price);
            productMrp.setText("₹"+mrp);

            if (productMrp.getText().toString().equals(productPrice.getText().toString())) {

                productMrp.setVisibility(View.GONE);
                percentOffContainer.setVisibility(View.GONE);
                mrpStroke.setVisibility(View.GONE);

            }else if (!productMrp.getText().toString().equals("") && !productPrice.getText().toString().equals("")) {

                productMrp.setVisibility(View.VISIBLE);
                percentOffContainer.setVisibility(View.VISIBLE);
                mrpStroke.setVisibility(View.VISIBLE);

                String pdMrp = productMrp.getText().toString().replace("₹", "");
                String pdPrice = productPrice.getText().toString().replace("₹", "");

                double a = Double.parseDouble(pdMrp);
                double b = Double.parseDouble(pdPrice);
                double c;
                int d;

                c = ((a - b) / a) * 100;
                d = (int) c;

                percOff = String.valueOf(d);

                percentOff.setText(percOff);
            }

            if (!productName.equals("")){
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
}
