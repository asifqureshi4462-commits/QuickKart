package com.quickkart.app.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Minimal HTTPS + JSON client used only by the Delivery OTP feature.
 * The PHP backend is the sole authority for OTP generation/verification;
 * this class just sends HTTPS requests and parses JSON responses.
 *
 * IMPORTANT: Update BASE_URL if your website's domain changes.
 */
public class ApiClient {

    public static final String BASE_URL = "https://quickkcart.infinityfreeapp.com/api/delivery/";

    public static class ApiResult {
        public boolean success;
        public int httpCode;
        public String message;
        public JSONObject raw;
    }

    /** POST JSON body, optionally with a Bearer token. Runs synchronously — call from a background thread. */
    public static ApiResult post(String endpoint, JSONObject body, String bearerToken) {
        ApiResult result = new ApiResult();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (bearerToken != null && !bearerToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
            }
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            result.httpCode = code;
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            String responseText = readStream(is);

            JSONObject json = new JSONObject(responseText);
            result.raw = json;
            result.success = json.optBoolean("success", false);
            result.message = json.optString("message", "");
        } catch (Exception e) {
            result.success = false;
            result.httpCode = 0;
            result.message = "Network error: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return result;
    }

    /** GET with a Bearer token. Runs synchronously — call from a background thread. */
    public static ApiResult get(String endpoint, String bearerToken) {
        ApiResult result = new ApiResult();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (bearerToken != null && !bearerToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
            }
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            int code = conn.getResponseCode();
            result.httpCode = code;
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            String responseText = readStream(is);

            JSONObject json = new JSONObject(responseText);
            result.raw = json;
            result.success = json.optBoolean("success", false);
            result.message = json.optString("message", "");
        } catch (Exception e) {
            result.success = false;
            result.httpCode = 0;
            result.message = "Network error: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return result;
    }

    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "{}";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
