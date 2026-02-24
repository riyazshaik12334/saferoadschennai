package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
