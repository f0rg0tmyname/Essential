package com.android.retail.hawk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.tv.TvContentRating;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductSpecificationAdapter extends RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder> {

    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductSpecificationAdapter(List<ProductSpecificationModel> productSpecificationModelList) {
        this.productSpecificationModelList = productSpecificationModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (productSpecificationModelList.get(position).getType()) {
            case 0:
                return ProductSpecificationModel.SPEC_TITLE;
            case 1:
                return ProductSpecificationModel.SPEC_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public ProductSpecificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ProductSpecificationModel.SPEC_TITLE:
                TextView title = new TextView(parent.getContext());
                Typeface font = ResourcesCompat.getFont(parent.getContext(), R.font.amazon_ember_bold);
                title.setTypeface(font);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(setDp(16, parent.getContext())
                        , setDp(16, parent.getContext())
                        , setDp(0, parent.getContext())
                        , setDp(8, parent.getContext()));
                title.setLayoutParams(layoutParams);
                return new ViewHolder(title);

            case ProductSpecificationModel.SPEC_BODY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.specification_item_layout, parent, false);
                return new ViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecificationAdapter.ViewHolder holder, int position) {

        switch (productSpecificationModelList.get(position).getType()) {
            case ProductSpecificationModel.SPEC_TITLE:
                holder.setTitle(productSpecificationModelList.get(position).getTitle());
                break;
            case ProductSpecificationModel.SPEC_BODY:
                String specTitle = productSpecificationModelList.get(position).getSpecName();
                String specDetail = productSpecificationModelList.get(position).getSpecValue();

                holder.setSpecs(specTitle, specDetail);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return productSpecificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView specName;
        private TextView specValue;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setTitle(String titleText) {
            title = (TextView) itemView;
            title.setText(titleText);
        }

        private void setSpecs(String specTitle, String specDetail) {
            specName = itemView.findViewById(R.id.spec_name);
            specValue = itemView.findViewById(R.id.spec_value);
            specName.setText(specTitle);
            specValue.setText(specDetail);
        }

    }

    private int setDp(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
