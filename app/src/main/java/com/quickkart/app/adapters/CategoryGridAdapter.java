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
import com.quickkart.app.models.Category;
import com.quickkart.app.utils.ImageMapper;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.VH> {

    private final Context context;
    private final List<Category> categories;
    private final CategoryAdapter.OnCategoryClick listener;

    public CategoryGridAdapter(Context context, List<Category> categories, CategoryAdapter.OnCategoryClick listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category_grid, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category cat = categories.get(position);
        holder.name.setText(cat.name);
        holder.image.setImageResource(ImageMapper.resolve(context, cat.imageKey));
        holder.itemView.setOnClickListener(v -> listener.onClick(cat));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gridCategoryImage);
            name = itemView.findViewById(R.id.gridCategoryName);
        }
    }
}
