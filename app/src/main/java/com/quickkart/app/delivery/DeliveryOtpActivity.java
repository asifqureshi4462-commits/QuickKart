package com.quickkart.app.delivery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickkart.app.R;
import com.quickkart.app.network.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Displays the OTP entry screen and forwards {order_id, entered_otp} to
 * api/delivery/verify_otp.php. This activity does NOT generate, store, or
 * validate the OTP itself — the PHP backend is the sole authority. This
 * screen only shows whatever success/error message the backend returns.
 */
public class DeliveryOtpActivity extends AppCompatActivity {

    private EditText otpInput;
    private TextView errorText, orderCodeText;
    private Button verifyButton;
    private ProgressBar progressBar;
    private DeliverySession session;

    private int orderId;
    private String orderCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_otp);

        session = new DeliverySession(this);
        orderId = getIntent().getIntExtra("orderId", -1);
        orderCode = getIntent().getStringExtra("orderCode");

        otpInput = findViewById(R.id.dotpInput);
        errorText = findViewById(R.id.dotpError);
        orderCodeText = findViewById(R.id.dotpOrderCode);
        verifyButton = findViewById(R.id.btnVerifyOtp);
        progressBar = findViewById(R.id.dotpProgress);

        orderCodeText.setText("Order #" + orderCode);
        findViewById(R.id.dotpBack).setOnClickListener(v -> finish());
        verifyButton.setOnClickListener(v -> attemptVerify());
    }

    private void attemptVerify() {
        String otp = otpInput.getText().toString().trim();
        errorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            showError("Please enter the 6-digit OTP");
            return;
        }
        if (orderId <= 0) {
            showError("Invalid order. Please go back and try again.");
            return;
        }

        setLoading(true);
        new VerifyTask().execute(otp);
    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        verifyButton.setEnabled(!loading);
    }

    private class VerifyTask extends AsyncTask<String, Void, ApiClient.ApiResult> {
        @Override
        protected ApiClient.ApiResult doInBackground(String... params) {
            try {
                JSONObject body = new JSONObject();
                body.put("order_id", orderId);
                body.put("entered_otp", params[0]);
                return ApiClient.post("verify_otp.php", body, session.getToken());
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
            if (result.success) {
                Toast.makeText(DeliveryOtpActivity.this, "Delivered! " + result.message, Toast.LENGTH_LONG).show();
                finish();
            } else {
                // Server is the sole authority — whatever it says (wrong OTP,
                // expired, max attempts, not eligible, etc.) is shown as-is.
                showError(result.message);
            }
        }
    }
}
