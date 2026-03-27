package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText etNewPassword = findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);

        final String email = getIntent().getStringExtra("email");

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = etNewPassword.getText().toString().trim();
                String confirmPass = etConfirmPassword.getText().toString().trim();

                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPass.length() < 8) {
                    Toast.makeText(ResetPasswordActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email == null || email.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Error: User email not found", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // API Call
                com.simats.saferoadschennaisrc.network.ApiService apiService = com.simats.saferoadschennaisrc.network.RetrofitClient
                        .getApiService();
                com.simats.saferoadschennaisrc.network.ApiService.ResetPasswordRequest request = new com.simats.saferoadschennaisrc.network.ApiService.ResetPasswordRequest(
                        email, newPass);

                apiService.resetPassword(request).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call,
                            retrofit2.Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Password Reset Successfully",
                                    Toast.LENGTH_SHORT).show();
                            // Navigate to Success Screen
                            android.content.Intent intent = new android.content.Intent(ResetPasswordActivity.this,
                                    ResetPasswordSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Reset failed: " + response.message(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
