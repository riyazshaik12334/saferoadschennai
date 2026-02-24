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

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = etNewPassword.getText().toString();
                String confirmPass = etConfirmPassword.getText().toString();

                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ResetPasswordActivity.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                // Navigate to Success Screen
                android.content.Intent intent = new android.content.Intent(ResetPasswordActivity.this,
                        ResetPasswordSuccessActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
