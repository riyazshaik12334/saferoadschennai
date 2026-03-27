package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AuthoritySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_settings);

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
