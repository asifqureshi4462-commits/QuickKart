package com.quickkart.app.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickkart.app.R;
import com.quickkart.app.adapters.AdminUserAdapter;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.User;

import java.util.List;

public class AdminUserActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private View emptyView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        db = DatabaseHelper.getInstance(this);
        recycler = findViewById(R.id.listRecycler);
        emptyView = findViewById(R.id.listEmptyView);

        ((TextView) findViewById(R.id.listTitle)).setText("Manage Users");
        ((TextView) findViewById(R.id.listEmptyText)).setText("No registered users yet");
        findViewById(R.id.listBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAdd).setVisibility(View.GONE);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();
    }

    private void loadUsers() {
        List<User> users = db.getAllUsers();
        emptyView.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(users.isEmpty() ? View.GONE : View.VISIBLE);

        recycler.setAdapter(new AdminUserAdapter(this, users, user ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete User")
                        .setMessage("Delete account for \"" + user.name + "\"? Their order history will remain.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.deleteUser(user.id);
                            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()));
    }
}
