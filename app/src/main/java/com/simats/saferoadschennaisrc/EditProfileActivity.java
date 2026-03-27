package com.simats.saferoadschennaisrc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Bind Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);

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

        ImageView imgProfile = findViewById(R.id.imgProfile);

        findViewById(R.id.btnSaveChanges).setOnClickListener(v -> saveProfileChanges());

        // Cancel
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void saveProfileChanges() {
        String newName = etFullName.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "");

        android.widget.Button btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("SAVING...");

        ApiService.ProfileUpdateRequest request = new ApiService.ProfileUpdateRequest(email, newName);
        RetrofitClient.getApiService().updateProfile(request).enqueue(new retrofit2.Callback<ApiService.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiService.AuthResponse> call, retrofit2.Response<ApiService.AuthResponse> response) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("SAVE CHANGES");

                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("USER_NAME", response.body().name);
                    editor.apply();

                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back after success
                } else {
                    String errorMsg = "Update failed";
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            if (errStr.contains("Username already exists")) {
                                errorMsg = "Username already exists";
                            }
                        }
                    } catch (Exception e) {}
                    Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiService.AuthResponse> call, Throwable t) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("SAVE CHANGES");
                Toast.makeText(EditProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
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
