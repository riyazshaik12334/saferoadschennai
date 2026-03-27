package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        android.widget.TextView tvTitle = findViewById(R.id.tvTitle);
        android.widget.TextView tvLabelMobile = findViewById(R.id.tvLabelMobile);
        android.widget.EditText etMobile = findViewById(R.id.etMobile);
        android.widget.Button btnSendOtp = findViewById(R.id.btnSendOtp);

        boolean isCitizen = getIntent().getBooleanExtra("IS_CITIZEN", true);

        if (!isCitizen) {
            tvTitle.setText("Authority Forgot Password");
            tvLabelMobile.setText("Officer ID / Email");
            etMobile.setHint("Enter Officer ID or Email");
            etMobile.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        }

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etMobile.getText().toString().trim();
                if (email.isEmpty()) {
                    etMobile.setError("Enter email address");
                    return;
                }

                // API Call to Send OTP
                btnSendOtp.setEnabled(false);
                btnSendOtp.setText("Please wait...");

                com.simats.saferoadschennaisrc.network.ApiService apiService = com.simats.saferoadschennaisrc.network.RetrofitClient
                        .getApiService();
                com.simats.saferoadschennaisrc.network.ApiService.SendOtpRequest request = new com.simats.saferoadschennaisrc.network.ApiService.SendOtpRequest(
                        email);

                apiService.sendOtp(request).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call,
                            retrofit2.Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            android.widget.Toast.makeText(ForgotPasswordActivity.this, "OTP sent to your email",
                                    android.widget.Toast.LENGTH_SHORT).show();
                            android.content.Intent intent = new android.content.Intent(ForgotPasswordActivity.this,
                                    VerifyOtpActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            btnSendOtp.setEnabled(true);
                            btnSendOtp.setText("Send OTP");
                            try {
                                String errorBody = response.errorBody().string();
                                org.json.JSONObject jsonObject = new org.json.JSONObject(errorBody);
                                String errorMsg = jsonObject.optString("error", "Failed to send OTP");
                                android.widget.Toast.makeText(ForgotPasswordActivity.this, errorMsg,
                                        android.widget.Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                android.widget.Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP",
                                        android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        btnSendOtp.setEnabled(true);
                        btnSendOtp.setText("Send OTP");
                        android.widget.Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(),
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
