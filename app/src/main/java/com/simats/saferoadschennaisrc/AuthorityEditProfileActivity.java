package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorityEditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etOfficerId;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_edit_profile);

        // Initialize UI Elements
        TextView tvInitials = findViewById(R.id.tvInitials);
        etFullName = findViewById(R.id.etFullName);
        etOfficerId = findViewById(R.id.etOfficerId);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Load User Data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = prefs.getString("USER_NAME", "Officer");
        String officerId = prefs.getString("USER_EMAIL", "EMP-00000"); // Emp ID stored in USER_EMAIL for authorities

        etFullName.setText(userName);
        etOfficerId.setText(officerId);

        // Set Initials
        updateInitials(userName, tvInitials);

        // Save Changes
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());

        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void updateInitials(String userName, TextView tvInitials) {
        if (userName != null && !userName.isEmpty()) {
            String initials = "";
            String[] parts = userName.trim().split("\\s+");
            if (parts.length > 0) {
                initials += parts[0].substring(0, 1).toUpperCase();
            }
            if (parts.length > 1) {
                initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
            } else if (userName.length() > 1) {
                initials = userName.substring(0, 2).toUpperCase();
            }
            tvInitials.setText(initials);
        }
    }

    private void saveProfileChanges() {
        String newName = etFullName.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "");

        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("SAVING...");

        ApiService.ProfileUpdateRequest request = new ApiService.ProfileUpdateRequest(email, newName);
        RetrofitClient.getApiService().updateProfile(request).enqueue(new Callback<ApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<ApiService.AuthResponse> call, Response<ApiService.AuthResponse> response) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("SAVE CHANGES");

                if (response.isSuccessful() && response.body() != null) {
                    // Update session
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("USER_NAME", response.body().name);
                    editor.apply();

                    Toast.makeText(AuthorityEditProfileActivity.this, "Profile updated successfully!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Update failed";
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            if (errStr.contains("Username already exists")) {
                                errorMsg = "Username already exists";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AuthorityEditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("SAVE CHANGES");
                Toast.makeText(AuthorityEditProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
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
