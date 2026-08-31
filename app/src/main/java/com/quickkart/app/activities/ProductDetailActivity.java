package com.quickkart.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.ProductAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Product;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.ImageMapper;
import com.quickkart.app.utils.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private Product product;
    private DatabaseHelper db;
    private SessionManager session;
    private TextView qtyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        int productId = getIntent().getIntExtra("productId", -1);
        product = db.getProductById(productId);
        if (product == null) {
            finish();
            return;
        }

        ImageView image = findViewById(R.id.pdImage);
        TextView category = findViewById(R.id.pdCategory);
        TextView name = findViewById(R.id.pdName);
        TextView price = findViewById(R.id.pdPrice);
        TextView stock = findViewById(R.id.pdStock);
        TextView description = findViewById(R.id.pdDescription);
        qtyValue = findViewById(R.id.pdQtyValue);

        image.setImageResource(ImageMapper.resolve(this, product.imageKey));
        category.setText(product.categoryName);
        name.setText(product.name);
        price.setText(CurrencyUtils.format(product.price));
        description.setText(product.description);

        if (product.stock > 0) {
            stock.setText("In Stock (" + product.stock + ")");
            stock.setTextColor(getColor(R.color.success));
        } else {
            stock.setText("Out of Stock");
            stock.setTextColor(getColor(R.color.danger));
        }

        findViewById(R.id.pdBack).setOnClickListener(v -> finish());
        findViewById(R.id.pdQtyMinus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                qtyValue.setText(String.valueOf(quantity));
            }
        });
        findViewById(R.id.pdQtyPlus).setOnClickListener(v -> {
            if (quantity < product.stock) {
                quantity++;
                qtyValue.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Only " + product.stock + " left in stock", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnAddToCartDetail).setOnClickListener(v -> {
            if (!session.isUserLoggedIn()) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (product.stock <= 0) {
                Toast.makeText(this, "This product is out of stock", Toast.LENGTH_SHORT).show();
                return;
            }
            db.addToCart(session.getUserId(), product.id, quantity);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        RecyclerView relatedRecycler = findViewById(R.id.pdRelatedRecycler);
        relatedRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedRecycler.setAdapter(new ProductAdapter(this, db.getRelatedProducts(product.categoryId, product.id),
                new ProductAdapter.OnProductAction() {
                    @Override
                    public void onClick(Product p) {
                        android.content.Intent intent = new android.content.Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                        intent.putExtra("productId", p.id);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAddToCart(Product p) {
                        if (!session.isUserLoggedIn()) return;
                        db.addToCart(session.getUserId(), p.id, 1);
                        Toast.makeText(ProductDetailActivity.this, p.name + " added to cart", Toast.LENGTH_SHORT).show();
                    }
                }, true));
    }
}
