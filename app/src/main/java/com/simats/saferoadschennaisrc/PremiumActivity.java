package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
                // In a real app, this would start the payment flow
                // For now, it just goes to login after "upgrading"
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(PremiumActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
