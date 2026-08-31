package com.quickkart.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.utils.SessionManager;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPass, newPass, confirmPass;
    private TextView errorText;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        oldPass = findViewById(R.id.cpOldPassword);
        newPass = findViewById(R.id.cpNewPassword);
        confirmPass = findViewById(R.id.cpConfirmPassword);
        errorText = findViewById(R.id.cpError);

        findViewById(R.id.cpBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> submit());
    }

    private void submit() {
        String o = oldPass.getText().toString().trim();
        String n = newPass.getText().toString().trim();
        String c = confirmPass.getText().toString().trim();
        errorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(o) || TextUtils.isEmpty(n) || TextUtils.isEmpty(c)) {
            showError("Please fill all fields");
            return;
        }
        if (n.length() < 6) {
            showError("New password must be at least 6 characters");
            return;
        }
        if (!n.equals(c)) {
            showError("New passwords do not match");
            return;
        }

        boolean success = db.changeUserPassword(session.getUserId(), o, n);
        if (success) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showError("Current password is incorrect");
        }
    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }
}
