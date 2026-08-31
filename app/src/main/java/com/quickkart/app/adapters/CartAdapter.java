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
import com.quickkart.app.models.CartItem;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.ImageMapper;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    public interface CartActionListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onRemove(CartItem item);
    }

    private final Context context;
    private final List<CartItem> items;
    private final CartActionListener listener;

    public CartAdapter(Context context, List<CartItem> items, CartActionListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.name);
        holder.price.setText(CurrencyUtils.format(item.getSubtotal()));
        holder.qty.setText(String.valueOf(item.quantity));
        holder.image.setImageResource(ImageMapper.resolve(context, item.imageKey));

        holder.minus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                listener.onQuantityChanged(item, item.quantity);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.plus.setOnClickListener(v -> {
            if (item.quantity < item.stock) {
                item.quantity++;
                listener.onQuantityChanged(item, item.quantity);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.delete.setOnClickListener(v -> listener.onRemove(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image, minus, plus, delete;
        TextView name, price, qty;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartItemImage);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            qty = itemView.findViewById(R.id.cartQtyValue);
            minus = itemView.findViewById(R.id.cartQtyMinus);
            plus = itemView.findViewById(R.id.cartQtyPlus);
            delete = itemView.findViewById(R.id.cartDelete);
        }
    }
}
