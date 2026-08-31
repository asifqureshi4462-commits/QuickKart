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
import com.quickkart.app.models.Product;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.ImageMapper;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnProductAction {
        void onClick(Product product);
        void onAddToCart(Product product);
    }

    private final Context context;
    private final List<Product> products;
    private final OnProductAction listener;
    private final boolean compact;

    public ProductAdapter(Context context, List<Product> products, OnProductAction listener) {
        this(context, products, listener, false);
    }

    public ProductAdapter(Context context, List<Product> products, OnProductAction listener, boolean compact) {
        this.context = context;
        this.products = products;
        this.listener = listener;
        this.compact = compact;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(
                compact ? R.layout.item_product_compact : R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = products.get(position);
        holder.name.setText(p.name);
        holder.price.setText(CurrencyUtils.format(p.price));
        holder.image.setImageResource(ImageMapper.resolve(context, p.imageKey));

        if (p.stock <= 0) {
            holder.addToCart.setText("Out of Stock");
            holder.addToCart.setEnabled(false);
            holder.addToCart.setAlpha(0.5f);
        } else {
            holder.addToCart.setText("Add to Cart");
            holder.addToCart.setEnabled(true);
            holder.addToCart.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(p));
        holder.addToCart.setOnClickListener(v -> listener.onAddToCart(p));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        android.widget.Button addToCart;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            addToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
