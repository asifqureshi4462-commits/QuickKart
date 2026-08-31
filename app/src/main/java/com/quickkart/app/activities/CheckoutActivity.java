package com.quickkart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.CartItem;
import com.quickkart.app.models.User;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.SessionManager;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private EditText nameInput, phoneInput, addressInput;
    private TextView itemsTotalText, grandTotalText;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;

    private DatabaseHelper db;
    private SessionManager session;
    private List<CartItem> cartItems;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        nameInput = findViewById(R.id.checkoutName);
        phoneInput = findViewById(R.id.checkoutPhone);
        addressInput = findViewById(R.id.checkoutAddress);
        itemsTotalText = findViewById(R.id.summaryItemsTotal);
        grandTotalText = findViewById(R.id.summaryGrandTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        progressBar = findViewById(R.id.checkoutProgress);

        findViewById(R.id.checkoutBack).setOnClickListener(v -> finish());

        User user = db.getUserById(session.getUserId());
        if (user != null) {
            nameInput.setText(user.name);
            phoneInput.setText(user.phone);
            addressInput.setText(user.address);
        }

        cartItems = db.getCartItems(session.getUserId());
        total = 0;
        for (CartItem item : cartItems) total += item.getSubtotal();

        itemsTotalText.setText(CurrencyUtils.format(total));
        grandTotalText.setText(CurrencyUtils.format(total));

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill all delivery details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setLoading(true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            long orderId = db.placeOrder(session.getUserId(), cartItems, total, address, phone);
            db.updateUserProfile(session.getUserId(), name, phone, address);
            setLoading(false);

            if (orderId > 0) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }, 800);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPlaceOrder.setEnabled(!loading);
    }
}
