package com.quickkart.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.activities.OrderDetailActivity;
import com.quickkart.app.adapters.OrderAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.Order;
import com.quickkart.app.utils.SessionManager;

import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recycler;
    private View emptyView;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());
        session = new SessionManager(requireContext());

        recycler = view.findViewById(R.id.ordersRecycler);
        emptyView = view.findViewById(R.id.emptyOrdersView);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        if (!session.isUserLoggedIn()) {
            emptyView.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }
        List<Order> orders = db.getOrdersByUser(session.getUserId());
        if (orders.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }
        emptyView.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        recycler.setAdapter(new OrderAdapter(requireContext(), orders, order -> {
            Intent intent = new Intent(requireContext(), OrderDetailActivity.class);
            intent.putExtra("orderId", order.id);
            startActivity(intent);
        }));
    }
}
