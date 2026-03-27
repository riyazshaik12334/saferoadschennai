package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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

        // Retrieve Email from SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "Error: User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // API Call
        com.simats.saferoadschennaisrc.network.ApiService apiService = com.simats.saferoadschennaisrc.network.RetrofitClient
                .getApiService();
        com.simats.saferoadschennaisrc.network.ApiService.ChangePasswordRequest request = new com.simats.saferoadschennaisrc.network.ApiService.ChangePasswordRequest(
                email, currentPass, newPass);

        apiService.changePassword(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this, ResetPasswordSuccessActivity.class);
                    intent.putExtra("is_update_mode", true);
                    startActivity(intent);
                    finish();
                } else {
                    String error = "Update failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            if (errorJson.contains("Incorrect current password")) {
                                error = "Current password wrong";
                            }
                        }
                    } catch (Exception e) {
                    }
                    if ("Current password wrong".equals(error)) {
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Update Failed")
                                .setMessage("The current password you entered is incorrect. Please try again.")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        Toast.makeText(SettingsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
