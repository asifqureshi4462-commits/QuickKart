package com.quickkart.app.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.AdminProductAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Category;
import com.quickkart.app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AdminProductActivity extends AppCompatActivity {

    private static final String[] IMAGE_KEYS = {
            "product_1", "product_2", "product_3", "product_4", "product_5", "product_6",
            "product_7", "product_8", "product_9", "product_10", "product_11", "product_12"
    };

    private RecyclerView recycler;
    private View emptyView;
    private DatabaseHelper db;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        db = DatabaseHelper.getInstance(this);
        recycler = findViewById(R.id.listRecycler);
        emptyView = findViewById(R.id.listEmptyView);

        ((TextView) findViewById(R.id.listTitle)).setText("Manage Products");
        ((TextView) findViewById(R.id.listEmptyText)).setText("No products yet. Tap + to add one.");
        findViewById(R.id.listBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            if (categories.isEmpty()) {
                Toast.makeText(this, "Please add a category first", Toast.LENGTH_SHORT).show();
                return;
            }
            showProductDialog(null);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        loadProducts();
    }

    private void loadProducts() {
        categories = db.getAllCategories();
        List<Product> products = db.getAllProducts();
        emptyView.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);

        recycler.setAdapter(new AdminProductAdapter(this, products, new AdminProductAdapter.Listener() {
            @Override
            public void onEdit(Product product) {
                showProductDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                confirmDelete(product);
            }
        }));
    }

    private void showProductDialog(Product existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        EditText nameInput = dialogView.findViewById(R.id.dialogProductName);
        EditText descInput = dialogView.findViewById(R.id.dialogProductDescription);
        EditText priceInput = dialogView.findViewById(R.id.dialogProductPrice);
        EditText stockInput = dialogView.findViewById(R.id.dialogProductStock);
        Spinner categorySpinner = dialogView.findViewById(R.id.dialogProductCategory);
        Spinner imageSpinner = dialogView.findViewById(R.id.dialogProductImage);

        List<String> catNames = new ArrayList<>();
        for (Category c : categories) catNames.add(c.name);
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, catNames);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(catAdapter);

        List<String> imageLabels = new ArrayList<>();
        for (int i = 1; i <= IMAGE_KEYS.length; i++) imageLabels.add("Image " + i);
        ArrayAdapter<String> imgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, imageLabels);
        imgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(imgAdapter);

        if (existing != null) {
            nameInput.setText(existing.name);
            descInput.setText(existing.description);
            priceInput.setText(String.valueOf((int) existing.price));
            stockInput.setText(String.valueOf(existing.stock));
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).id == existing.categoryId) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < IMAGE_KEYS.length; i++) {
                if (IMAGE_KEYS[i].equals(existing.imageKey)) {
                    imageSpinner.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Add Product" : "Edit Product")
                .setView(dialogView)
                .setPositiveButton(existing == null ? "Add" : "Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String priceStr = priceInput.getText().toString().trim();
                    String stockStr = stockInput.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(stockStr)) {
                        Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    int stock;
                    try {
                        price = Double.parseDouble(priceStr);
                        stock = Integer.parseInt(stockStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid price and stock", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int catId = categories.get(categorySpinner.getSelectedItemPosition()).id;
                    String imageKey = IMAGE_KEYS[imageSpinner.getSelectedItemPosition()];

                    if (existing == null) {
                        db.addProduct(catId, name, desc, price, stock, imageKey);
                        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                    } else {
                        db.updateProduct(existing.id, catId, name, desc, price, stock, imageKey);
                        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
                    }
                    loadProducts();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Delete \"" + product.name + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteProduct(product.id);
                    Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                    loadProducts();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
