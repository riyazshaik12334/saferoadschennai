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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous screen
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous screen
            }
        });

        android.widget.EditText etMobile = findViewById(R.id.etMobile);
        android.widget.Button btnSendOtp = findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etMobile.getText().toString().trim();
                if (email.isEmpty()) {
                    etMobile.setError("Enter email address");
                    return;
                }

                android.content.Intent intent = new android.content.Intent(ForgotPasswordActivity.this,
                        VerifyOtpActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}
