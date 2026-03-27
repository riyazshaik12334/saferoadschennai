package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.saferoadschennaisrc.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorComplaintsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SupervisorComplaintAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private List<ComplaintModel> workList = new ArrayList<>();
    private List<ComplaintModel> masterList = new ArrayList<>();
    private com.google.android.material.chip.ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_complaints);

        recyclerView = findViewById(R.id.rvSupervisorComplaints);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SupervisorComplaintAdapter(workList, new SupervisorComplaintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ComplaintModel complaint) {
                onComplaintClick(complaint);
            }

            @Override
            public void onDeleteProofClick(ComplaintModel complaint) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(SupervisorComplaintsActivity.this)
                        .setTitle("Delete Evidence")
                        .setMessage("Are you sure you want to delete the submitted proof and supervisor info?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteEvidence(complaint.getId()))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        chipGroup = findViewById(R.id.chipGroupFilter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filterWorkList();
        });

        fetchWorkQueue();
    }

    private void filterWorkList() {
        if (masterList == null) return;
        workList.clear();
        
        boolean showCompleted = chipGroup.getCheckedChipId() == R.id.chipCompleted;
        
        for (ComplaintModel c : masterList) {
            String s = c.getStatus() != null ? c.getStatus() : "";
            boolean isResolved = "Resolved".equalsIgnoreCase(s) || "Closed".equalsIgnoreCase(s) || "Fixed".equalsIgnoreCase(s);
            
            if (showCompleted) {
                if (isResolved) workList.add(c);
            } else {
                // Ongoing
                // Ongoing - only show if Authority has marked it Processing or In Progress
                if ("In Progress".equalsIgnoreCase(s) || "Processing".equalsIgnoreCase(s)) {
                    workList.add(c);
                }
            }
        }
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(workList.isEmpty() ? View.VISIBLE : View.GONE);
        if (showCompleted && workList.isEmpty()) {
            tvEmpty.setText("No completed work found.");
        } else if (workList.isEmpty()) {
            tvEmpty.setText("No active tasks found.");
        }
    }

    private void fetchWorkQueue() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().getComplaints().enqueue(new Callback<List<ComplaintModel>>() {
            @Override
            public void onResponse(Call<List<ComplaintModel>> call, Response<List<ComplaintModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    masterList.addAll(response.body());
                    filterWorkList();
                }
            }

            @Override
            public void onFailure(Call<List<ComplaintModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void onComplaintClick(ComplaintModel complaint) {
        Intent intent = new Intent(this, SupervisorSubmitActivity.class);
        intent.putExtra("REPORT_ID", complaint.getId());
        intent.putExtra("TITLE", complaint.getTitle());
        intent.putExtra("IMAGE_URL", complaint.getImageUri());
        startActivity(intent);
    }

    private void deleteEvidence(String id) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().deleteSupervisorEvidence(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(SupervisorComplaintsActivity.this, "Evidence deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchWorkQueue();
                } else {
                    Toast.makeText(SupervisorComplaintsActivity.this, "Failed to delete evidence", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SupervisorComplaintsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
