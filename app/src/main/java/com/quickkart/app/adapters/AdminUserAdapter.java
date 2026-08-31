package com.quickkart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.VH> {

    public interface Listener {
        void onDelete(User user);
    }

    private final Context context;
    private final List<User> users;
    private final Listener listener;

    public AdminUserAdapter(Context context, List<User> users, Listener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.name);
        holder.email.setText(user.email);
        holder.phone.setText("Phone: " + user.phone);
        holder.delete.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, phone;
        View delete;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.auName);
            email = itemView.findViewById(R.id.auEmail);
            phone = itemView.findViewById(R.id.auPhone);
            delete = itemView.findViewById(R.id.auDelete);
        }
    }
}
