package com.quickkart.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.quickkart.app.R;
import com.quickkart.app.activities.ChangePasswordActivity;
import com.quickkart.app.activities.EditProfileActivity;
import com.quickkart.app.activities.LoginActivity;
import com.quickkart.app.activities.MainActivity;
import com.quickkart.app.admin.AdminLoginActivity;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.User;
import com.quickkart.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private DatabaseHelper db;
    private SessionManager session;
    private TextView nameView, emailView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());
        session = new SessionManager(requireContext());

        nameView = view.findViewById(R.id.profileName);
        emailView = view.findViewById(R.id.profileEmail);

        view.findViewById(R.id.menuEditProfile).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditProfileActivity.class)));

        view.findViewById(R.id.menuChangePassword).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class)));

        view.findViewById(R.id.menuMyOrders).setOnClickListener(v ->
                ((MainActivity) requireActivity()).selectOrdersTab());

        view.findViewById(R.id.menuAdminLogin).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AdminLoginActivity.class)));

        view.findViewById(R.id.menuLogout).setOnClickListener(v -> confirmLogout());

        loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        if (!session.isUserLoggedIn()) return;
        User user = db.getUserById(session.getUserId());
        if (user != null) {
            nameView.setText(user.name);
            emailView.setText(user.email);
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    session.logoutUser();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
