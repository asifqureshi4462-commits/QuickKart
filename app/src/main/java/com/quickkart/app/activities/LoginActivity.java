package com.quickkart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.admin.AdminLoginActivity;
import com.quickkart.app.db.DatabaseHelper;
import com.quickkart.app.models.User;
import com.quickkart.app.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextView tabLogin, tabSignup, adminLoginLink, loginError, signupError;
    private View loginForm, signupForm;
    private EditText loginEmail, loginPassword;
    private EditText signupName, signupPhone, signupEmail, signupPassword;
    private Button btnLogin, btnSignup;
    private ProgressBar progressBar;

    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        tabLogin = findViewById(R.id.tabLogin);
        tabSignup = findViewById(R.id.tabSignup);
        loginForm = findViewById(R.id.loginForm);
        signupForm = findViewById(R.id.signupForm);
        adminLoginLink = findViewById(R.id.adminLoginLink);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginError = findViewById(R.id.loginError);
        btnLogin = findViewById(R.id.btnLogin);

        signupName = findViewById(R.id.signupName);
        signupPhone = findViewById(R.id.signupPhone);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupError = findViewById(R.id.signupError);
        btnSignup = findViewById(R.id.btnSignup);

        progressBar = findViewById(R.id.authProgress);

        tabLogin.setOnClickListener(v -> showLoginTab());
        tabSignup.setOnClickListener(v -> showSignupTab());

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnSignup.setOnClickListener(v -> attemptSignup());

        adminLoginLink.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class)));
    }

    private void showLoginTab() {
        tabLogin.setBackgroundResource(R.drawable.bg_tab_selected);
        tabLogin.setTextColor(getColor(R.color.white));
        tabSignup.setBackground(null);
        tabSignup.setTextColor(getColor(R.color.text_secondary));
        loginForm.setVisibility(View.VISIBLE);
        signupForm.setVisibility(View.GONE);
    }

    private void showSignupTab() {
        tabSignup.setBackgroundResource(R.drawable.bg_tab_selected);
        tabSignup.setTextColor(getColor(R.color.white));
        tabLogin.setBackground(null);
        tabLogin.setTextColor(getColor(R.color.text_secondary));
        signupForm.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);
    }

    // Simulated AJAX-style async call using a short delay + background-safe DB call
    private void attemptLogin() {
        String email = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();
        loginError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showLoginError("Please enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            showLoginError("Please enter your password");
            return;
        }

        setLoading(true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            User user = db.loginUser(email, pass);
            setLoading(false);
            if (user != null) {
                session.loginUser(user.id, user.name);
                Toast.makeText(this, "Welcome back, " + user.name + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                showLoginError("Invalid email or password");
            }
        }, 600);
    }

    private void attemptSignup() {
        String name = signupName.getText().toString().trim();
        String phone = signupPhone.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String pass = signupPassword.getText().toString().trim();
        signupError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(name)) {
            showSignupError("Please enter your name");
            return;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            showSignupError("Please enter a valid phone number");
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showSignupError("Please enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            showSignupError("Password must be at least 6 characters");
            return;
        }

        setLoading(true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (db.isEmailTaken(email)) {
                setLoading(false);
                showSignupError("This email is already registered");
                return;
            }
            long userId = db.registerUser(name, phone, email, pass);
            setLoading(false);
            if (userId > 0) {
                session.loginUser((int) userId, name);
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                showSignupError("Something went wrong. Please try again.");
            }
        }, 600);
    }

    private void showLoginError(String msg) {
        loginError.setText(msg);
        loginError.setVisibility(View.VISIBLE);
    }

    private void showSignupError(String msg) {
        signupError.setText(msg);
        signupError.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnSignup.setEnabled(!loading);
    }
}
