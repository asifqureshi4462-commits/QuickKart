package com.quickkart.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.activities.ProductListActivity;
import com.quickkart.app.adapters.CategoryGridAdapter;
import com.quickkart.app.db.DatabaseHelper;

public class CategoriesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recycler = view.findViewById(R.id.allCategoriesRecycler);
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
        recycler.setAdapter(new CategoryGridAdapter(requireContext(), db.getAllCategories(), category -> {
            Intent intent = new Intent(requireContext(), ProductListActivity.class);
            intent.putExtra("mode", "category");
            intent.putExtra("catId", category.id);
            intent.putExtra("catName", category.name);
            startActivity(intent);
        }));
    }
}
