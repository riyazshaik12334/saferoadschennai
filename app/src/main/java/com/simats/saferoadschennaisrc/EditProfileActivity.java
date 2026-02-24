package com.simats.saferoadschennaisrc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Bind Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Retrieve Data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "citizen@saferoads.com");

        // Populate Fields
        etEmail.setText(email);

        String name = prefs.getString("USER_NAME", "");
        if (name.isEmpty()) {
            if (email.length() > 5) {
                name = email.substring(0, 5);
            } else {
                name = email;
            }
        }
        etFullName.setText(name);

        String address = prefs.getString("USER_ADDRESS", "");
        if (!address.isEmpty()) {
            etAddress.setText(address);
        }

        // Mock Map Pick Logic
        View.OnClickListener mapPickListener = v -> {
            etAddress.setText("123, Gandhi Rd, Chennai - Map Picked");
            Toast.makeText(this, "Location picked from Map", Toast.LENGTH_SHORT).show();
        };

        etAddress.setOnClickListener(mapPickListener);

        ImageView imgProfile = findViewById(R.id.imgProfile);

        // Avatar Selection Logic
        View.OnClickListener avatarListener = v -> {
            // Reset all avatars
            findViewById(R.id.avatarPerson).setBackgroundResource(R.drawable.bg_circle_white);
            findViewById(R.id.avatarSecurity).setBackgroundResource(R.drawable.bg_circle_white);
            findViewById(R.id.avatarHelp).setBackgroundResource(R.drawable.bg_circle_white);
            findViewById(R.id.avatarNotifications).setBackgroundResource(R.drawable.bg_circle_white);
            findViewById(R.id.avatarInfo).setBackgroundResource(R.drawable.bg_circle_white);

            // Highlight selected
            v.setBackgroundResource(R.drawable.bg_tab_selected); // Using existing white but could use a light tint

            // Update main profile pic
            if (v instanceof ImageView) {
                imgProfile.setImageDrawable(((ImageView) v).getDrawable());
                imgProfile.setColorFilter(((ImageView) v).getColorFilter());
            }
        };

        findViewById(R.id.avatarPerson).setOnClickListener(avatarListener);
        findViewById(R.id.avatarSecurity).setOnClickListener(avatarListener);
        findViewById(R.id.avatarHelp).setOnClickListener(avatarListener);
        findViewById(R.id.avatarNotifications).setOnClickListener(avatarListener);
        findViewById(R.id.avatarInfo).setOnClickListener(avatarListener);

        // Save Changes
        findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
            String newName = etFullName.getText().toString();
            String newEmail = etEmail.getText().toString();
            String newAddress = etAddress.getText().toString();

            // Save to Prefs
            prefs.edit()
                    .putString("USER_NAME", newName)
                    .putString("USER_EMAIL", newEmail)
                    .putString("USER_ADDRESS", newAddress)
                    .apply();

            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Cancel
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Home
        findViewById(R.id.navHome).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.setFlags(
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Report
        findViewById(R.id.navReport).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ReportActivity.class);
            startActivity(intent);
            finish();
        });

        // My Reports
        findViewById(R.id.navMyReports).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, MyReportsActivity.class);
            startActivity(intent);
            finish();
        });

        // Profile (Already here - No Op or refresh)
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Maybe go back to dashboard? Or stay.
            // For now, no op as it is "Profile" section.
            finish(); // Go back to dashboard actually
        });
    }
}
