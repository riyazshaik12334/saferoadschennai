package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MyReportsActivity extends AppCompatActivity {

    private TextView tabAll, tabActive, tabResolved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        // Initialize Filter Tabs
        tabAll = findViewById(R.id.tabAll);
        tabActive = findViewById(R.id.tabActive);
        tabResolved = findViewById(R.id.tabResolved);

        View.OnClickListener tabListener = v -> {
            resetTabs();
            v.setBackgroundResource(R.drawable.bg_tab_selected);
            ((TextView) v).setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            // List is empty, so no data filtering logic needed yet
        };

        tabAll.setOnClickListener(tabListener);
        tabActive.setOnClickListener(tabListener);
        tabResolved.setOnClickListener(tabListener);

        // Filter Icon Click
        findViewById(R.id.btnFilter)
                .setOnClickListener(v -> Toast.makeText(this, "Filter options", Toast.LENGTH_SHORT).show());

        // Bottom Navigation Logic
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navReport).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
            finish();
        });

        // Current item is My Reports (No Op)

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void resetTabs() {
        int unselectedBg = R.drawable.bg_tab_unselected;
        int textColor = ContextCompat.getColor(this, R.color.text_hint); // Using text_hint as unselected color

        tabAll.setBackgroundResource(unselectedBg);
        tabAll.setTextColor(textColor);

        tabActive.setBackgroundResource(unselectedBg);
        tabActive.setTextColor(textColor);

        tabResolved.setBackgroundResource(unselectedBg);
        tabResolved.setTextColor(textColor);
    }
}
