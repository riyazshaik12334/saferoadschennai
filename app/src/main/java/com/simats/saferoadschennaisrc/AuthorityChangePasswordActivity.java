package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorityChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnSavePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_change_password);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSavePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPw = etCurrentPassword.getText().toString().trim();
        String newPw = etNewPassword.getText().toString().trim();
        String confirmPw = etConfirmPassword.getText().toString().trim();

        if (currentPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPw.length() < 8) {
            Toast.makeText(this, "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPw.equals(confirmPw)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", null); // This is the Emp ID/Email

        if (email == null) {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fix: Use getApiService() instead of getRetrofitInstance()
        ApiService apiService = RetrofitClient.getApiService();
        ApiService.ChangePasswordRequest request = new ApiService.ChangePasswordRequest(email, currentPw, newPw);

        btnSavePassword.setEnabled(false);
        btnSavePassword.setText("Updating...");

        apiService.changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSavePassword.setEnabled(true);
                btnSavePassword.setText("Save Password");

                if (response.isSuccessful()) {
                    Intent intent = new Intent(AuthorityChangePasswordActivity.this,
                            AuthorityPasswordSuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Failed: " + response.message();
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            if (errorJson.contains("Incorrect current password")) {
                                errorMessage = "Current password wrong";
                            } else if (errorJson.contains("error")) {
                                // Extract simple error if possible or just show the json for now
                                errorMessage = errorJson;
                                if (errorJson.contains("\"error\":")) {
                                    errorMessage = errorJson.split("\"error\":")[1]
                                            .replace("\"", "").replace("}", "").replace("{", "").trim();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ("Current password wrong".equals(errorMessage)) {
                        new AlertDialog.Builder(AuthorityChangePasswordActivity.this)
                                .setTitle("Update Failed")
                                .setMessage("The current password you entered is incorrect. Please try again.")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        Toast.makeText(AuthorityChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSavePassword.setEnabled(true);
                btnSavePassword.setText("Save Password");
                Toast.makeText(AuthorityChangePasswordActivity.this, "Network Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
