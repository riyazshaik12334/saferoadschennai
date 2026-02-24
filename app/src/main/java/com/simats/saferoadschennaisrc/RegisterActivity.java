package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etMobile, etEmail, etAadhaar, etPassword, etConfirmPassword;
    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etAadhaar = findViewById(R.id.etAadhaar);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        // Register Button Logic
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegister();
            }
        });

        // Login Link Logic
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Login
                finish();
            }
        });
    }

    private void validateAndRegister() {
        String fullName = etFullName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (fullName.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate successful registration
        // Navigate to Success Screen
        android.widget.Toast.makeText(this, "Registration Successful! Redirecting...", android.widget.Toast.LENGTH_LONG)
                .show();

        try {
            Intent intent = new Intent(this, RegisterSuccessActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            android.widget.Toast
                    .makeText(this, "Error opening screen: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
