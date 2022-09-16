package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ProductDescriptionAdapter extends FragmentPagerAdapter {

    private int totalTabs;
    private String description;
    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductDescriptionAdapter(@NonNull FragmentManager fm, int totalTabs, String description, List<ProductSpecificationModel> productSpecificationModelList) {
        super(fm);
        this.totalTabs = totalTabs;
        this.description = description;
        this.productSpecificationModelList = productSpecificationModelList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ProductDescriptionFragment productDescriptionFragment = new ProductDescriptionFragment();
                productDescriptionFragment.productDescription = description;
                return productDescriptionFragment;
            case 1:
                ProductSpecificationsFragment productSpecificationsFragment = new ProductSpecificationsFragment();
                productSpecificationsFragment.productSpecificationModelList = productSpecificationModelList;
                return productSpecificationsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
