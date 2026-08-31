package com.quickkart.app.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.quickkart.app.R;
import com.quickkart.app.fragments.CartFragment;
import com.quickkart.app.fragments.CategoriesFragment;
import com.quickkart.app.fragments.HomeFragment;
import com.quickkart.app.fragments.OrdersFragment;
import com.quickkart.app.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);
    }

    private boolean onNavItemSelected(@NonNull android.view.MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_categories) {
            fragment = new CategoriesFragment();
        } else if (id == R.id.nav_cart) {
            fragment = new CartFragment();
        } else if (id == R.id.nav_orders) {
            fragment = new OrdersFragment();
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
        }
        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void selectCartTab() {
        bottomNav.setSelectedItemId(R.id.nav_cart);
    }

    public void selectOrdersTab() {
        bottomNav.setSelectedItemId(R.id.nav_orders);
    }
}
