package com.android.retail.hawk;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrdersModel> ordersModelList;

    public OrdersAdapter(List<OrdersModel> ordersModelList) {
        this.ordersModelList = ordersModelList;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        int resource = ordersModelList.get(position).getProductImage();
        String name = ordersModelList.get(position).getProductName();
        String delStatus = ordersModelList.get(position).getDeliveryStatus();

        holder.setData(resource, name, delStatus);
    }

    @Override
    public int getItemCount() {
        return ordersModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName;
        private TextView deliveryStatus;
        private ImageView deliveryIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.order_pd_image);
            productName = itemView.findViewById(R.id.order_pd_name);
            deliveryStatus = itemView.findViewById(R.id.order_del_status);
            deliveryIndicator = itemView.findViewById(R.id.order_del_status_indicator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),OrderDetailsActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        private void setData(int resource, String pdName, String delStatus){
            productImage.setImageResource(resource);
            productName.setText(pdName);
            deliveryStatus.setText(delStatus);
            if (delStatus.equals("Cancelled")){
                deliveryIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
            }else{
                deliveryIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.blue_accent)));
            }
        }
    }
}
