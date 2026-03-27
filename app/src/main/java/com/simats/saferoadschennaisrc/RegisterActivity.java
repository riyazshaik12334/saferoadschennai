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
import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etMobile, etEmail, etAadhaar, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Views
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etAadhaar = findViewById(R.id.etAadhaar);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
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

        setupPasswordToggles();

        // Privacy Policy Link
        cbTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If they checked it, show the policy? Or just let them click a separate link.
                // We will add a long click listener to the checkbox or modify UI in xml to have
                // a clickable TextView.
            }
        });

        // Make the text of the checkbox clickable to show policy
        String text = getString(R.string.terms_conditions) + " (Click to read)";
        android.text.SpannableString ss = new android.text.SpannableString(text);
        android.text.style.ClickableSpan clickableSpan = new android.text.style.ClickableSpan() {
            @Override
            public void onClick(@androidx.annotation.NonNull View textView) {
                new androidx.appcompat.app.AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Privacy Policy")
                        .setMessage(
                                "Details of Privacy Policy:\n\n1. We collect location data to accurately log reports.\n2. We collect your mobile number and email to securely log you in and contact you regarding report status.\n3. Your data will not be shared with 3rd parties without your consent.")
                        .setPositiveButton("Close", null)
                        .show();
            }
        };
        ss.setSpan(clickableSpan, 0, text.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        cbTerms.setText(ss);
        cbTerms.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    private void setupPasswordToggles() {
        final ImageView ivTogglePasswordReg = findViewById(R.id.ivTogglePasswordReg);
        final ImageView ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);

        ivTogglePasswordReg.setOnClickListener(new View.OnClickListener() {
            private boolean isVisible = false;

            @Override
            public void onClick(View v) {
                isVisible = !isVisible;
                if (isVisible) {
                    etPassword
                            .setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                    ivTogglePasswordReg.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                } else {
                    etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                    ivTogglePasswordReg.setImageResource(android.R.drawable.ic_menu_view);
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        ivToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            private boolean isVisible = false;

            @Override
            public void onClick(View v) {
                isVisible = !isVisible;
                if (isVisible) {
                    etConfirmPassword
                            .setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                    ivToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                } else {
                    etConfirmPassword
                            .setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                    ivToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            }
        });
    }

    private void validateAndRegister() {
        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (mobile.isEmpty() || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
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

        // Network Registration
        btnRegister.setEnabled(false);
        btnRegister.setText("Registering...");

        ApiService apiService = RetrofitClient.getApiService();
        apiService.register(new ApiService.RegisterRequest("", email, password, mobile))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register");

                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(RegisterActivity.this, RegisterSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Registration Failed: "
                                            + (response.code() == 400 ? "User already exists" : "Error"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register");
                        Toast.makeText(RegisterActivity.this, "Network Error: Check Connection", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
}
