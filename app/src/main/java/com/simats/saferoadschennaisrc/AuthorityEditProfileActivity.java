package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AuthorityEditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_edit_profile);

        // Initialize UI Elements
        TextView tvInitials = findViewById(R.id.tvInitials);
        EditText etFullName = findViewById(R.id.etFullName);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etOfficerId = findViewById(R.id.etOfficerId);

        // Load User Data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = prefs.getString("USER_NAME", "Suresh");
        String userPhone = prefs.getString("USER_MOBILE", "+91 94567 12345");
        String officerId = "EMP-10042"; // Mocked for now

        etFullName.setText("Officer " + userName);
        etPhone.setText(userPhone);
        etOfficerId.setText(officerId);

        // Set Initials
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

        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bottom Navigation
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityComplaintsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navAlerts).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }
}
