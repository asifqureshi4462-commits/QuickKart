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

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.VH> {

    public interface Listener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    private final Context context;
    private final List<Product> products;
    private final Listener listener;

    public AdminProductAdapter(Context context, List<Product> products, Listener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = products.get(position);
        holder.name.setText(p.name);
        holder.category.setText(p.categoryName);
        holder.price.setText(CurrencyUtils.format(p.price));
        holder.stock.setText("Stock: " + p.stock);
        holder.image.setImageResource(ImageMapper.resolve(context, p.imageKey));
        holder.edit.setOnClickListener(v -> listener.onEdit(p));
        holder.delete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image, edit, delete;
        TextView name, category, price, stock;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.apImage);
            name = itemView.findViewById(R.id.apName);
            category = itemView.findViewById(R.id.apCategory);
            price = itemView.findViewById(R.id.apPrice);
            stock = itemView.findViewById(R.id.apStock);
            edit = itemView.findViewById(R.id.apEdit);
            delete = itemView.findViewById(R.id.apDelete);
        }
    }
}
