package com.quickkart.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.User;
import com.quickkart.app.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameInput, phoneInput, addressInput, emailInput;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        nameInput = findViewById(R.id.epName);
        phoneInput = findViewById(R.id.epPhone);
        addressInput = findViewById(R.id.epAddress);
        emailInput = findViewById(R.id.epEmail);

        findViewById(R.id.epBack).setOnClickListener(v -> finish());

        User user = db.getUserById(session.getUserId());
        if (user != null) {
            nameInput.setText(user.name);
            phoneInput.setText(user.phone);
            addressInput.setText(user.address);
            emailInput.setText(user.email);
        }

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> save());
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = db.updateUserProfile(session.getUserId(), name, phone, address);
        if (ok) {
            session.loginUser(session.getUserId(), name);
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
