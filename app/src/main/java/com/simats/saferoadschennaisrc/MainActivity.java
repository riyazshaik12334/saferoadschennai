package com.simats.saferoadschennaisrc;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the username from the Intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            userName = "Citizen";
        }

        // Bind the TextView and set the welcome message
        android.widget.TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvWelcomeName.setText("Welcome, " + userName);

        // Setup "Report Now" button click
        findViewById(R.id.btnReportNow).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        // Setup Notification button click
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Setup My Reports button click
        // Setup My Reports button click
        findViewById(R.id.navMyReports).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MyReportsActivity.class);
            startActivity(intent);
        });

        // Setup Report button (Bottom Nav)
        findViewById(R.id.navReport).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        // Setup Home button (Bottom Nav)
        findViewById(R.id.navHome).setOnClickListener(v -> {
            // Already on Home
        });

        // Setup Profile button (Bottom Nav)
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ProfileActivity.class);
            String email = getSharedPreferences("UserSession", MODE_PRIVATE).getString("USER_EMAIL", "");
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
        });
    }
}