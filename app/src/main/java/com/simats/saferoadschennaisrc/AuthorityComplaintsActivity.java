package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AuthorityComplaintsActivity extends AppCompatActivity {

    private RecyclerView rvComplaints;
    private AuthorityComplaintAdapter adapter;
    private List<ComplaintModel> allComplaints;
    private TextView tvItemCount, chipAll, chipPending, chipInProgress, chipResolved;
    private LinearLayout emptyState;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_complaints);

        initializeViews();
        setupDummyData();
        setupRecyclerView();
        checkEmptyState();
        setupSearch();
        setupFilters();
        setupBottomNavigation();
    }

    private void initializeViews() {
        rvComplaints = findViewById(R.id.rvComplaints);
        tvItemCount = findViewById(R.id.tvItemCount);
        emptyState = findViewById(R.id.emptyState);
        etSearch = findViewById(R.id.etSearch);

        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipInProgress = findViewById(R.id.chipInProgress);
        chipResolved = findViewById(R.id.chipResolved);
    }

    private String currentFilter = "All";

    private void setupDummyData() {
        allComplaints = new ArrayList<>();
        // Adding an example case as requested
        allComplaints.add(new ComplaintModel("RPT-2023-901", "Mount Road, Near Metro", "Zone 10", "Priya S.", "Pending",
                "13 Feb 2026", "High", R.drawable.bg_login_gradient));
    }

    private void setupRecyclerView() {
        adapter = new AuthorityComplaintAdapter(allComplaints, complaint -> {
            showStatusUpdateMenu(complaint);
        });
        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        rvComplaints.setAdapter(adapter);
        updateItemCount(allComplaints.size());
    }

    private void showStatusUpdateMenu(ComplaintModel complaint) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this,
                rvComplaints.findViewHolderForAdapterPosition(allComplaints.indexOf(complaint)).itemView);
        popup.getMenu().add("Pending");
        popup.getMenu().add("In Progress");
        popup.getMenu().add("Resolved");

        popup.setOnMenuItemClickListener(item -> {
            String newStatus = item.getTitle().toString();
            // Update the status in our list
            int index = allComplaints.indexOf(complaint);
            if (index != -1) {
                allComplaints.set(index, new ComplaintModel(
                        complaint.getId(),
                        complaint.getTitle(),
                        complaint.getZone(),
                        complaint.getReporter(),
                        newStatus,
                        complaint.getDate(),
                        complaint.getPriority(),
                        complaint.getImageResId()));

                // Refresh the current filter
                adapter.setList(allComplaints);
                adapter.filter(currentFilter);
                checkEmptyState();
                Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popup.show();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.search(s.toString());
                checkEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupFilters() {
        chipAll.setOnClickListener(v -> selectFilter(chipAll, "All"));
        chipPending.setOnClickListener(v -> selectFilter(chipPending, "Pending"));
        chipInProgress.setOnClickListener(v -> selectFilter(chipInProgress, "In Progress"));
        chipResolved.setOnClickListener(v -> selectFilter(chipResolved, "Resolved"));
    }

    private void selectFilter(TextView selectedChip, String status) {
        // Reset all chips
        resetChip(chipAll);
        resetChip(chipPending);
        resetChip(chipInProgress);
        resetChip(chipResolved);

        // Style selected chip
        selectedChip.setBackgroundResource(R.drawable.bg_tab_selected);
        selectedChip.setBackgroundTintList(null); // Clear tint to show primary color
        selectedChip.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(ColorUtils.getColor(this, "#1A237E")));
        selectedChip.setTextColor(ContextCompat.getColor(this, R.color.white));

        adapter.filter(status);
        currentFilter = status;
        checkEmptyState();
    }

    // Helper to fix the color issue if ColorUtils is not available
    private static class ColorUtils {
        public static int getColor(android.content.Context context, String colorHash) {
            return android.graphics.Color.parseColor(colorHash);
        }
    }

    private void resetChip(TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_tab_unselected);
        chip.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFFFF")));
        chip.setTextColor(android.graphics.Color.parseColor("#455A64"));
    }

    private void checkEmptyState() {
        int count = adapter.getItemCount();
        if (count == 0) {
            rvComplaints.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvComplaints.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
        updateItemCount(count);
    }

    private void updateItemCount(int count) {
        tvItemCount.setText(count + " Items");
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorityMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navComplaints).setOnClickListener(v -> {
            // Already here
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
}
