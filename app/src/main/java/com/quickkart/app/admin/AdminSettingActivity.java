package com.quickkart.app.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.db.DatabaseHelper;

public class AdminSettingActivity extends AppCompatActivity {

    private EditText currentPasswordInput, newUsernameInput, newPasswordInput;
    private TextView errorText;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_setting);

        db = DatabaseHelper.getInstance(this);

        currentPasswordInput = findViewById(R.id.asCurrentPassword);
        newUsernameInput = findViewById(R.id.asNewUsername);
        newPasswordInput = findViewById(R.id.asNewPassword);
        errorText = findViewById(R.id.asError);

        findViewById(R.id.asBack).setOnClickListener(v -> finish());

        newUsernameInput.setText(db.getAdminUsername());

        findViewById(R.id.btnUpdateAdmin).setOnClickListener(v -> submit());
    }

    private void submit() {
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newUsername = newUsernameInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        errorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(currentPassword)) {
            showError("Please enter your current password to confirm");
            return;
        }
        if (TextUtils.isEmpty(newUsername)) {
            showError("Username cannot be empty");
            return;
        }
        if (!newPassword.isEmpty() && newPassword.length() < 6) {
            showError("New password must be at least 6 characters");
            return;
        }

        // Verify current password against existing admin username before allowing update
        String existingUsername = db.getAdminUsername();
        if (!db.loginAdmin(existingUsername, currentPassword)) {
            showError("Current password is incorrect");
            return;
        }

        boolean ok = db.updateAdminCredentials(newUsername, newPassword.isEmpty() ? null : newPassword);
        if (ok) {
            Toast.makeText(this, "Admin credentials updated successfully", Toast.LENGTH_SHORT).show();
            currentPasswordInput.setText("");
            newPasswordInput.setText("");
        } else {
            showError("Something went wrong. Please try again.");
        }
    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }
}
