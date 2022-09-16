package com.android.retail.hawk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.android.retail.hawk.DatabaseQueries.categoryModelList;
import static com.android.retail.hawk.DatabaseQueries.loadCategories;

public class SearchFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        GridView gridView = view.findViewById(R.id.category_grid_view);

        //Category View
        CategoryAdapter adapter = new CategoryAdapter(categoryModelList);
        gridView.setAdapter(adapter);

        if (categoryModelList.size() == 0){
            loadCategories(adapter, getContext());
        }else {
            adapter.notifyDataSetChanged();
        }
        //Category View

        return view;
    }
}
