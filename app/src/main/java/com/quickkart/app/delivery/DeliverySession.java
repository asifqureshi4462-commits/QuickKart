package com.quickkart.app.delivery;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores the delivery agent's bearer token after api/delivery/login.php
 * succeeds. The token itself is opaque to the app — the PHP backend is what
 * validates and expires it (see DELIVERY_AGENT_TOKEN_TTL_HOURS server-side).
 */
public class DeliverySession {
    private static final String PREF_NAME = "quickkart_delivery_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_AGENT_NAME = "agent_name";

    private final SharedPreferences prefs;

    public DeliverySession(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void login(String token, String agentName) {
        prefs.edit().putString(KEY_TOKEN, token).putString(KEY_AGENT_NAME, agentName).apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getAgentName() {
        return prefs.getString(KEY_AGENT_NAME, "");
    }

    public void logout() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_AGENT_NAME).apply();
    }
}
