package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Emergency Contacts
        findViewById(R.id.btnCallPolice).setOnClickListener(v -> dialNumber("100"));
        findViewById(R.id.btnCallFire).setOnClickListener(v -> dialNumber("101"));
        findViewById(R.id.btnCallAmbulance).setOnClickListener(v -> dialNumber("108"));

        // GCC Contact Center
        findViewById(R.id.btnCallGCC).setOnClickListener(v -> dialNumber("1913"));
        findViewById(R.id.btnCallControl).setOnClickListener(v -> dialNumber("04425384520"));
        findViewById(R.id.btnWhatsApp).setOnClickListener(v -> dialNumber("+919444122244"));

        // Email Support
        findViewById(R.id.btnEmailSupport).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:chennaisaferoads@gmail.com"));
            intent.setPackage("com.google.android.gm");
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                // Fallback if Gmail app is not installed
                intent.setPackage(null);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    private void dialNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
