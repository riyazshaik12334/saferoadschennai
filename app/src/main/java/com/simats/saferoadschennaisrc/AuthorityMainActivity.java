package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorityMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_main);

        // Set Officer Name from Intent or SharedPreferences
        android.widget.TextView tvOfficerName = findViewById(R.id.tvOfficerName);
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            userName = prefs.getString("USER_NAME", "Officer Suresh");
        }
        tvOfficerName.setText("Officer " + userName);

        // Hide Priority Alerts and Recent Activity if counts are zero
        // In a real app, these would come from a database or API
        // For now, we assume if they are zero in the UI, we should hide the sections
        android.widget.LinearLayout layoutPriorityAlerts = findViewById(R.id.layoutPriorityAlerts);
        android.widget.LinearLayout layoutRecentActivity = findViewById(R.id.layoutRecentActivity);

        // Example check: if we had actual count variables
        int pendingCount = 0; // This should be fetched from logic
        int inProgressCount = 0;
        int resolvedCount = 0;

        if (pendingCount == 0 && inProgressCount == 0 && resolvedCount == 0) {
            layoutPriorityAlerts.setVisibility(android.view.View.GONE);
            layoutRecentActivity.setVisibility(android.view.View.GONE);
        }

        // Notification Click
        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation logic
        findViewById(R.id.btnViewComplaints).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityMainActivity.this, AuthorityComplaintsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            // Already on Dashboard
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityMainActivity.this, AuthorityComplaintsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navAlerts).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityProfileActivity.class);
            startActivity(intent);
        });
    }
}
