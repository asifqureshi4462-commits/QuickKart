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
import com.quickkart.app.adapters.AdminCategoryAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Category;

import java.util.List;

public class AdminCategoryActivity extends AppCompatActivity {

    private static final String[] ICON_KEYS = {
            "cat_mobiles", "cat_fashion", "cat_electronics", "cat_home",
            "cat_beauty", "cat_grocery", "cat_footwear", "cat_toys"
    };
    private static final String[] ICON_LABELS = {
            "Mobiles", "Fashion", "Electronics", "Home & Kitchen",
            "Beauty", "Grocery", "Footwear", "Toys"
    };

    private RecyclerView recycler;
    private View emptyView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        db = DatabaseHelper.getInstance(this);
        recycler = findViewById(R.id.listRecycler);
        emptyView = findViewById(R.id.listEmptyView);

        ((TextView) findViewById(R.id.listTitle)).setText("Manage Categories");
        ((TextView) findViewById(R.id.listEmptyText)).setText("No categories yet. Tap + to add one.");
        findViewById(R.id.listBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAdd).setOnClickListener(v -> showCategoryDialog(null));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        loadCategories();
    }

    private void loadCategories() {
        List<Category> categories = db.getAllCategories();
        emptyView.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(categories.isEmpty() ? View.GONE : View.VISIBLE);

        recycler.setAdapter(new AdminCategoryAdapter(this, categories, new AdminCategoryAdapter.Listener() {
            @Override
            public void onEdit(Category category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                confirmDelete(category);
            }
        }));
    }

    private void showCategoryDialog(Category existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        EditText nameInput = dialogView.findViewById(R.id.dialogCategoryName);
        Spinner iconSpinner = dialogView.findViewById(R.id.dialogCategoryIcon);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ICON_LABELS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iconSpinner.setAdapter(spinnerAdapter);

        if (existing != null) {
            nameInput.setText(existing.name);
            for (int i = 0; i < ICON_KEYS.length; i++) {
                if (ICON_KEYS[i].equals(existing.imageKey)) {
                    iconSpinner.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Add Category" : "Edit Category")
                .setView(dialogView)
                .setPositiveButton(existing == null ? "Add" : "Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String iconKey = ICON_KEYS[iconSpinner.getSelectedItemPosition()];
                    if (existing == null) {
                        db.addCategory(name, iconKey);
                        Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                    } else {
                        db.updateCategory(existing.id, name, iconKey);
                        Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show();
                    }
                    loadCategories();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Delete \"" + category.name + "\"? Products in this category will remain but lose their category.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteCategory(category.id);
                    Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
