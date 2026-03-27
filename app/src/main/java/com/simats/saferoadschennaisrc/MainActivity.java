package com.simats.saferoadschennaisrc;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.saferoadschennaisrc.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private android.os.Handler pollingHandler = new android.os.Handler();
    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            fetchReportsFromServer();
            updateNotificationDot();
            pollingHandler.postDelayed(this, 30000); // Poll every 30 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the username from the Intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            userName = "Citizen";
        }

        // Bind the TextView and set the welcome message
        android.widget.TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvWelcomeName.setText("Welcome, " + userName);

        // Setup "Report Now" button click
        findViewById(R.id.btnReportNow).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        // Setup Notification button click
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        updateNotificationDot();
        // Setup My Reports button click
        findViewById(R.id.navMyReports).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MyReportsActivity.class);
            startActivity(intent);
        });

        // Setup View All click
        findViewById(R.id.btnViewAll).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MyReportsActivity.class);
            startActivity(intent);
        });

        // Setup Report button (Bottom Nav)
        findViewById(R.id.navReport).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        // Setup Home button (Bottom Nav)
        findViewById(R.id.navHome).setOnClickListener(v -> {
            // Already on Home
        });

        // Setup Profile button (Bottom Nav)
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ProfileActivity.class);
            String email = getSharedPreferences("UserSession", MODE_PRIVATE).getString("USER_EMAIL", "");
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
        });

        updateDashboardStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Refresh username in case it was edited in EditProfileActivity
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = prefs.getString("USER_NAME", "Citizen");
        android.widget.TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        if (tvWelcomeName != null) {
            tvWelcomeName.setText("Welcome, " + userName);
        }
        
        pollingHandler.post(pollingRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    private void fetchReportsFromServer() {
        RetrofitClient.getApiService().getComplaints().enqueue(new Callback<List<ComplaintModel>>() {
            @Override
            public void onResponse(Call<List<ComplaintModel>> call, Response<List<ComplaintModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update global store
                    ComplaintStore.getInstance().getComplaints().clear();
                    ComplaintStore.getInstance().getComplaints().addAll(response.body());
                    updateDashboardStats();
                }
            }

            @Override
            public void onFailure(Call<List<ComplaintModel>> call, Throwable t) {
                // Fallback to local if offline
                updateDashboardStats();
            }
        });
    }

    private void updateNotificationDot() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userEmail = prefs.getString("USER_EMAIL", "");

        RetrofitClient.getApiService().getNotifications("CITIZEN", userEmail)
                .enqueue(new Callback<List<NotificationModel>>() {
                    @Override
                    public void onResponse(Call<List<NotificationModel>> call,
                            Response<List<NotificationModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean hasUnread = false;
                            for (NotificationModel n : response.body()) {
                                if (!n.isRead()) {
                                    hasUnread = true;
                                    break;
                                }
                            }
                            android.view.View dot = findViewById(R.id.btnNotifications).findViewById(R.id.notifDot);
                            if (dot != null) {
                                dot.setVisibility(hasUnread ? android.view.View.VISIBLE : android.view.View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                        // ignore error
                    }
                });
    }

    private void updateDashboardStats() {
        ComplaintStore store = ComplaintStore.getInstance();

        // Fetch real user email from session for filtering stats
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("USER_EMAIL", "");

        int totalCount = 0;
        int activeCount = 0;
        int fixedCount = 0;

        for (ComplaintModel report : store.getComplaints()) {
            if (report.getReporterEmail() != null && report.getReporterEmail().equals(currentUserEmail)) {
                totalCount++;
                if ("Pending".equalsIgnoreCase(report.getStatus())
                        || "In Progress".equalsIgnoreCase(report.getStatus())) {
                    activeCount++;
                } else if ("Resolved".equalsIgnoreCase(report.getStatus())
                        || "Fixed".equalsIgnoreCase(report.getStatus())) {
                    fixedCount++;
                }
            }
        }

        android.widget.TextView tvTotalCount = findViewById(R.id.tvTotalCount);
        android.widget.TextView tvActiveCount = findViewById(R.id.tvActiveCount);
        android.widget.TextView tvFixedCount = findViewById(R.id.tvFixedCount);

        if (tvTotalCount != null)
            tvTotalCount.setText(String.valueOf(totalCount));
        if (tvActiveCount != null)
            tvActiveCount.setText(String.valueOf(activeCount));
        if (tvFixedCount != null)
            tvFixedCount.setText(String.valueOf(fixedCount));

        // Populating Recent Reports List (Latest 3)
        androidx.recyclerview.widget.RecyclerView rvRecent = findViewById(R.id.rvRecentReports);
        android.view.View tvEmpty = findViewById(R.id.tvEmptyRecent);

        java.util.List<ComplaintModel> recentList = new java.util.ArrayList<>();
        for (ComplaintModel report : store.getComplaints()) {
            if (report.getReporterEmail() != null && report.getReporterEmail().equals(currentUserEmail)) {
                recentList.add(report);
                if (recentList.size() >= 3)
                    break;
            }
        }

        if (recentList.isEmpty()) {
            rvRecent.setVisibility(android.view.View.GONE);
            tvEmpty.setVisibility(android.view.View.VISIBLE);
        } else {
            rvRecent.setVisibility(android.view.View.VISIBLE);
            tvEmpty.setVisibility(android.view.View.GONE);

            MyReportAdapter adapter = new MyReportAdapter(recentList, report -> {
                android.content.Intent intent = new android.content.Intent(this, MyReportsActivity.class);
                startActivity(intent);
            });
            rvRecent.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            rvRecent.setAdapter(adapter);
        }
    }
}