package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve Data
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "citizen@saferoads.com");

        // Name logic
        String name = prefs.getString("USER_NAME", "");
        if (name.isEmpty()) {
            if (email.length() > 5) {
                name = email.substring(0, 5);
            } else {
                name = email;
            }
        }

        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvUserContact = findViewById(R.id.tvUserContact);

        tvUserName.setText(name);
        tvUserContact.setText(email);

        // Make Profile Card clickalbe to Edit Profile
        findViewById(R.id.profileCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Back Button Logic

        // Menu Click Listeners
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        });

        // Other settings / Help / Privacy / Terms logic placeholders
        View.OnClickListener stubListener = v -> Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnHelp).setOnClickListener(v -> {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnPrivacy).setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnTerms).setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnDelete).setOnClickListener(stubListener);

        // Logic for Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply(); // Clear session
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Home
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Report
        findViewById(R.id.navReport).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
            finish();
        });

        // My Reports
        findViewById(R.id.navMyReports).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyReportsActivity.class);
            startActivity(intent);
            finish();
        });

        // Profile (Current - No Op)
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Already on Profile
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data in case it was edited in EditProfileActivity
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String name = prefs.getString("USER_NAME", "");
        if (!name.isEmpty()) {
            ((TextView) findViewById(R.id.tvUserName)).setText(name);
        }
    }
}
