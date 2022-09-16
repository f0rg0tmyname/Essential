package com.android.retail.hawk;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @Override
    public int getCount() {
        return categoryModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, null);
            ConstraintLayout categoryItemContainer = view.findViewById(R.id.cat_item_container);

            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in_anim);
            view.setAnimation(animation);

            categoryItemContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(categoryModelList.get(position).getBgColor())));
            ImageView categoryImage = view.findViewById(R.id.category_image);
            Glide.with(parent.getContext()).load(categoryModelList.get(position).getCategoryImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder_1)).into(categoryImage);
            TextView categoryName = view.findViewById(R.id.category_name);
            categoryName.setText(categoryModelList.get(position).getCategoryName());

            String name = categoryName.getText().toString();

            if (!categoryName.equals("")) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent categoryIntent = new Intent(view.getContext(), CategoryActivity.class);
                        categoryIntent.putExtra("CategoryName", name);
                        view.getContext().startActivity(categoryIntent);
                    }
                });
            }
        } else {
            view = convertView;
        }
        return view;
    }
}
