package com.quickkart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.models.OrderItem;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.ImageMapper;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.VH> {

    private final Context context;
    private final List<OrderItem> items;

    public OrderItemAdapter(Context context, List<OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderItem item = items.get(position);
        holder.name.setText(item.productName);
        holder.qty.setText("Qty: " + item.quantity);
        holder.price.setText(CurrencyUtils.format(item.price * item.quantity));
        holder.image.setImageResource(ImageMapper.resolve(context, item.imageKey));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, qty, price;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.oiImage);
            name = itemView.findViewById(R.id.oiName);
            qty = itemView.findViewById(R.id.oiQty);
            price = itemView.findViewById(R.id.oiPrice);
        }
    }
}
