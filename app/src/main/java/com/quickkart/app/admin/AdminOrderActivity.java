package com.quickkart.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.AdminOrderAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Order;

import java.util.List;

public class AdminOrderActivity extends AppCompatActivity {

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

        ((TextView) findViewById(R.id.listTitle)).setText("Manage Orders");
        ((TextView) findViewById(R.id.listEmptyText)).setText("No orders placed yet");
        findViewById(R.id.listBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAdd).setVisibility(View.GONE);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = db.getAllOrders();
        emptyView.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE);

        recycler.setAdapter(new AdminOrderAdapter(this, orders, order -> {
            Intent intent = new Intent(this, AdminOrderDetailActivity.class);
            intent.putExtra("orderId", order.id);
            startActivity(intent);
        }));
    }
}
