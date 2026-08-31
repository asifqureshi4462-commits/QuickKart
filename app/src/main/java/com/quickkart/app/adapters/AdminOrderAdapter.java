package com.quickkart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.models.Order;
import com.quickkart.app.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.VH> {

    public interface OnOrderClick {
        void onClick(Order order);
    }

    private final Context context;
    private final List<Order> orders;
    private final OnOrderClick listener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public AdminOrderAdapter(Context context, List<Order> orders, OnOrderClick listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order order = orders.get(position);
        holder.orderId.setText("Order #" + (1000 + order.id));
        holder.customer.setText(order.userName != null ? order.userName : "Unknown");
        holder.date.setText(DATE_FORMAT.format(order.createdAt));
        holder.total.setText(CurrencyUtils.format(order.totalAmount));
        holder.status.setText(order.status);

        switch (order.status) {
            case "Delivered":
                holder.status.setBackgroundResource(R.drawable.bg_chip_success);
                holder.status.setTextColor(context.getColor(R.color.success));
                break;
            case "Cancelled":
                holder.status.setBackgroundResource(R.drawable.bg_chip_danger);
                holder.status.setTextColor(context.getColor(R.color.danger));
                break;
            default:
                holder.status.setBackgroundResource(R.drawable.bg_chip_warning);
                holder.status.setTextColor(context.getColor(R.color.warning));
                break;
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView orderId, customer, date, total, status;

        VH(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.aoOrderId);
            customer = itemView.findViewById(R.id.aoCustomer);
            date = itemView.findViewById(R.id.aoDate);
            total = itemView.findViewById(R.id.aoTotal);
            status = itemView.findViewById(R.id.aoStatusChip);
        }
    }
}
