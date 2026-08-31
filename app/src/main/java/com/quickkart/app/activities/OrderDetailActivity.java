package com.quickkart.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.OrderItemAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Order;
import com.quickkart.app.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        int orderId = getIntent().getIntExtra("orderId", -1);
        Order order = db.getOrderById(orderId);
        if (order == null) {
            finish();
            return;
        }

        findViewById(R.id.odBack).setOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.odOrderId)).setText("Order #" + (1000 + order.id));

        TextView statusView = findViewById(R.id.odStatus);
        statusView.setText(order.status);
        switch (order.status) {
            case "Delivered":
                statusView.setBackgroundResource(R.drawable.bg_chip_success);
                statusView.setTextColor(getColor(R.color.success));
                break;
            case "Cancelled":
                statusView.setBackgroundResource(R.drawable.bg_chip_danger);
                statusView.setTextColor(getColor(R.color.danger));
                break;
            default:
                statusView.setBackgroundResource(R.drawable.bg_chip_warning);
                statusView.setTextColor(getColor(R.color.warning));
                break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        ((TextView) findViewById(R.id.odDate)).setText("Placed on " + sdf.format(order.createdAt));
        ((TextView) findViewById(R.id.odAddress)).setText(order.address);
        ((TextView) findViewById(R.id.odPhone)).setText("Phone: " + order.phone);
        ((TextView) findViewById(R.id.odTotal)).setText(CurrencyUtils.format(order.totalAmount));

        RecyclerView recycler = findViewById(R.id.odItemsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new OrderItemAdapter(this, order.items));
    }
}
