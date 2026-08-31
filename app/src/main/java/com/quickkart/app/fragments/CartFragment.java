package com.quickkart.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.activities.CheckoutActivity;
import com.quickkart.app.activities.LoginActivity;
import com.quickkart.app.adapters.CartAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.CartItem;
import com.quickkart.app.utils.CurrencyUtils;
import com.quickkart.app.utils.SessionManager;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recycler;
    private View emptyView, summaryView;
    private TextView totalText;
    private DatabaseHelper db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());
        session = new SessionManager(requireContext());

        recycler = view.findViewById(R.id.cartRecycler);
        emptyView = view.findViewById(R.id.emptyCartView);
        summaryView = view.findViewById(R.id.cartSummary);
        totalText = view.findViewById(R.id.cartTotal);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        view.findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (!session.isUserLoggedIn()) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                return;
            }
            List<CartItem> items = db.getCartItems(session.getUserId());
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), CheckoutActivity.class));
        });

        loadCart();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        if (!session.isUserLoggedIn()) {
            emptyView.setVisibility(View.VISIBLE);
            summaryView.setVisibility(View.GONE);
            recycler.setVisibility(View.GONE);
            return;
        }

        List<CartItem> items = db.getCartItems(session.getUserId());
        if (items.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            summaryView.setVisibility(View.GONE);
            recycler.setVisibility(View.GONE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        summaryView.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.VISIBLE);

        recycler.setAdapter(new CartAdapter(requireContext(), items, new CartAdapter.CartActionListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQty) {
                db.updateCartQuantity(item.cartId, newQty);
                updateTotal(items);
            }

            @Override
            public void onRemove(CartItem item) {
                db.removeFromCart(item.cartId);
                loadCart();
            }
        }));

        updateTotal(items);
    }

    private void updateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) total += item.getSubtotal();
        totalText.setText(CurrencyUtils.format(total));
    }
}
