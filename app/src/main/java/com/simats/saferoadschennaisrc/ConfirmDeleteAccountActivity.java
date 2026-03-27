package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmDeleteAccountActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_delete_account);

        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        findViewById(R.id.btnConfirmDelete).setOnClickListener(v -> {
            deleteAccount();
        });
    }

    private void deleteAccount() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("USER_EMAIL", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnConfirmDelete).setEnabled(false);
        findViewById(R.id.btnCancel).setEnabled(false);

        ApiService apiService = RetrofitClient.getApiService();
        ApiService.DeleteAccountRequest request = new ApiService.DeleteAccountRequest(email);

        apiService.deleteAccount(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ConfirmDeleteAccountActivity.this, "Account deleted successfully", Toast.LENGTH_LONG)
                            .show();
                    clearSessionAndExit();
                } else {
                    findViewById(R.id.btnConfirmDelete).setEnabled(true);
                    findViewById(R.id.btnCancel).setEnabled(true);
                    Toast.makeText(ConfirmDeleteAccountActivity.this, "Deletion failed. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                findViewById(R.id.btnConfirmDelete).setEnabled(true);
                findViewById(R.id.btnCancel).setEnabled(true);
                Toast.makeText(ConfirmDeleteAccountActivity.this, "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSessionAndExit() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
