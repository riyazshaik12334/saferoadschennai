package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {
    private NotificationAdapter adapter;
    private RecyclerView rvNotifications;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_authority);

        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentRole = prefs.getString("USER_ROLE", "AUTHORITY");

        updateList(currentRole);

        // Setup "Mark all as read" click
        findViewById(R.id.tvMarkRead).setOnClickListener(v -> {
            RetrofitClient.getApiService().markAllNotificationsAsRead(
                    new ApiService.NotificationMarkAllReadRequest(currentRole, null)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(NotificationsActivity.this, "Alerts cleared",
                            Toast.LENGTH_SHORT).show();
                    updateList(currentRole);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(NotificationsActivity.this, "Failed to clear alerts",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Bottom Navigation logic
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            Intent intent;
            if ("SUPERVISOR".equalsIgnoreCase(currentRole)) {
                intent = new Intent(this, SupervisorMainActivity.class);
            } else {
                intent = new Intent(this, AuthorityMainActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            Intent intent;
            if ("SUPERVISOR".equalsIgnoreCase(currentRole)) {
                intent = new Intent(this, SupervisorComplaintsActivity.class);
            } else {
                intent = new Intent(this, AuthorityComplaintsActivity.class);
            }
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, AuthorityProfileActivity.class));
        });
    }

    private void updateList(String role) {
        RetrofitClient.getApiService().getNotifications(role, null)
                .enqueue(new Callback<List<NotificationModel>>() {
                    @Override
                    public void onResponse(Call<List<NotificationModel>> call,
                                           Response<List<NotificationModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<NotificationModel> list = response.body();
                            if (list.isEmpty()) {
                                layoutEmpty.setVisibility(View.VISIBLE);
                                rvNotifications.setVisibility(View.GONE);
                            } else {
                                layoutEmpty.setVisibility(View.GONE);
                                rvNotifications.setVisibility(View.VISIBLE);
                                adapter = new NotificationAdapter(list);
                                rvNotifications.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                        Toast.makeText(NotificationsActivity.this, "Failed to fetch alerts",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
