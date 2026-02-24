package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Header Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
