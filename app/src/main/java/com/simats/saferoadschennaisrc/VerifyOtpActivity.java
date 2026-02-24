package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp1, etOtp2, etOtp3, etOtp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        // Back Button Logic
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Dynamic Email Display
        TextView tvDesc = findViewById(R.id.tvDesc);
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            // Masking logic: show first letter + **** + domain
            int atIndex = email.indexOf("@");
            if (atIndex > 1) {
                String domain = email.substring(atIndex);
                String maskedEmail = email.charAt(0) + "****" + domain;
                tvDesc.setText("Enter the 4-digit code sent to your email " + maskedEmail);
            } else {
                tvDesc.setText("Enter the 4-digit code sent to your email " + email);
            }
        } else {
            tvDesc.setText("Enter the 4-digit code sent to your email");
        }

        // Resend OTP Logic
        TextView tvResendOtp = findViewById(R.id.tvResendOtp);
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerifyOtpActivity.this, "OTP Resent Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // OTP Auto-Focus Logic
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);

        setupOtpInputs();

        // Verify Button Logic
        Button btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etOtp1.getText().toString() +
                        etOtp2.getText().toString() +
                        etOtp3.getText().toString() +
                        etOtp4.getText().toString();

                if (otp.length() < 4) {
                    Toast.makeText(VerifyOtpActivity.this, "Please enter 4-digit OTP", Toast.LENGTH_SHORT).show();
                } else if (otp.equals("1234")) {
                    Toast.makeText(VerifyOtpActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                    // Navigate to Reset Password Screen
                    android.content.Intent intent = new android.content.Intent(VerifyOtpActivity.this,
                            ResetPasswordActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupOtpInputs() {
        etOtp1.addTextChangedListener(new OtpTextWatcher(etOtp1, etOtp2));
        etOtp2.addTextChangedListener(new OtpTextWatcher(etOtp2, etOtp3));
        etOtp3.addTextChangedListener(new OtpTextWatcher(etOtp3, etOtp4));
        etOtp4.addTextChangedListener(new OtpTextWatcher(etOtp4, null));
    }

    private class OtpTextWatcher implements TextWatcher {
        private EditText currentView;
        private EditText nextView;

        public OtpTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
