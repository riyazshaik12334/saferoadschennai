package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);
        ImageView btnBack = findViewById(R.id.btnBack);

        View.OnClickListener navigateToLogin = v -> {
            Intent intent = new Intent(RegisterSuccessActivity.this, LoginActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        };

        btnBackToLogin.setOnClickListener(navigateToLogin);

        // Back arrow also goes to login or just finishes
        if (btnBack != null) {
            btnBack.setOnClickListener(navigateToLogin);
        }
    }
}
