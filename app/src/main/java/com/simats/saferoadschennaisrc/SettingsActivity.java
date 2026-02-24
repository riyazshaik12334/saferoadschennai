package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Views
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Header and Back handled by standard Android behavior if wanted,
        // but design shows specialized header. For now, finish() can be hooked to logo
        // if needed.
        findViewById(R.id.headerLogo).setOnClickListener(v -> finish());

        // Password Update Button
        findViewById(R.id.btnUpdatePassword).setOnClickListener(v -> {
            validateAndUpdatePassword();
        });

        // Bottom Navigation logic
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.navReport).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
            finish();
        });
        findViewById(R.id.navMyReports).setOnClickListener(v -> {
            // Intent for MyReportsActivity if exists
            Toast.makeText(this, "My Reports clicked", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> finish());
    }

    private void validateAndUpdatePassword() {
        String currentPass = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate success
        Intent intent = new Intent(this, ResetPasswordSuccessActivity.class);
        intent.putExtra("is_update_mode", true);
        startActivity(intent);
        finish();
    }
}
