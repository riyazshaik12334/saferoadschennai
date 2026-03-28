package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class PremiumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        TextView btnSkip = findViewById(R.id.btnSkip);
        MaterialButton btnUpgrade = findViewById(R.id.btnUpgrade);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PremiumActivity.this, "Opening Payment Gateway...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PremiumActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(PremiumActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
