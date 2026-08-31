package com.quickkart.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "quickkart_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_ADMIN_LOGGED_IN = "admin_logged_in";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void loginUser(int userId, String name) {
        prefs.edit().putInt(KEY_USER_ID, userId).putString(KEY_USER_NAME, name).apply();
    }

    public boolean isUserLoggedIn() {
        return prefs.getInt(KEY_USER_ID, -1) != -1;
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public void logoutUser() {
        prefs.edit().remove(KEY_USER_ID).remove(KEY_USER_NAME).apply();
    }

    public void loginAdmin() {
        prefs.edit().putBoolean(KEY_ADMIN_LOGGED_IN, true).apply();
    }

    public boolean isAdminLoggedIn() {
        return prefs.getBoolean(KEY_ADMIN_LOGGED_IN, false);
    }

    public void logoutAdmin() {
        prefs.edit().remove(KEY_ADMIN_LOGGED_IN).apply();
    }
}
