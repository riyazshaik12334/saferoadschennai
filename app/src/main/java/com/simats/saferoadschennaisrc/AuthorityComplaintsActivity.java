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

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorityComplaintsActivity extends AppCompatActivity {

    private RecyclerView rvComplaints;
    private AuthorityComplaintAdapter adapter;
    private List<ComplaintModel> allComplaints;
    private TextView tvItemCount, chipAll, chipPending, chipInProgress, chipResolved;
    private LinearLayout emptyState;
    private EditText etSearch;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;

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

    @Override
    protected void onResume() {
        super.onResume();
        refreshComplaints();
    }

    private void refreshComplaints() {
        if (swipeRefresh != null)
            swipeRefresh.setRefreshing(true);
        RetrofitClient.getApiService().getComplaints().enqueue(new Callback<List<ComplaintModel>>() {
            @Override
            public void onResponse(Call<List<ComplaintModel>> call, Response<List<ComplaintModel>> response) {
                if (swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allComplaints = response.body();
                    ComplaintStore.getInstance().getComplaints().clear();
                    ComplaintStore.getInstance().getComplaints().addAll(allComplaints);

                    if (adapter != null) {
                        adapter.setList(allComplaints);
                        adapter.filter(currentFilter);
                        updateItemCount(adapter.getItemCount());
                    }
                }
                checkEmptyState();
            }

            @Override
            public void onFailure(Call<List<ComplaintModel>> call, Throwable t) {
                if (swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);
                allComplaints = ComplaintStore.getInstance().getComplaints();
                if (adapter != null) {
                    adapter.setList(allComplaints);
                    adapter.filter(currentFilter);
                    updateItemCount(adapter.getItemCount());
                }
                checkEmptyState();
            }
        });
    }

    private void initializeViews() {
        rvComplaints = findViewById(R.id.rvComplaints);
        tvItemCount = findViewById(R.id.tvItemCount);
        emptyState = findViewById(R.id.emptyState);
        etSearch = findViewById(R.id.etSearch);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(this::refreshComplaints);
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.primary));

        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipInProgress = findViewById(R.id.chipInProgress);
        chipResolved = findViewById(R.id.chipResolved);
    }

    private String currentFilter = "All";

    private void setupDummyData() {
        allComplaints = ComplaintStore.getInstance().getComplaints();
    }

    private void setupRecyclerView() {
        adapter = new AuthorityComplaintAdapter(allComplaints, complaint -> {
            showStatusUpdateMenu(complaint);
        });
        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        rvComplaints.setAdapter(adapter);
        updateItemCount(allComplaints.size());
    }

    public void showStatusUpdateMenu(ComplaintModel complaint) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_status_bottom_sheet, null);

        view.findViewById(R.id.btnStatusPending).setOnClickListener(v -> {
            updateStatus(complaint, "Pending");
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btnStatusInProgress).setOnClickListener(v -> {
            updateStatus(complaint, "In Progress");
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btnStatusResolved).setOnClickListener(v -> {
            updateStatus(complaint, "Resolved");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void updateStatus(ComplaintModel complaint, String newStatus) {
        // Update the status in storage
        ComplaintStore.getInstance().updateStatus(complaint.getId(), newStatus, new Callback<ComplaintModel>() {
            @Override
            public void onResponse(Call<ComplaintModel> call, Response<ComplaintModel> response) {
                refreshComplaints();
                Toast.makeText(AuthorityComplaintsActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<ComplaintModel> call, Throwable t) {
                Toast.makeText(AuthorityComplaintsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });
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
