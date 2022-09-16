package com.android.retail.hawk;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.android.retail.hawk.AddressActivity.refreshAddressView;
import static com.android.retail.hawk.DeliveryActivity.SELECT_ADDRESS;
import static com.android.retail.hawk.ProfileFragment.MANAGE_ADDRESS;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    private int MODE;
    private int preSelectedPosition;

    private List<AddressesModel> addressesModelList;

    public AddressesAdapter(List<AddressesModel> addressesModelList, int MODE) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DatabaseQueries.selectedAddress;
    }

    @NonNull
    @Override
    public AddressesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_address_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.ViewHolder holder, int position) {
        String name = addressesModelList.get(position).getFullName();
        String mAddress = addressesModelList.get(position).getAddress();
        String pin = addressesModelList.get(position).getPinCode();
        Boolean selected = addressesModelList.get(position).getSelected();

        holder.setData(name, mAddress, pin, selected, position);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView address;
        private TextView pinCode;
        private ImageView icon;
        private LinearLayout optionsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.sa_name);
            address = itemView.findViewById(R.id.sa_address);
            pinCode = itemView.findViewById(R.id.sa_pincode);
            icon = itemView.findViewById(R.id.sa_icon);
            optionsContainer = itemView.findViewById(R.id.options_container);
        }

        private void setData(String name, String mAddress, String pin, Boolean selected, int position) {
            fullName.setText(name);
            address.setText(mAddress);
            pinCode.setText(pin);

            if (MODE == SELECT_ADDRESS) {

                optionsContainer.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_check);
                icon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#D41F1F")));

                if (selected) {
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    icon.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshAddressView(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DatabaseQueries.selectedAddress = position;
                        }
                    }
                });

            } else if (MODE == MANAGE_ADDRESS) {

                optionsContainer.setVisibility(View.VISIBLE);
                icon.setVisibility(View.GONE);

            }
        }
    }
}
