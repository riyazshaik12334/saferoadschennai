package com.simats.saferoadschennaisrc;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView ivFullScreenImage = findViewById(R.id.ivFullScreenImage);
        ImageButton btnClose = findViewById(R.id.btnClose);

        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("ngrok-skip-browser-warning", "true")
                    .build());

            Glide.with(this)
                    .load(glideUrl)
                    .into(ivFullScreenImage);
        } else {
            Toast.makeText(this, "No image to display", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnClose.setOnClickListener(v -> finish());
    }
}
