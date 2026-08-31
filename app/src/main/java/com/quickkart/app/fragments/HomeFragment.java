package com.quickkart.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.quickkart.app.R;
import com.quickkart.app.activities.MainActivity;
import com.quickkart.app.activities.ProductDetailActivity;
import com.quickkart.app.activities.ProductListActivity;
import com.quickkart.app.adapters.CategoryAdapter;
import com.quickkart.app.adapters.ProductAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Category;
import com.quickkart.app.models.Product;
import com.quickkart.app.utils.SessionManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecycler, productRecycler;
    private SwipeRefreshLayout swipeRefresh;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());
        session = new SessionManager(requireContext());

        categoryRecycler = view.findViewById(R.id.categoryRecycler);
        productRecycler = view.findViewById(R.id.productRecycler);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        categoryRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        productRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        view.findViewById(R.id.searchInput).setOnClickListener(v -> openSearch());

        swipeRefresh.setOnRefreshListener(() -> {
            loadData();
            swipeRefresh.setRefreshing(false);
        });

        loadData();
    }

    private void openSearch() {
        startActivity(new android.content.Intent(requireContext(), ProductListActivity.class)
                .putExtra("mode", "search"));
    }

    private void loadData() {
        List<Category> categories = db.getAllCategories();
        categoryRecycler.setAdapter(new CategoryAdapter(requireContext(), categories, category -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), ProductListActivity.class);
            intent.putExtra("mode", "category");
            intent.putExtra("catId", category.id);
            intent.putExtra("catName", category.name);
            startActivity(intent);
        }));

        List<Product> products = db.getAllProducts();
        productRecycler.setAdapter(new ProductAdapter(requireContext(), products, new ProductAdapter.OnProductAction() {
            @Override
            public void onClick(Product product) {
                android.content.Intent intent = new android.content.Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("productId", product.id);
                startActivity(intent);
            }

            @Override
            public void onAddToCart(Product product) {
                if (!session.isUserLoggedIn()) return;
                db.addToCart(session.getUserId(), product.id, 1);
                Toast.makeText(requireContext(), product.name + " added to cart", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
