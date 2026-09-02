package com.quickkart.app.delivery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.quickkart.app.R;
import com.quickkart.app.network.ApiClient;

import org.json.JSONArray;

/**
 * Lists orders currently "Out for Delivery" (from api/delivery/orders.php).
 * Tapping an order opens DeliveryOtpActivity to enter the customer's OTP.
 * This screen never sees or stores the OTP itself.
 */
public class DeliveryOrdersActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private View emptyView;
    private SwipeRefreshLayout swipeRefresh;
    private DeliverySession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_orders);

        session = new DeliverySession(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, DeliveryLoginActivity.class));
            finish();
            return;
        }

        recycler = findViewById(R.id.doRecycler);
        emptyView = findViewById(R.id.doEmptyView);
        swipeRefresh = findViewById(R.id.doSwipeRefresh);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.doLogout).setOnClickListener(v -> confirmLogout());
        swipeRefresh.setOnRefreshListener(this::loadOrders);

        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(); // refresh after coming back from a successful OTP verification
    }

    private void loadOrders() {
        new LoadOrdersTask().execute();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Logout from delivery agent panel?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    session.logout();
                    startActivity(new Intent(this, DeliveryLoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class LoadOrdersTask extends AsyncTask<Void, Void, ApiClient.ApiResult> {
        @Override
        protected ApiClient.ApiResult doInBackground(Void... voids) {
            return ApiClient.get("orders.php", session.getToken());
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            swipeRefresh.setRefreshing(false);

            if (result.httpCode == 401) {
                Toast.makeText(DeliveryOrdersActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                session.logout();
                startActivity(new Intent(DeliveryOrdersActivity.this, DeliveryLoginActivity.class));
                finish();
                return;
            }

            if (!result.success || result.raw == null) {
                Toast.makeText(DeliveryOrdersActivity.this, result.message, Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray orders = result.raw.optJSONArray("orders");
            if (orders == null) orders = new JSONArray();

            boolean empty = orders.length() == 0;
            emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
            recycler.setVisibility(empty ? View.GONE : View.VISIBLE);

            recycler.setAdapter(new DeliveryOrderAdapter(DeliveryOrdersActivity.this, orders, (orderId, orderCode) -> {
                Intent intent = new Intent(DeliveryOrdersActivity.this, DeliveryOtpActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("orderCode", orderCode);
                startActivity(intent);
            }));
        }
    }
}
