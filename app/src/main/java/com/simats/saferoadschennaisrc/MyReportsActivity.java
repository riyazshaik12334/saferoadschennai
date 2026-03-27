package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReportsActivity extends AppCompatActivity {

    private TextView tabAll, tabActive, tabResolved;
    private RecyclerView rvReports;
    private MyReportAdapter adapter;
    private View emptyStateView;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        // Initialize Views
        tabAll = findViewById(R.id.tabAll);
        tabActive = findViewById(R.id.tabActive);
        tabResolved = findViewById(R.id.tabResolved);
        rvReports = findViewById(R.id.rvReports);
        emptyStateView = findViewById(R.id.emptyStateView);

        setupRecyclerView();

        View.OnClickListener tabListener = v -> {
            resetTabs();
            v.setBackgroundResource(R.drawable.bg_tab_selected);
            ((TextView) v).setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            if (v.getId() == R.id.tabAll)
                currentFilter = "All";
            else if (v.getId() == R.id.tabActive)
                currentFilter = "Active";
            else if (v.getId() == R.id.tabResolved)
                currentFilter = "Resolved";

            adapter.filter(currentFilter);
            checkEmptyState();
        };

        tabAll.setOnClickListener(tabListener);
        tabActive.setOnClickListener(tabListener);
        tabResolved.setOnClickListener(tabListener);

        // Filter Icon Click
        findViewById(R.id.btnFilter)
                .setOnClickListener(v -> Toast.makeText(this, "Filter options", Toast.LENGTH_SHORT).show());

        // Bottom Navigation Logic
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navReport).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
            finish();
        });

        // Current item is My Reports (No Op)

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshReports();
    }

    private void setupRecyclerView() {
        adapter = new MyReportAdapter(new java.util.ArrayList<>(), report -> {
            showDeleteConfirmation(report);
        });
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(adapter);
    }

    private void refreshReports() {
        // Fetch real user email from session for filtering (permanent ID)
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("USER_EMAIL", "");

        RetrofitClient.getApiService().getComplaints().enqueue(new Callback<List<ComplaintModel>>() {
            @Override
            public void onResponse(Call<List<ComplaintModel>> call, Response<List<ComplaintModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ComplaintModel> allReports = response.body();
                    List<ComplaintModel> userReports = new java.util.ArrayList<>();

                    for (ComplaintModel report : allReports) {
                        String reporterEmail = report.getReporterEmail();
                        if (reporterEmail != null && !reporterEmail.isEmpty()) {
                            if (reporterEmail.equalsIgnoreCase(currentUserEmail)) {
                                userReports.add(report);
                            }
                        } else {
                            // Fallback for older reports that might not have email field yet
                            // but still filtering by name if needed (optional, safer to just use email)
                            String currentUserName = prefs.getString("USER_NAME", "Resident");
                            if (report.getReporter() != null && report.getReporter().equals(currentUserName)) {
                                userReports.add(report);
                            }
                        }
                    }

                    adapter.setList(userReports);
                    adapter.filter(currentFilter);
                    checkEmptyState();

                    // Update store for other activities
                    ComplaintStore.getInstance().getComplaints().clear();
                    ComplaintStore.getInstance().getComplaints().addAll(allReports);
                }
            }

            @Override
            public void onFailure(Call<List<ComplaintModel>> call, Throwable t) {
                // Fallback to local
                List<ComplaintModel> allReports = ComplaintStore.getInstance().getComplaints();
                List<ComplaintModel> userReports = new java.util.ArrayList<>();
                for (ComplaintModel report : allReports) {
                    if (report.getReporterEmail() != null
                            && report.getReporterEmail().equalsIgnoreCase(currentUserEmail)) {
                        userReports.add(report);
                    }
                }
                adapter.setList(userReports);
                adapter.filter(currentFilter);
                checkEmptyState();
            }
        });
    }

    private void showDeleteConfirmation(ComplaintModel report) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RetrofitClient.getApiService().deleteComplaint(report.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                ComplaintStore.getInstance().deleteComplaint(report.getId());
                                refreshReports();
                                Toast.makeText(MyReportsActivity.this, "Report deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyReportsActivity.this, "Failed to delete from server",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MyReportsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkEmptyState() {
        if (adapter == null || adapter.getItemCount() == 0) {
            rvReports.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    private void resetTabs() {
        int unselectedBg = R.drawable.bg_tab_unselected;
        int textColor = ContextCompat.getColor(this, R.color.text_hint);

        tabAll.setBackgroundResource(unselectedBg);
        tabAll.setTextColor(textColor);

        tabActive.setBackgroundResource(unselectedBg);
        tabActive.setTextColor(textColor);

        tabResolved.setBackgroundResource(unselectedBg);
        tabResolved.setTextColor(textColor);
    }
}
