package com.quickkart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.ProductAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Product;
import com.quickkart.app.utils.SessionManager;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView title, sortNew, sortPriceLow, sortPriceHigh;
    private EditText searchInput;
    private DatabaseHelper db;
    private SessionManager session;

    private String mode;
    private int catId;
    private String catName;
    private String currentSort = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        mode = getIntent().getStringExtra("mode");
        catId = getIntent().getIntExtra("catId", -1);
        catName = getIntent().getStringExtra("catName");

        recycler = findViewById(R.id.plRecycler);
        title = findViewById(R.id.plTitle);
        searchInput = findViewById(R.id.plSearchInput);
        sortNew = findViewById(R.id.sortNew);
        sortPriceLow = findViewById(R.id.sortPriceLow);
        sortPriceHigh = findViewById(R.id.sortPriceHigh);

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        title.setText("category".equals(mode) ? catName : "Search Products");

        sortNew.setOnClickListener(v -> setSort("new"));
        sortPriceLow.setOnClickListener(v -> setSort("price_low"));
        sortPriceHigh.setOnClickListener(v -> setSort("price_high"));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { loadProducts(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadProducts();
    }

    private void setSort(String sort) {
        currentSort = sort;
        sortNew.setBackgroundResource(sort.equals("new") ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        sortNew.setTextColor(getColor(sort.equals("new") ? R.color.white : R.color.text_secondary));
        sortPriceLow.setBackgroundResource(sort.equals("price_low") ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        sortPriceLow.setTextColor(getColor(sort.equals("price_low") ? R.color.white : R.color.text_secondary));
        sortPriceHigh.setBackgroundResource(sort.equals("price_high") ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        sortPriceHigh.setTextColor(getColor(sort.equals("price_high") ? R.color.white : R.color.text_secondary));
        loadProducts();
    }

    private void loadProducts() {
        List<Product> products;
        String keyword = searchInput.getText().toString().trim();

        if (!keyword.isEmpty()) {
            products = db.searchProducts(keyword, currentSort);
        } else if ("category".equals(mode) && catId != -1) {
            products = db.getProductsByCategory(catId);
        } else {
            products = db.getProductsSorted(currentSort);
        }

        recycler.setAdapter(new ProductAdapter(this, products, new ProductAdapter.OnProductAction() {
            @Override
            public void onClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("productId", product.id);
                startActivity(intent);
            }

            @Override
            public void onAddToCart(Product product) {
                if (!session.isUserLoggedIn()) return;
                db.addToCart(session.getUserId(), product.id, 1);
                Toast.makeText(ProductListActivity.this, product.name + " added to cart", Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
