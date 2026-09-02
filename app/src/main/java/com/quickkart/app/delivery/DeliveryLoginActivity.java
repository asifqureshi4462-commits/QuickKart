package com.quickkart.app.delivery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.network.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Delivery agent login. The app never generates or checks an OTP — this
 * screen only authenticates the agent against the PHP backend so the agent
 * has a bearer token for the OTP-entry screen (DeliveryOtpActivity).
 */
public class DeliveryLoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private TextView errorText;
    private Button loginButton;
    private ProgressBar progressBar;
    private DeliverySession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_login);

        session = new DeliverySession(this);
        if (session.isLoggedIn()) {
            goToOrders();
            return;
        }

        usernameInput = findViewById(R.id.dlUsername);
        passwordInput = findViewById(R.id.dlPassword);
        errorText = findViewById(R.id.dlError);
        loginButton = findViewById(R.id.btnDeliveryLogin);
        progressBar = findViewById(R.id.dlProgress);

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        errorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showError("Please enter username and password");
            return;
        }

        setLoading(true);
        new LoginTask().execute(username, password);
    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
    }

    private void goToOrders() {
        startActivity(new Intent(this, DeliveryOrdersActivity.class));
        finish();
    }

    /** Background network call — ApiClient.post() is synchronous. */
    private class LoginTask extends AsyncTask<String, Void, ApiClient.ApiResult> {
        @Override
        protected ApiClient.ApiResult doInBackground(String... params) {
            try {
                JSONObject body = new JSONObject();
                body.put("username", params[0]);
                body.put("password", params[1]);
                return ApiClient.post("login.php", body, null);
            } catch (JSONException e) {
                ApiClient.ApiResult err = new ApiClient.ApiResult();
                err.success = false;
                err.message = "Request build error.";
                return err;
            }
        }

        @Override
        protected void onPostExecute(ApiClient.ApiResult result) {
            setLoading(false);
            if (result.success && result.raw != null) {
                String token = result.raw.optString("token", null);
                String agentName = result.raw.optString("agent_name", "");
                if (token != null) {
                    session.login(token, agentName);
                    goToOrders();
                    return;
                }
            }
            showError(!TextUtils.isEmpty(result.message) ? result.message : "Login failed. Please try again.");
        }
    }
}
