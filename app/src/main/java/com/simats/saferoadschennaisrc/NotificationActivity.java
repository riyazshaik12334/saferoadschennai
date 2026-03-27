package com.simats.saferoadschennaisrc;

import com.simats.saferoadschennaisrc.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.simats.saferoadschennaisrc.NotificationAdapter;
import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import com.simats.saferoadschennaisrc.NotificationModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private NotificationAdapter adapter;
    private RecyclerView rvNotifications;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        updateList();

        // Setup "Mark all as read" click
        findViewById(R.id.tvMarkRead).setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            String userEmail = prefs.getString("USER_EMAIL", "");

            RetrofitClient.getApiService().markAllNotificationsAsRead(
                    new ApiService.NotificationMarkAllReadRequest("CITIZEN", userEmail)).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(NotificationActivity.this, "Notifications cleared", Toast.LENGTH_SHORT)
                                    .show();
                            updateList();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(NotificationActivity.this, "Failed to clear notifications",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Setup Home navigation to return to MainActivity
        findViewById(R.id.navHome).setOnClickListener(v -> finish());

        findViewById(R.id.navReport).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ReportActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navMyReports).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, MyReportsActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Mark all as read when opened logic can be added here
    }

    private void updateList() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userEmail = prefs.getString("USER_EMAIL", "");

        RetrofitClient.getApiService().getNotifications("CITIZEN", userEmail)
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
                        Toast.makeText(NotificationActivity.this, "Failed to fetch notifications", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
}
