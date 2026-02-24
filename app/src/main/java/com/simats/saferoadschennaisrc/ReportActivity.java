package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ReportActivity extends AppCompatActivity {

    private TextView btnLow, btnMedium, btnHigh;
    private EditText etLocation, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Views
        btnLow = findViewById(R.id.btnLow);
        btnMedium = findViewById(R.id.btnMedium);
        btnHigh = findViewById(R.id.btnHigh);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);

        // Severity Selection Logic
        View.OnClickListener severityListener = v -> {
            resetSeverityButtons();
            v.setBackgroundResource(R.drawable.bg_severity_selected);
            ((TextView) v).setTextColor(ContextCompat.getColor(this, android.R.color.white));
        };

        btnLow.setOnClickListener(severityListener);
        btnMedium.setOnClickListener(severityListener);
        btnHigh.setOnClickListener(severityListener);

        // Auto-detect Location Mock
        findViewById(R.id.btnAutoDetect).setOnClickListener(v -> {
            etLocation.setText("123, Anna Salai, Chennai");
            Toast.makeText(this, "Location detected", Toast.LENGTH_SHORT).show();
        });

        // Submit Button Mock
        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String location = etLocation.getText().toString();
            String description = etDescription.getText().toString();

            if (location.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();
                finish(); // Return to Home
            }
        });

        // Setup Bottom Navigation
        View navHome = findViewById(R.id.navHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> finish());
        }

        View navMyReports = findViewById(R.id.navMyReports);
        if (navMyReports != null) {
            navMyReports.setOnClickListener(v -> {
                Intent intent = new Intent(ReportActivity.this, MyReportsActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Setup Bottom Navigation - Profile
        View navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ReportActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void resetSeverityButtons() {
        int unselectedBg = R.drawable.bg_severity_unselected;
        int textColor = ContextCompat.getColor(this, R.color.text_primary);

        btnLow.setBackgroundResource(unselectedBg);
        btnLow.setTextColor(textColor);

        btnMedium.setBackgroundResource(unselectedBg);
        btnMedium.setTextColor(textColor);

        btnHigh.setBackgroundResource(unselectedBg);
        btnHigh.setTextColor(textColor);
    }
}