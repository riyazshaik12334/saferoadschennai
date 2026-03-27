package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.saferoadschennaisrc.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorMainActivity extends AppCompatActivity {
    private TextView tvActiveTasks, tvCompletedTasks, tvTotalTasks, tvSupervisorName;
    private androidx.recyclerview.widget.RecyclerView rvQueue;
    private SupervisorComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_main);

        tvActiveTasks = findViewById(R.id.tvActiveTasksCount);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasksCount);
        tvTotalTasks = findViewById(R.id.tvTotalTasksCount);
        tvSupervisorName = findViewById(R.id.tvSupervisorName);
        rvQueue = findViewById(R.id.rvInvestigationQueue);

        rvQueue.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = new SupervisorComplaintAdapter(new java.util.ArrayList<>(), new SupervisorComplaintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ComplaintModel complaint) {
                Intent intent = new Intent(SupervisorMainActivity.this, SupervisorSubmitActivity.class);
                intent.putExtra("REPORT_ID", complaint.getId());
                intent.putExtra("TITLE", complaint.getTitle());
                intent.putExtra("IMAGE_URL", complaint.getImageUri());
                startActivity(intent);
            }

            @Override
            public void onDeleteProofClick(ComplaintModel complaint) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(SupervisorMainActivity.this)
                        .setTitle("Delete Evidence")
                        .setMessage("Are you sure you want to delete the submitted proof and supervisor info?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteEvidence(complaint.getId()))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        rvQueue.setAdapter(adapter);

        refreshSupervisorInfo();
        fetchJobStats();

        findViewById(R.id.btnViewAllWork).setOnClickListener(v -> {
            startActivity(new Intent(this, SupervisorComplaintsActivity.class));
        });

        findViewById(R.id.navWork).setOnClickListener(v -> {
            startActivity(new Intent(this, SupervisorComplaintsActivity.class));
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, AuthorityProfileActivity.class));
        });
        
        findViewById(R.id.btnNotification).setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });
    }

    private void refreshSupervisorInfo() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = prefs.getString("USER_NAME", "Alpha");
        tvSupervisorName.setText("Supervisor " + userName);
    }

    private void fetchJobStats() {
        RetrofitClient.getApiService().getComplaints().enqueue(new Callback<List<ComplaintModel>>() {
            @Override
            public void onResponse(Call<List<ComplaintModel>> call, Response<List<ComplaintModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ComplaintModel> all = response.body();
                    List<ComplaintModel> inProgressList = new java.util.ArrayList<>();
                    int active = 0;
                    int completed = 0;
                    for(ComplaintModel c : all) {
                        if ("In Progress".equalsIgnoreCase(c.getStatus())) {
                            active++;
                            inProgressList.add(c);
                        }
                        if ("Resolved".equalsIgnoreCase(c.getStatus()) && c.getSupervisorName() != null) completed++;
                    }
                    tvActiveTasks.setText(String.valueOf(active));
                    tvTotalTasks.setText(String.valueOf(active)); // Matches web logic (Total assigned = In Progress)
                    tvCompletedTasks.setText(String.valueOf(completed));
                    
                    adapter.setList(inProgressList);
                }
            }

            @Override
            public void onFailure(Call<List<ComplaintModel>> call, Throwable t) { }
        });
    }

    private void deleteEvidence(String reportId) {
        RetrofitClient.getApiService().deleteSupervisorEvidence(reportId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    android.widget.Toast.makeText(SupervisorMainActivity.this, "Evidence deleted", android.widget.Toast.LENGTH_SHORT).show();
                    fetchJobStats(); // Refresh
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.widget.Toast.makeText(SupervisorMainActivity.this, "Delete failed: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
