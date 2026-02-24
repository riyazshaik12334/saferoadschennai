package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private TextView tabCitizen;
    private TextView tabAuthority;
    private TextView tvInputLabel;
    private android.widget.EditText etMobile;
    private android.widget.LinearLayout layoutCreateAccount;
    private Button btnLogin;
    private boolean isCitizenLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabCitizen = findViewById(R.id.tabCitizen);
        tabAuthority = findViewById(R.id.tabAuthority);
        tvInputLabel = findViewById(R.id.tvInputLabel);
        etMobile = findViewById(R.id.etMobile);
        layoutCreateAccount = findViewById(R.id.layoutCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);

        // Set initial hints
        etMobile.setHint(R.string.hint_email);

        tabCitizen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(true);
            }
        });

        tabAuthority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(false);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCitizenLogin) {
                    String email = etMobile.getText().toString();
                    String userName = "Guest";
                    if (!email.isEmpty()) {
                        userName = email.length() > 5 ? email.substring(0, 5) : email;
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_NAME", userName);
                    intent.putExtra("USER_EMAIL", email);

                    // Save to SharedPreferences for global access
                    getSharedPreferences("UserSession", MODE_PRIVATE).edit()
                            .putString("USER_EMAIL", email)
                            .putString("USER_NAME", userName)
                            .apply();

                    startActivity(intent);
                    finish(); // Optional: finish LoginActivity so back button doesn't return to login
                } else {
                    // Authority Login logic
                    String officerId = etMobile.getText().toString();
                    if (officerId.isEmpty())
                        officerId = "Officer";

                    // Save to SharedPreferences for global access
                    getSharedPreferences("UserSession", MODE_PRIVATE).edit()
                            .putString("USER_NAME", officerId)
                            .apply();

                    Intent intent = new Intent(LoginActivity.this, AuthorityMainActivity.class);
                    intent.putExtra("USER_NAME", officerId);
                    startActivity(intent);
                    finish();
                }
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
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void switchTab(boolean isCitizen) {
        isCitizenLogin = isCitizen;

        if (isCitizen) {
            tabCitizen.setBackgroundResource(R.drawable.bg_tab_selected);
            tabCitizen.setTextColor(ContextCompat.getColor(this, R.color.tab_text_selected));

            tabAuthority.setBackgroundResource(0);
            tabAuthority.setTextColor(ContextCompat.getColor(this, R.color.tab_text_unselected));

            tvInputLabel.setText(R.string.email_address);
            etMobile.setHint(R.string.hint_email);
            etMobile.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            layoutCreateAccount.setVisibility(View.VISIBLE);

            btnLogin.setText(R.string.login_citizen);
        } else {
            tabAuthority.setBackgroundResource(R.drawable.bg_tab_selected);
            tabAuthority.setTextColor(ContextCompat.getColor(this, R.color.tab_text_selected));

            tabCitizen.setBackgroundResource(0);
            tabCitizen.setTextColor(ContextCompat.getColor(this, R.color.tab_text_unselected));

            tvInputLabel.setText(R.string.officer_id);
            etMobile.setHint(R.string.hint_officer_id);
            etMobile.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            layoutCreateAccount.setVisibility(View.GONE);

            btnLogin.setText(R.string.login_authority);
        }
    }
}
