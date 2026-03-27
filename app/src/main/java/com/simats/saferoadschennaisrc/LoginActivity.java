package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tabCitizen;
    private TextView tabAuthority;
    private TextView tabSupervisor;
    private TextView tvInputLabel;
    private android.widget.EditText etMobile;
    private android.widget.LinearLayout layoutCreateAccount;
    private Button btnLogin;
    private String currentLoginRole = "CITIZEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabCitizen = findViewById(R.id.tabCitizen);
        tabAuthority = findViewById(R.id.tabAuthority);
        tabSupervisor = findViewById(R.id.tabSupervisor);
        tvInputLabel = findViewById(R.id.tvInputLabel);
        etMobile = findViewById(R.id.etMobile);
        layoutCreateAccount = findViewById(R.id.layoutCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);

        // Set initial hints
        etMobile.setHint(R.string.hint_email);

        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isRemembered = prefs.getBoolean("REMEMBER_ME", false);
        android.widget.CheckBox cbRememberMe = findViewById(R.id.cbRememberMe);
        if (cbRememberMe != null) {
            cbRememberMe.setChecked(isRemembered);
        }

        if (isRemembered) {
            String savedEmail = prefs.getString("USER_EMAIL", "");
            String savedPassword = prefs.getString("USER_PASSWORD", "");
            if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
                etMobile.setText(savedEmail);
                android.widget.EditText etPassword = findViewById(R.id.etPassword);
                if (etPassword != null) {
                    etPassword.setText(savedPassword);
                }
                // Optional: Auto-login
                // btnLogin.performClick();
            }
        }

        tabCitizen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab("CITIZEN");
            }
        });

        tabAuthority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab("AUTHORITY");
            }
        });

        if (tabSupervisor != null) {
            tabSupervisor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchTab("SUPERVISOR");
                }
            });
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identifier = etMobile.getText().toString().trim();
                String password = ((android.widget.EditText) findViewById(R.id.etPassword)).getText().toString().trim();

                if (identifier.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnLogin.setEnabled(false);
                btnLogin.setText("Connecting...");

                ApiService apiService = RetrofitClient.getApiService();
                apiService.login(new ApiService.LoginRequest(identifier, password, currentLoginRole))
                        .enqueue(new Callback<ApiService.AuthResponse>() {
                            @Override
                            public void onResponse(Call<ApiService.AuthResponse> call,
                                    Response<ApiService.AuthResponse> response) {
                                btnLogin.setEnabled(true);
                                if ("CITIZEN".equals(currentLoginRole)) btnLogin.setText(R.string.login_citizen);
                                else if ("AUTHORITY".equals(currentLoginRole)) btnLogin.setText(R.string.login_authority);
                                else btnLogin.setText("LOGIN AS SUPERVISOR");

                                if (response.isSuccessful() && response.body() != null) {
                                    ApiService.AuthResponse auth = response.body();

                                    // Save session
                                    android.content.SharedPreferences.Editor editor = getSharedPreferences(
                                            "UserSession", MODE_PRIVATE).edit();
                                    editor.putString("USER_EMAIL", auth.email)
                                            .putString("USER_NAME", auth.name)
                                            .putString("USER_ROLE", auth.role)
                                            .putString("AUTH_TOKEN", auth.token);

                                    android.widget.CheckBox cbRememberMe = findViewById(R.id.cbRememberMe);
                                    if (cbRememberMe != null && cbRememberMe.isChecked()) {
                                        editor.putBoolean("REMEMBER_ME", true);
                                        editor.putString("USER_PASSWORD", password);
                                    } else {
                                        editor.putBoolean("REMEMBER_ME", false);
                                        editor.remove("USER_PASSWORD");
                                    }
                                    editor.apply();

                                    Intent intent;
                                    if ("AUTHORITY".equalsIgnoreCase(auth.role)) {
                                        intent = new Intent(LoginActivity.this, AuthorityMainActivity.class);
                                    } else if ("SUPERVISOR".equalsIgnoreCase(auth.role)) {
                                        intent = new Intent(LoginActivity.this, SupervisorMainActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    }
                                    intent.putExtra("USER_NAME", auth.name);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorMsg = "Login Failed";
                                    try {
                                        String errorBody = response.errorBody().string();
                                        if (errorBody.contains("Incorrect password")) {
                                            errorMsg = "Incorrect password";
                                        } else if (errorBody.contains("User not found")) {
                                            errorMsg = "User not found";
                                        } else if (errorBody.contains("Invalid credentials")) {
                                            errorMsg = "Invalid credentials";
                                        }
                                    } catch (Exception e) {
                                    }
                                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                                btnLogin.setEnabled(true);
                                if ("CITIZEN".equals(currentLoginRole)) btnLogin.setText(R.string.login_citizen);
                                else if ("AUTHORITY".equals(currentLoginRole)) btnLogin.setText(R.string.login_authority);
                                else btnLogin.setText("LOGIN AS SUPERVISOR");
                                String errMsg = t.getMessage() != null ? t.getMessage() : "Unknown Network Error";
                                Toast.makeText(LoginActivity.this, "Network Error: " + errMsg, Toast.LENGTH_LONG).show();
                                android.util.Log.e("LoginActivity", "Login Failure", t);
                            }
                        });
            }
        });

        layoutCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("CITIZEN".equals(currentLoginRole)) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    intent.putExtra("IS_CITIZEN", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, AuthorityForgotPasswordActivity.class);
                    startActivity(intent);
                }
            }
        });

        final android.widget.EditText etPassword = findViewById(R.id.etPassword);
        final ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            private boolean isVisible = false;

            @Override
            public void onClick(View v) {
                isVisible = !isVisible;
                if (isVisible) {
                    etPassword
                            .setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                    ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Change icon
                } else {
                    etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                    ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });
    }

    private void switchTab(String role) {
        currentLoginRole = role;

        tabCitizen.setBackgroundResource(role.equals("CITIZEN") ? R.drawable.bg_tab_selected : 0);
        tabCitizen.setTextColor(ContextCompat.getColor(this, role.equals("CITIZEN") ? R.color.tab_text_selected : R.color.tab_text_unselected));

        tabAuthority.setBackgroundResource(role.equals("AUTHORITY") ? R.drawable.bg_tab_selected : 0);
        tabAuthority.setTextColor(ContextCompat.getColor(this, role.equals("AUTHORITY") ? R.color.tab_text_selected : R.color.tab_text_unselected));

        if (tabSupervisor != null) {
            tabSupervisor.setBackgroundResource(role.equals("SUPERVISOR") ? R.drawable.bg_tab_selected : 0);
            tabSupervisor.setTextColor(ContextCompat.getColor(this, role.equals("SUPERVISOR") ? R.color.tab_text_selected : R.color.tab_text_unselected));
        }

        if (role.equals("CITIZEN")) {
            tvInputLabel.setText(R.string.email_address);
            etMobile.setHint(R.string.hint_email);
            etMobile.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            layoutCreateAccount.setVisibility(View.VISIBLE);
            btnLogin.setText(R.string.login_citizen);
        } else if (role.equals("AUTHORITY")) {
            tvInputLabel.setText(R.string.officer_id);
            etMobile.setHint(R.string.hint_officer_id);
            etMobile.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            layoutCreateAccount.setVisibility(View.GONE);
            btnLogin.setText(R.string.login_authority);
        } else {
            tvInputLabel.setText("Supervisor ID");
            etMobile.setHint("Enter Supervisor ID");
            etMobile.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            layoutCreateAccount.setVisibility(View.GONE);
            btnLogin.setText("LOGIN AS SUPERVISOR");
        }
    }
}
