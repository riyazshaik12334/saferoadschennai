package com.simats.saferoadschennaisrc;

import android.net.Uri;
import com.bumptech.glide.Glide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyReportAdapter extends RecyclerView.Adapter<MyReportAdapter.ReportViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(ComplaintModel complaint);
    }

    private List<ComplaintModel> reportList;
    private List<ComplaintModel> filteredList;
    private OnDeleteClickListener deleteListener;

    public MyReportAdapter(List<ComplaintModel> reportList, OnDeleteClickListener deleteListener) {
        this.reportList = reportList;
        this.filteredList = new ArrayList<>(reportList);
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ComplaintModel report = filteredList.get(position);
        holder.tvTitle.setText(report.getTitle());
        holder.tvStatus.setText(report.getStatus());
        holder.tvZoneInfo.setText("Location: " + report.getZone());
        String cleanId = report.getId();
        if (cleanId != null) {
            if (cleanId.toLowerCase().startsWith("src - ")) cleanId = cleanId.substring(6);
            else if (cleanId.toLowerCase().startsWith("src-")) cleanId = cleanId.substring(4);
            else if (cleanId.toLowerCase().startsWith("src ")) cleanId = cleanId.substring(4);
            else if (cleanId.toLowerCase().startsWith("src")) cleanId = cleanId.substring(3);
        }
        holder.tvReportId.setText("src - " + cleanId);
        holder.tvDate.setText(report.getDate());

        // Consolidate baseUrl calculation
        String rawBaseUrl = com.simats.saferoadschennaisrc.network.RetrofitClient.BASE_URL;
        if (rawBaseUrl == null) rawBaseUrl = "";
        final String baseUrl = rawBaseUrl.endsWith("api/") ? rawBaseUrl.substring(0, rawBaseUrl.length() - 4) : rawBaseUrl;

        if (report.getImageUri() != null && !report.getImageUri().isEmpty()) {
            String imageUrl = report.getImageUri();
            if (imageUrl.contains("localhost:8000")) {
                imageUrl = imageUrl.replace("http://localhost:8000/", baseUrl);
            } else if (!imageUrl.startsWith("http") && !imageUrl.startsWith("content")
                    && !imageUrl.startsWith("file")) {
                if (baseUrl.endsWith("/") && imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + imageUrl.substring(1);
                } else if (!baseUrl.endsWith("/") && !imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + "/" + imageUrl;
                } else {
                    imageUrl = baseUrl + imageUrl;
                }
            }
            com.bumptech.glide.load.model.GlideUrl glideUrl = new com.bumptech.glide.load.model.GlideUrl(imageUrl,
                    new com.bumptech.glide.load.model.LazyHeaders.Builder()
                            .addHeader("ngrok-skip-browser-warning", "true")
                            .build());

            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(glideUrl)
                    .placeholder(R.drawable.bg_login_gradient)
                    .error(R.drawable.bg_login_gradient)
                    .into(holder.imgComplaint);
        } else {
            holder.imgComplaint.setImageResource(R.drawable.bg_login_gradient);
        }

        if ("High".equalsIgnoreCase(report.getPriority())) {
            holder.tvPriority.setVisibility(View.VISIBLE);
        } else {
            holder.tvPriority.setVisibility(View.GONE);
        }

        // Consistent Status Styling (Muted backgrounds with high-contrast text)
        int bgDrawableRes, statusColor;
        int statusIconRes;

        switch (report.getStatus().toLowerCase()) {
            case "processing":
            case "in progress":
                bgDrawableRes = R.drawable.bg_status_inprogress_solid;
                statusIconRes = R.drawable.ic_timer;
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_inprogress_text);
                break;
            case "resolved":
            case "fixed":
                bgDrawableRes = R.drawable.bg_status_resolved_solid;
                statusIconRes = R.drawable.ic_checkmark;
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_resolved_text);
                break;
            case "pending":
            default:
                bgDrawableRes = R.drawable.bg_status_pending_solid;
                statusIconRes = R.drawable.ic_warning;
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending_text);
                break;
        }

        holder.cardStatus.setBackgroundResource(bgDrawableRes);
        String displayStatus = report.getStatus();
        if ("in progress".equalsIgnoreCase(displayStatus)) displayStatus = "Processing";
        holder.tvStatus.setText(displayStatus.toUpperCase());
        holder.tvStatus.setTextColor(statusColor);
        holder.imgStatusIcon.setImageResource(statusIconRes);
        holder.imgStatusIcon.setColorFilter(statusColor);

        if ("Pending".equalsIgnoreCase(report.getStatus())) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(report);
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnDelete.setOnClickListener(null);
        }

        // Image Click -> Open Image in Full Screen
        holder.imgComplaint.setOnClickListener(v -> {
            String imageUrl = report.getImageUri();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.contains("localhost:8000")) {
                    imageUrl = imageUrl.replace("http://localhost:8000/", baseUrl);
                } else if (!imageUrl.startsWith("http") && !imageUrl.startsWith("content")
                        && !imageUrl.startsWith("file")) {
                    if (baseUrl.endsWith("/") && imageUrl.startsWith("/"))
                        imageUrl = baseUrl + imageUrl.substring(1);
                    else if (!baseUrl.endsWith("/") && !imageUrl.startsWith("/"))
                        imageUrl = baseUrl + "/" + imageUrl;
                    else
                        imageUrl = baseUrl + imageUrl;
                }
                android.content.Intent intent = new android.content.Intent(v.getContext(),
                        FullScreenImageActivity.class);
                intent.putExtra("IMAGE_URL", imageUrl);
                v.getContext().startActivity(intent);
            } else {
                android.widget.Toast.makeText(v.getContext(), "No image available", android.widget.Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Map Click -> Open Google Maps Pin
        View.OnClickListener mapListener = v -> {
            double lat = report.getLatitude();
            double lon = report.getLongitude();
            String uri;
            if (lat != 0 && lon != 0) {
                uri = "geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "("
                        + android.net.Uri.encode(report.getTitle()) + ")";
            } else {
                uri = "geo:0,0?q=" + android.net.Uri.encode(report.getZone() + ", Chennai");
            }
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            } else {
                v.getContext().startActivity(
                        new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri)));
            }
        };

        holder.layoutLocation.setOnClickListener(mapListener);

        // Resolution Details Binding
        if ("Resolved".equalsIgnoreCase(report.getStatus()) || "Fixed".equalsIgnoreCase(report.getStatus())) {
            holder.layoutResolutionDetails.setVisibility(View.VISIBLE);
            
            // Attribution
            String auth = report.getAuthorityName() != null ? report.getAuthorityName() : "Assigned Authority";
            String sup = report.getSupervisorName() != null ? report.getSupervisorName() : "Field Supervisor";
            holder.tvAttribution.setText("Resolved by: " + auth + " | Supervisor: " + sup);

            // Proof Image
            if (report.getProof() != null && !report.getProof().isEmpty()) {
                String proofUrl = report.getProof();
                if (proofUrl.contains("localhost:8000")) {
                    proofUrl = proofUrl.replace("http://localhost:8000/", baseUrl);
                } else if (!proofUrl.startsWith("http")) {
                    proofUrl = baseUrl + (proofUrl.startsWith("/") ? proofUrl : "/" + proofUrl);
                }
                
                GlideUrl proofGlideUrl = new GlideUrl(proofUrl,
                        new LazyHeaders.Builder()
                                .addHeader("ngrok-skip-browser-warning", "true")
                                .build());

                Glide.with(holder.itemView.getContext())
                        .load(proofGlideUrl)
                        .placeholder(R.drawable.bg_login_gradient)
                        .into(holder.imgProof);
                
                holder.imgProof.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(v.getContext(), FullScreenImageActivity.class);
                    intent.putExtra("IMAGE_URL", proofGlideUrl.toString());
                    v.getContext().startActivity(intent);
                });
            }

            // Feedback Display vs Action
            if (report.getRating() != null && report.getRating() > 0) {
                holder.btnRateResolution.setVisibility(View.GONE);
                holder.layoutFeedbackDisplay.setVisibility(View.VISIBLE);
                
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < 5; i++) {
                    if (i < report.getRating()) stars.append("★");
                    else stars.append("☆");
                }
                holder.tvRatingStars.setText(stars.toString());
                holder.tvFeedbackComment.setText(report.getRatingDescription() != null ? "\"" + report.getRatingDescription() + "\"" : "");
            } else {
                holder.btnRateResolution.setVisibility(View.VISIBLE);
                holder.layoutFeedbackDisplay.setVisibility(View.GONE);
                holder.btnRateResolution.setOnClickListener(v -> showFeedbackDialog(report, holder.itemView.getContext()));
            }
        } else {
            holder.layoutResolutionDetails.setVisibility(View.GONE);
        }

    }

    private void showFeedbackDialog(ComplaintModel report, android.content.Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.rbRating);
        EditText etComment = dialogView.findViewById(R.id.etComment);

        new MaterialAlertDialogBuilder(context)
                .setTitle("Rate Resolution")
                .setMessage("How satisfied are you with the fix for report " + report.getId() + "?")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    int rating = (int) ratingBar.getRating();
                    String comment = etComment.getText().toString();
                    if (rating == 0) {
                        Toast.makeText(context, "Please select at least 1 star", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitFeedback(report.getId(), rating, comment, context);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitFeedback(String id, int rating, String comment, android.content.Context context) {
        Map<String, Object> body = new HashMap<>();
        body.put("rating", rating);
        body.put("rating_description", comment);

        RetrofitClient.getApiService().updateComplaintStatus(id, body).enqueue(new Callback<ComplaintModel>() {
            @Override
            public void onResponse(Call<ComplaintModel> call, Response<ComplaintModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                    // Ideally refresh the specific item or notify activity
                } else {
                    Toast.makeText(context, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ComplaintModel> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void setList(List<ComplaintModel> newList) {
        this.reportList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String status) {
        filteredList.clear();
        if (status.equalsIgnoreCase("All")) {
            filteredList.addAll(reportList);
        } else if (status.equalsIgnoreCase("Active")) {
            for (ComplaintModel item : reportList) {
                if ("Pending".equalsIgnoreCase(item.getStatus()) || "Processing".equalsIgnoreCase(item.getStatus()) || "In Progress".equalsIgnoreCase(item.getStatus())) {
                    filteredList.add(item);
                }
            }
        } else if (status.equalsIgnoreCase("Resolved") || status.equalsIgnoreCase("Fixed")) {
            for (ComplaintModel item : reportList) {
                if ("Resolved".equalsIgnoreCase(item.getStatus()) || "Fixed".equalsIgnoreCase(item.getStatus())) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView imgComplaint, btnDelete, imgStatusIcon, imgProof;
        TextView tvPriority, tvTitle, tvStatus, tvZoneInfo, tvReportId, tvDate, tvAttribution, tvRatingStars, tvFeedbackComment;
        View cardStatus, layoutLocation, layoutResolutionDetails, layoutFeedbackDisplay;
        Button btnRateResolution;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            imgComplaint = itemView.findViewById(R.id.imgComplaint);
            imgStatusIcon = itemView.findViewById(R.id.imgStatusIcon);
            imgProof = itemView.findViewById(R.id.imgProof);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvZoneInfo = itemView.findViewById(R.id.tvZoneInfo);
            tvReportId = itemView.findViewById(R.id.tvReportId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAttribution = itemView.findViewById(R.id.tvAttribution);
            tvRatingStars = itemView.findViewById(R.id.tvRatingStars);
            tvFeedbackComment = itemView.findViewById(R.id.tvFeedbackComment);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnRateResolution = itemView.findViewById(R.id.btnRateResolution);
            cardStatus = itemView.findViewById(R.id.cardStatus);
            layoutLocation = itemView.findViewById(R.id.layoutLocation);
            layoutResolutionDetails = itemView.findViewById(R.id.layoutResolutionDetails);
            layoutFeedbackDisplay = itemView.findViewById(R.id.layoutFeedbackDisplay);

        }
    }
}
