package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthorityProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_profile);

        // Name and initials moved to onResume

        // Setup Bottom Navigation
        SharedPreferences resPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentRole = resPrefs.getString("USER_ROLE", "AUTHORITY");

        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            Intent intent;
            if ("SUPERVISOR".equalsIgnoreCase(currentRole)) {
                intent = new Intent(AuthorityProfileActivity.this, SupervisorMainActivity.class);
            } else {
                intent = new Intent(AuthorityProfileActivity.this, AuthorityMainActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            Intent intent;
            if ("SUPERVISOR".equalsIgnoreCase(currentRole)) {
                intent = new Intent(AuthorityProfileActivity.this, SupervisorComplaintsActivity.class);
            } else {
                intent = new Intent(AuthorityProfileActivity.this, AuthorityComplaintsActivity.class);
            }
            startActivity(intent);
        });

        findViewById(R.id.navAlerts).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Already on Profile
        });

        // Setup Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("USER_PASSWORD");
            editor.putBoolean("REMEMBER_ME", false);
            editor.remove("AUTH_TOKEN");
            editor.apply();

            Intent intent = new Intent(AuthorityProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Setup Menu Item Click Listeners
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityProfileActivity.this, AuthorityEditProfileActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityProfileActivity.this, AuthorityChangePasswordActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityProfileActivity.this, AuthoritySettingsActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnDeptInfo).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityProfileActivity.this, DepartmentInfoActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnHelpSupport).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityProfileActivity.this, AuthorityHelpActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfileData();
    }

    private void refreshProfileData() {
        TextView tvOfficerName = findViewById(R.id.tvOfficerName);
        TextView tvInitials = findViewById(R.id.tvInitials);
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = prefs.getString("USER_NAME", "Suresh");
        String role = prefs.getString("USER_ROLE", "AUTHORITY");
        String displayPrefix = "SUPERVISOR".equalsIgnoreCase(role) ? "Supervisor " : "Officer ";

        if (userName != null && userName.startsWith(displayPrefix)) {
            tvOfficerName.setText(userName);
        } else {
            tvOfficerName.setText(displayPrefix + userName);
        }

        if (userName != null && !userName.isEmpty()) {
            String initials = "";
            String[] parts = userName.trim().split("\\s+");
            if (parts.length > 0) {
                initials += parts[0].substring(0, 1).toUpperCase();
            }
            if (parts.length > 1) {
                initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
            } else if (userName.length() > 1) {
                initials = userName.substring(0, 2).toUpperCase();
            }
            tvInitials.setText(initials);
        }
    }
}
