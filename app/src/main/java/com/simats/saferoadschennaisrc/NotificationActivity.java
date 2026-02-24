package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Setup "Mark all as read" click
        findViewById(R.id.tvMarkRead)
                .setOnClickListener(v -> Toast.makeText(this, "Marked all as read", Toast.LENGTH_SHORT).show());

        // Setup Home navigation to return to MainActivity
        LinearLayout navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(v -> finish());
    }
}
