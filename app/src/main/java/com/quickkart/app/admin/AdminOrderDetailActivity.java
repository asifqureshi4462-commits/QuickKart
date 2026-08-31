package com.quickkart.app.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.OrderItemAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Order;
import com.quickkart.app.utils.CurrencyUtils;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private static final String[] STATUSES = {"Placed", "Dispatched", "Delivered", "Cancelled"};

    private DatabaseHelper db;
    private Order order;
    private Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        db = DatabaseHelper.getInstance(this);
        int orderId = getIntent().getIntExtra("orderId", -1);
        order = db.getOrderById(orderId);
        if (order == null) {
            finish();
            return;
        }

        findViewById(R.id.aodBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.aodOrderId)).setText("Order #" + (1000 + order.id));
        ((TextView) findViewById(R.id.aodCustomer)).setText(order.userName != null ? order.userName : "Unknown Customer");
        ((TextView) findViewById(R.id.aodAddress)).setText(order.address);
        ((TextView) findViewById(R.id.aodPhone)).setText("Phone: " + order.phone);
        ((TextView) findViewById(R.id.aodTotal)).setText(CurrencyUtils.format(order.totalAmount));

        statusSpinner = findViewById(R.id.aodStatusSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATUSES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        for (int i = 0; i < STATUSES.length; i++) {
            if (STATUSES[i].equals(order.status)) {
                statusSpinner.setSelection(i);
                break;
            }
        }

        findViewById(R.id.btnUpdateStatus).setOnClickListener(v -> {
            String newStatus = STATUSES[statusSpinner.getSelectedItemPosition()];
            db.updateOrderStatus(order.id, newStatus);
            Toast.makeText(this, "Order status updated to " + newStatus, Toast.LENGTH_SHORT).show();
            finish();
        });

        RecyclerView recycler = findViewById(R.id.aodItemsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new OrderItemAdapter(this, order.items));
    }
}
