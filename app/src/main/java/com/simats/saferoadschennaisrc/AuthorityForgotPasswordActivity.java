package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorityForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_forgot_password);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Emergency Contacts
        findViewById(R.id.btnCallPolice).setOnClickListener(v -> dialNumber("100"));
        findViewById(R.id.btnCallFire).setOnClickListener(v -> dialNumber("101"));
        findViewById(R.id.btnCallAmbulance).setOnClickListener(v -> dialNumber("108"));

        // Admin Support - Dial Number
        findViewById(R.id.cardAdminSupport).setOnClickListener(v -> {
            dialNumber("+919347678977");
        });
    }

    private void dialNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
