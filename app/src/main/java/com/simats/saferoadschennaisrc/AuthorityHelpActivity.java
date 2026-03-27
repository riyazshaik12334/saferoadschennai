package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorityHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_help);

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

        // Bottom Navigation
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityComplaintsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navAlerts).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void dialNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
