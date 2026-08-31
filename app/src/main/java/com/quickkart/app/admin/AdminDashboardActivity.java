package com.quickkart.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        if (!session.isAdminLoggedIn()) {
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
            return;
        }

        findViewById(R.id.adminLogoutIcon).setOnClickListener(v -> confirmLogout());

        findViewById(R.id.menuCategories).setOnClickListener(v ->
                startActivity(new Intent(this, AdminCategoryActivity.class)));
        findViewById(R.id.menuProducts).setOnClickListener(v ->
                startActivity(new Intent(this, AdminProductActivity.class)));
        findViewById(R.id.menuOrders).setOnClickListener(v ->
                startActivity(new Intent(this, AdminOrderActivity.class)));
        findViewById(R.id.menuUsers).setOnClickListener(v ->
                startActivity(new Intent(this, AdminUserActivity.class)));
        findViewById(R.id.menuAdminSettings).setOnClickListener(v ->
                startActivity(new Intent(this, AdminSettingActivity.class)));

        setupStatCard(R.id.statUsers, R.drawable.ic_users, String.valueOf(db.countRows(DatabaseHelper.TABLE_USERS)), "Total Users");
        setupStatCard(R.id.statProducts, R.drawable.ic_box, String.valueOf(db.countRows(DatabaseHelper.TABLE_PRODUCTS)), "Total Products");
        setupStatCard(R.id.statOrders, R.drawable.ic_orders, String.valueOf(db.countRows(DatabaseHelper.TABLE_ORDERS)), "Total Orders");
        setupStatCard(R.id.statRevenue, R.drawable.ic_money, CurrencyUtils.format(db.getTotalRevenue()), "Total Revenue");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db != null) {
            setupStatCard(R.id.statUsers, R.drawable.ic_users, String.valueOf(db.countRows(DatabaseHelper.TABLE_USERS)), "Total Users");
            setupStatCard(R.id.statProducts, R.drawable.ic_box, String.valueOf(db.countRows(DatabaseHelper.TABLE_PRODUCTS)), "Total Products");
            setupStatCard(R.id.statOrders, R.drawable.ic_orders, String.valueOf(db.countRows(DatabaseHelper.TABLE_ORDERS)), "Total Orders");
            setupStatCard(R.id.statRevenue, R.drawable.ic_money, CurrencyUtils.format(db.getTotalRevenue()), "Total Revenue");
        }
    }

    private void setupStatCard(int cardId, int iconRes, String value, String label) {
        View card = findViewById(cardId);
        ((ImageView) card.findViewById(R.id.statIcon)).setImageResource(iconRes);
        ((TextView) card.findViewById(R.id.statValue)).setText(value);
        ((TextView) card.findViewById(R.id.statLabel)).setText(label);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Logout from admin panel?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    session.logoutAdmin();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
