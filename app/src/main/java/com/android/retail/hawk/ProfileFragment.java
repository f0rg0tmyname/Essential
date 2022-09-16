package com.android.retail.hawk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;

import static com.android.retail.hawk.DatabaseQueries.currentUser;

public class ProfileFragment extends Fragment {

    private LinearLayout shopListBtn;
    private LinearLayout recentOrdersBtn;
    private LinearLayout savedAddressBtn;
    private LinearLayout logOutBtn;
    private TextView editProfileBtn, userMobile;

    public static final int MANAGE_ADDRESS = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        shopListBtn = view.findViewById(R.id.profile_shop_list_btn);
        recentOrdersBtn = view.findViewById(R.id.profile_recent_orders_btn);
        savedAddressBtn = view.findViewById(R.id.profile_saved_address_btn);
        logOutBtn = view.findViewById(R.id.profile_log_out_btn);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        userMobile = view.findViewById(R.id.user_mobile);

        userMobile.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        shopListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShoppingListActivity.class));
            }
        });

        recentOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This is Your Orders", Toast.LENGTH_SHORT).show();
            }
        });

        savedAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageAddressIntent = new Intent(getActivity(), AddressActivity.class);
                manageAddressIntent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(manageAddressIntent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    FirebaseAuth.getInstance().signOut();
                    currentUser = null;
                    DatabaseQueries.clearData();
                    Intent intent = new Intent(getActivity(), SendOTPActivity.class);
                    startActivity(intent);
                    getActivity().finish();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        return view;

    }
}
