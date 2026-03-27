package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import java.util.List;

public class SupervisorComplaintAdapter extends RecyclerView.Adapter<SupervisorComplaintAdapter.ViewHolder> {
    private List<ComplaintModel> complaints;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ComplaintModel complaint);
        void onDeleteProofClick(ComplaintModel complaint);
    }

    public SupervisorComplaintAdapter(List<ComplaintModel> complaints, OnItemClickListener listener) {
        this.complaints = complaints != null ? complaints : new java.util.ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supervisor_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintModel item = complaints.get(position);
        holder.tvTitle.setText(item.getTitle());
        String cleanId = item.getId();
        if (cleanId != null) {
            if (cleanId.toLowerCase().startsWith("src - ")) cleanId = cleanId.substring(6);
            else if (cleanId.toLowerCase().startsWith("src-")) cleanId = cleanId.substring(4);
            else if (cleanId.toLowerCase().startsWith("src ")) cleanId = cleanId.substring(4);
            else if (cleanId.toLowerCase().startsWith("src")) cleanId = cleanId.substring(3);
        }
        holder.tvId.setText("src - " + cleanId);
        holder.tvZone.setText(item.getZone());
        holder.tvDate.setText(item.getDate());

        // Consolidate baseUrl calculation and ensure it doesn't end with a slash for easier joining
        String rawBaseUrl = RetrofitClient.BASE_URL;
        if (rawBaseUrl == null) rawBaseUrl = "";
        String tempBaseUrl = rawBaseUrl.endsWith("api/") ? rawBaseUrl.substring(0, rawBaseUrl.length() - 4) : rawBaseUrl;
        if (tempBaseUrl.endsWith("/")) tempBaseUrl = tempBaseUrl.substring(0, tempBaseUrl.length() - 1);
        final String baseUrl = tempBaseUrl;

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            String imageUrl = item.getImageUri();
            if (imageUrl.contains("localhost:8000")) {
                imageUrl = imageUrl.replace("http://localhost:8000/", baseUrl + "/");
            } else if (!imageUrl.startsWith("http") && !imageUrl.startsWith("content") && !imageUrl.startsWith("file")) {
                imageUrl = baseUrl + (imageUrl.startsWith("/") ? "" : "/") + imageUrl;
            }

            GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("ngrok-skip-browser-warning", "true")
                    .build());

            Glide.with(holder.itemView.getContext())
                    .load(glideUrl)
                    .placeholder(R.drawable.bg_login_gradient)
                    .error(R.drawable.bg_login_gradient)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.bg_login_gradient);
        }

        holder.tvReporterInfo.setText("Reporter: " + (item.getReporter() != null ? item.getReporter() : "N/A"));
        holder.tvReporterMobile.setText(item.getReporterMobile() != null ? item.getReporterMobile() : "N/A");

        // Image Click -> Full Screen Image
        holder.ivImage.setOnClickListener(v -> {
            if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                String imgUrl = item.getImageUri();
                if (imgUrl.contains("localhost:8000")) {
                    imgUrl = imgUrl.replace("http://localhost:8000/", baseUrl + "/");
                } else if (!imgUrl.startsWith("http") && !imgUrl.startsWith("content") && !imgUrl.startsWith("file")) {
                    imgUrl = baseUrl + (imgUrl.startsWith("/") ? "" : "/") + imgUrl;
                }
                Intent intent = new Intent(v.getContext(), FullScreenImageActivity.class);
                intent.putExtra("IMAGE_URL", imgUrl);
                v.getContext().startActivity(intent);
            }
        });

        holder.btnDirections.setOnClickListener(v -> {
            double lat = item.getLatitude();
            double lon = item.getLongitude();
            String uri = "google.navigation:q=" + (lat != 0 ? lat + "," + lon : Uri.encode(item.getZone() + ", Chennai"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            v.getContext().startActivity(intent);
        });

        holder.btnSubmitProof.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        // Status Pill Logic
        String s = item.getStatus();
        if (s == null || s.isEmpty()) s = "Pending";
        String displayStatus = s;
        if ("In Progress".equalsIgnoreCase(s)) displayStatus = "Processing";
        holder.tvStatus.setText(displayStatus.toUpperCase());

        int pillColor = 0xFFFFF9C4; // Default Yellow
        int textColor = 0xFFFBC02D;

        if ("Pending".equalsIgnoreCase(s)) {
            pillColor = 0xFFFFE0B2; // Light Orange
            textColor = 0xFFEF6C00; // Dark Orange
        } else if ("Resolved".equalsIgnoreCase(s) || "Closed".equalsIgnoreCase(s) || "Fixed".equalsIgnoreCase(s)) {
            pillColor = 0xFFE8F5E9; // Light Green
            textColor = 0xFF2E7D32; // Dark Green
        } else {
            // Processing / In Progress / etc.
            pillColor = 0xFFE3F2FD; // Light Blue
            textColor = 0xFF1565C0; // Dark Blue
        }
        ((androidx.cardview.widget.CardView)holder.cardStatus).setCardBackgroundColor(pillColor);
        holder.tvStatus.setTextColor(textColor);

        // Submitted Proof Visibility
        boolean isResolved = "Resolved".equalsIgnoreCase(s) || "Closed".equalsIgnoreCase(s) || "Fixed".equalsIgnoreCase(s);

        if (item.getProof() != null && !item.getProof().isEmpty()) {
            holder.layoutProof.setVisibility(View.VISIBLE);
            String proofUrl = item.getProof();
            if (!proofUrl.startsWith("http")) {
                proofUrl = baseUrl + (proofUrl.startsWith("/") ? "" : "/") + proofUrl;
            }

            GlideUrl glideUrl = new GlideUrl(proofUrl, new LazyHeaders.Builder()
                    .addHeader("ngrok-skip-browser-warning", "true")
                    .build());

            Glide.with(holder.itemView.getContext())
                    .load(glideUrl)
                    .placeholder(R.drawable.bg_login_gradient)
                    .into(holder.ivProofImage);

            if (item.getSupervisorUpdatedAt() != null && !item.getSupervisorUpdatedAt().isEmpty()) {
                holder.tvProofTime.setVisibility(View.VISIBLE);
                try {
                    String raw = item.getSupervisorUpdatedAt();
                    if (raw.length() >= 16) {
                        String dp = raw.substring(0, 10);
                        String tp = raw.substring(11, 16);
                        holder.tvProofTime.setText(dp + " " + tp);
                    } else {
                        holder.tvProofTime.setText(raw);
                    }
                } catch (Exception e) {
                    holder.tvProofTime.setText(item.getSupervisorUpdatedAt());
                }
            } else {
                holder.tvProofTime.setVisibility(View.GONE);
            }

            holder.ivProofImage.setOnClickListener(v -> {
                String imgUrl = item.getProof();
                if (!imgUrl.startsWith("http")) {
                    imgUrl = baseUrl + (imgUrl.startsWith("/") ? "" : "/") + imgUrl;
                }
                Intent intent1 = new Intent(v.getContext(), FullScreenImageActivity.class);
                intent1.putExtra("IMAGE_URL", imgUrl);
                v.getContext().startActivity(intent1);
            });

            // Hide delete option if Resolved
            if (isResolved) {
                holder.btnDeleteProof.setVisibility(View.GONE);
                holder.btnSubmitProof.setVisibility(View.GONE);
            } else {
                holder.btnDeleteProof.setVisibility(View.VISIBLE);
                holder.btnSubmitProof.setVisibility(View.VISIBLE);
                holder.btnDeleteProof.setOnClickListener(v -> {
                    if (listener != null) listener.onDeleteProofClick(item);
                });
            }
        } else {
            holder.btnSubmitProof.setVisibility(View.VISIBLE);
        }

        // Citizen Feedback Visibility
        if (item.getRating() != null && item.getRating() > 0) {
            holder.layoutFeedback.setVisibility(View.VISIBLE);
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                if (i < item.getRating()) stars.append("★");
                else stars.append("☆");
            }
            holder.tvRatingStars.setText(stars.toString());
            holder.tvFeedbackDescription.setText(item.getRatingDescription() != null ? "\"" + item.getRatingDescription() + "\"" : "No comments provided.");
        } else {
            holder.layoutFeedback.setVisibility(View.GONE);
        }

        // Dual Attribution Visibility
        StringBuilder attribution = new StringBuilder();
        if (item.getAuthorityName() != null && !item.getAuthorityName().isEmpty()) {
            attribution.append(item.getStatus().toLowerCase().contains("process") ? "Taken by: " : "Resolved by: ");
            attribution.append(item.getAuthorityName());
        }
        if (item.getSupervisorName() != null && !item.getSupervisorName().isEmpty()) {
            if (attribution.length() > 0) attribution.append(" | ");
            attribution.append("Proof by: ").append(item.getSupervisorName());
        }

        if (attribution.length() > 0) {
            holder.tvAuthorityName.setVisibility(View.VISIBLE);
            holder.tvAuthorityName.setText(attribution.toString());
        } else {
            holder.tvAuthorityName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return complaints != null ? complaints.size() : 0;
    }

    public void setList(List<ComplaintModel> newList) {
        this.complaints = newList != null ? newList : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvId, tvZone, tvDate, tvReporterInfo, tvReporterMobile, tvStatus, tvProofTime, tvRatingStars, tvFeedbackDescription, tvAuthorityName;
        View btnDirections, btnSubmitProof, layoutProof, cardStatus, layoutFeedback;
        ImageView ivImage, ivProofImage, btnDeleteProof;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvComplaintTitle);
            tvId = itemView.findViewById(R.id.tvComplaintId);
            tvZone = itemView.findViewById(R.id.tvComplaintZone);
            tvDate = itemView.findViewById(R.id.tvComplaintDate);
            tvReporterInfo = itemView.findViewById(R.id.tvReporterInfo);
            tvReporterMobile = itemView.findViewById(R.id.tvReporterMobile);
            tvStatus = itemView.findViewById(R.id.tvStatusText);
            tvProofTime = itemView.findViewById(R.id.tvProofTime);
            tvRatingStars = itemView.findViewById(R.id.tvRatingStars);
            tvFeedbackDescription = itemView.findViewById(R.id.tvFeedbackDescription);
            btnDirections = itemView.findViewById(R.id.btnDirections);
            btnSubmitProof = itemView.findViewById(R.id.btnSubmitProof);
            ivImage = itemView.findViewById(R.id.ivComplaintImage);
            layoutProof = itemView.findViewById(R.id.layoutProof);
            ivProofImage = itemView.findViewById(R.id.ivProofImage);
            btnDeleteProof = itemView.findViewById(R.id.btnDeleteProof);
            cardStatus = itemView.findViewById(R.id.cardStatus);
            layoutFeedback = itemView.findViewById(R.id.layoutFeedback);
            tvAuthorityName = itemView.findViewById(R.id.tvAuthorityName);
        }
    }
}

