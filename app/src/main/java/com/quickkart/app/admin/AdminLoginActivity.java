package com.quickkart.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.utils.SessionManager;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private TextView errorText;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        usernameInput = findViewById(R.id.adminUsername);
        passwordInput = findViewById(R.id.adminPassword);
        errorText = findViewById(R.id.adminLoginError);

        findViewById(R.id.btnAdminLogin).setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        errorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            errorText.setText("Please enter username and password");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (db.loginAdmin(username, password)) {
            session.loginAdmin();
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
        } else {
            errorText.setText("Invalid admin credentials");
            errorText.setVisibility(View.VISIBLE);
        }
    }
}
