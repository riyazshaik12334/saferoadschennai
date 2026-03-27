package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.saferoadschennaisrc.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorityMainActivity extends AppCompatActivity {
    private android.os.Handler pollingHandler = new android.os.Handler();
    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            fetchReportsFromServer();
            updateNotificationDot();
            pollingHandler.postDelayed(this, 30000); // Poll every 30 seconds
        }
    };

    private TextView tvPendingCount, tvInProgressCount, tvResolvedCount, tvTotalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_main);

        // Initialize count text views
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvResolvedCount = findViewById(R.id.tvResolvedCount);
        tvTotalCount = findViewById(R.id.tvTotalCount);

        updateDashboardStats();

        // Notification Click
        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation logic
        findViewById(R.id.btnViewComplaints).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityMainActivity.this, AuthorityComplaintsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            // Already on Dashboard
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            Intent intent = new Intent(AuthorityMainActivity.this, AuthorityComplaintsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navAlerts).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOfficerName();
        pollingHandler.post(pollingRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    private void refreshOfficerName() {
        android.widget.TextView tvOfficerName = findViewById(R.id.tvOfficerName);
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = prefs.getString("USER_NAME", "Suresh");

        if (userName != null && userName.startsWith("Officer ")) {
            tvOfficerName.setText(userName);
        } else {
            tvOfficerName.setText("Officer " + userName);
        }
    }

    private void fetchReportsFromServer() {
        RetrofitClient.getApiService().getComplaints().enqueue(new Callback<List<ComplaintModel>>() {
            @Override
            public void onResponse(Call<List<ComplaintModel>> call, Response<List<ComplaintModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ComplaintStore.getInstance().getComplaints().clear();
                    ComplaintStore.getInstance().getComplaints().addAll(response.body());
                    updateDashboardStats();
                }
            }

            @Override
            public void onFailure(Call<List<ComplaintModel>> call, Throwable t) {
                updateDashboardStats();
            }
        });
    }

    private void updateNotificationDot() {
        RetrofitClient.getApiService().getNotifications("AUTHORITY", null)
                .enqueue(new Callback<List<NotificationModel>>() {
                    @Override
                    public void onResponse(Call<List<NotificationModel>> call,
                            Response<List<NotificationModel>> response) {
                        if (isFinishing() || isDestroyed())
                            return;
                        if (response.isSuccessful() && response.body() != null) {
                            boolean hasUnread = false;
                            for (NotificationModel n : response.body()) {
                                if (n != null && !n.isRead()) {
                                    hasUnread = true;
                                    break;
                                }
                            }
                            android.view.View btnNotification = findViewById(R.id.btnNotification);
                            if (btnNotification != null) {
                                android.view.View dot = btnNotification.findViewById(R.id.notifDot);
                                if (dot != null) {
                                    dot.setVisibility(hasUnread ? android.view.View.VISIBLE : android.view.View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                        // Ignore fail
                    }
                });
    }

    private void updateDashboardStats() {
        ComplaintStore store = ComplaintStore.getInstance();
        tvPendingCount.setText(String.valueOf(store.getPendingCount()));
        tvInProgressCount.setText(String.valueOf(store.getInProgressCount()));
        tvResolvedCount.setText(String.valueOf(store.getResolvedCount()));
        tvTotalCount.setText(String.valueOf(store.getTotalCount()));

        android.widget.LinearLayout layoutPriorityAlerts = findViewById(R.id.layoutPriorityAlerts);
        android.widget.LinearLayout layoutRecentActivity = findViewById(R.id.layoutRecentActivity);

        if (store.getTotalCount() == 0) {
            layoutPriorityAlerts.setVisibility(android.view.View.GONE);
            layoutRecentActivity.setVisibility(android.view.View.GONE);
        } else {
            layoutPriorityAlerts.setVisibility(android.view.View.VISIBLE);
            layoutRecentActivity.setVisibility(android.view.View.VISIBLE);
        }

        // Populating Priority Alerts RecyclerView
        androidx.recyclerview.widget.RecyclerView rvPriority = findViewById(R.id.rvPriorityAlerts);
        android.view.View tvEmptyPriority = findViewById(R.id.tvEmptyPriority);

        java.util.List<ComplaintModel> highPriorityList = new java.util.ArrayList<>();
        for (ComplaintModel report : store.getComplaints()) {
            if ("High".equalsIgnoreCase(report.getPriority())) {
                highPriorityList.add(report);
                if (highPriorityList.size() >= 3)
                    break;
            }
        }

        if (highPriorityList.isEmpty()) {
            rvPriority.setVisibility(android.view.View.GONE);
            tvEmptyPriority.setVisibility(android.view.View.VISIBLE);
        } else {
            rvPriority.setVisibility(android.view.View.VISIBLE);
            tvEmptyPriority.setVisibility(android.view.View.GONE);

            AuthorityComplaintAdapter adapter = new AuthorityComplaintAdapter(highPriorityList, report -> {
                android.content.Intent intent = new android.content.Intent(this, AuthorityComplaintsActivity.class);
                startActivity(intent);
            });
            rvPriority.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            rvPriority.setAdapter(adapter);
        }
    }
}
