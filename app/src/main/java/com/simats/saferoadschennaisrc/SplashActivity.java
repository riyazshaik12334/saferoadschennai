package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to start the Main Activity after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                String token = prefs.getString("AUTH_TOKEN", null);
                String role = prefs.getString("USER_ROLE", "");

                Intent intent;
                if (token != null && !token.isEmpty()) {
                    if ("AUTHORITY".equalsIgnoreCase(role)) {
                        intent = new Intent(SplashActivity.this, AuthorityMainActivity.class);
                    } else if ("SUPERVISOR".equalsIgnoreCase(role)) {
                        intent = new Intent(SplashActivity.this, SupervisorMainActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    }
                } else {
                    intent = new Intent(SplashActivity.this, PremiumActivity.class);
                }
                startActivity(intent);
                finish(); 
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }
}
