package com.quickkart.app.delivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.utils.CurrencyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.VH> {

    public interface OnVerifyClick {
        void onVerify(int orderId, String orderCode);
    }

    private final Context context;
    private final JSONArray orders;
    private final OnVerifyClick listener;

    public DeliveryOrderAdapter(Context context, JSONArray orders, OnVerifyClick listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_delivery_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        JSONObject o = orders.optJSONObject(position);
        if (o == null) return;

        int orderId = o.optInt("id");
        String orderCode = o.optString("order_code");

        holder.orderCode.setText("Order #" + orderCode);
        holder.customerName.setText(o.optString("name"));
        holder.address.setText(o.optString("address"));
        holder.total.setText(CurrencyUtils.format(o.optDouble("total_amount", 0)));

        holder.verifyButton.setOnClickListener(v -> listener.onVerify(orderId, orderCode));
    }

    @Override
    public int getItemCount() {
        return orders.length();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView orderCode, customerName, address, total;
        android.widget.Button verifyButton;

        VH(@NonNull View itemView) {
            super(itemView);
            orderCode = itemView.findViewById(R.id.doOrderCode);
            customerName = itemView.findViewById(R.id.doCustomerName);
            address = itemView.findViewById(R.id.doAddress);
            total = itemView.findViewById(R.id.doTotal);
            verifyButton = itemView.findViewById(R.id.btnVerifyDelivery);
        }
    }
}
