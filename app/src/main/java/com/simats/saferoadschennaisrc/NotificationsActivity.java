package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_authority);

        // Setup "Mark all as read" click
        findViewById(R.id.tvMarkRead)
                .setOnClickListener(v -> android.widget.Toast
                        .makeText(this, "Marked all as read", android.widget.Toast.LENGTH_SHORT).show());

        // Bottom Navigation logic
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, AuthorityMainActivity.class);
            intent.addFlags(
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Complaints Tab clicked", android.widget.Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.navAlerts).setOnClickListener(v -> {
            // Already on Alerts (Notifications) page
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, AuthorityProfileActivity.class);
            startActivity(intent);
        });
    }
}
