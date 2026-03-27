package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AuthorityComplaintAdapter extends RecyclerView.Adapter<AuthorityComplaintAdapter.ComplaintViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ComplaintModel complaint);
    }

    private List<ComplaintModel> complaintList;
    private List<ComplaintModel> filteredList;
    private OnItemClickListener listener;
    private String currentSearchQuery = "";
    private String currentFilterStatus = "All";

    public AuthorityComplaintAdapter(List<ComplaintModel> complaintList, OnItemClickListener listener) {
        this.complaintList = complaintList != null ? complaintList : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.complaintList);
        this.listener = listener;
    }

    public void setList(List<ComplaintModel> newList) {
        this.complaintList = newList != null ? newList : new ArrayList<>();
        applyFilterAndSearch();
    }

    public void search(String query) {
        this.currentSearchQuery = query.toLowerCase();
        applyFilterAndSearch();
    }

    public void filter(String status) {
        this.currentFilterStatus = status;
        applyFilterAndSearch();
    }

    private void applyFilterAndSearch() {
        filteredList.clear();
        for (ComplaintModel item : complaintList) {
            String status = item.getStatus() != null ? item.getStatus() : "";
            String title = item.getTitle() != null ? item.getTitle() : "";
            String id = item.getId() != null ? item.getId() : "";
            String zone = item.getZone() != null ? item.getZone() : "";

            boolean matchesFilter = currentFilterStatus.equalsIgnoreCase("All") || 
                                 status.equalsIgnoreCase(currentFilterStatus);
            
            boolean matchesSearch = currentSearchQuery.isEmpty() || 
                                 (title != null && title.toLowerCase().contains(currentSearchQuery)) ||
                                 (id != null && id.toLowerCase().contains(currentSearchQuery)) ||
                                 (zone != null && zone.toLowerCase().contains(currentSearchQuery));
            
            if (matchesFilter && matchesSearch) {
                filteredList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_authority_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        ComplaintModel complaint = filteredList.get(position);
        holder.tvTitle.setText(complaint.getTitle());
        holder.tvStatus.setText(complaint.getStatus());
        holder.tvZoneInfo.setText("Location: " + complaint.getZone());
        holder.tvMobileInfo.setText("Contact: " + complaint.getReporterMobile() + " (" + complaint.getReporter() + ")");
        holder.tvReportId.setText(complaint.getId());
        holder.tvDate.setText(complaint.getDate());

        String rawBaseUrl = RetrofitClient.BASE_URL;
        final String baseUrl = rawBaseUrl.endsWith("api/") ? rawBaseUrl.substring(0, rawBaseUrl.length() - 4) : rawBaseUrl;

        if (complaint.getImageUri() != null && !complaint.getImageUri().isEmpty()) {
            String imageUrl = complaint.getImageUri();
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
            GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("ngrok-skip-browser-warning", "true")
                    .build());

            Glide.with(holder.itemView.getContext())
                    .load(glideUrl)
                    .placeholder(R.drawable.bg_login_gradient)
                    .error(R.drawable.bg_login_gradient)
                    .into(holder.imgComplaint);
        } else {
            holder.imgComplaint.setImageResource(R.drawable.bg_login_gradient);
        }

        if ("High".equalsIgnoreCase(complaint.getPriority())) {
            holder.tvPriority.setVisibility(View.VISIBLE);
        } else {
            holder.tvPriority.setVisibility(View.GONE);
        }

        // Enhanced Status Styling
        int bgDrawableRes, statusColor;
        int statusIconRes;

        String status = complaint.getStatus();
        if (status == null)
            status = "Pending";

        switch (status.toLowerCase()) {
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
        holder.tvStatus.setText(status.toUpperCase());
        holder.tvStatus.setTextColor(statusColor);
        holder.imgStatusIcon.setImageResource(statusIconRes);
        holder.imgStatusIcon.setColorFilter(statusColor);
        holder.imgEditStatus.setVisibility(View.VISIBLE);

        // Main Card Click -> Update Status or Navigate
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(complaint);
            } else if (v.getContext() instanceof AuthorityComplaintsActivity) {
                ((AuthorityComplaintsActivity) v.getContext()).showStatusUpdateMenu(complaint);
            } else {
                android.widget.Toast.makeText(v.getContext(), "Update status for: " + complaint.getId(),
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Image Click -> Full Screen Image
        holder.imgComplaint.setOnClickListener(v -> {
            if (complaint.getImageUri() != null && !complaint.getImageUri().isEmpty()) {
                String imgUrl = complaint.getImageUri();
                
                // Clean up the URL
                if (imgUrl.contains("localhost:8000")) {
                    imgUrl = imgUrl.replace("http://localhost:8000/", baseUrl);
                } else if (!imgUrl.startsWith("http") && !imgUrl.startsWith("content") && !imgUrl.startsWith("file")) {
                    if (!imgUrl.startsWith("/")) {
                        imgUrl = "/" + imgUrl;
                    }
                    if (baseUrl.endsWith("/")) {
                        imgUrl = baseUrl + imgUrl.substring(1);
                    } else {
                        imgUrl = baseUrl + imgUrl;
                    }
                }
                
                android.content.Intent intent = new android.content.Intent(v.getContext(), FullScreenImageActivity.class);
                intent.putExtra("IMAGE_URL", imgUrl);
                v.getContext().startActivity(intent);
            } else {
                android.widget.Toast.makeText(v.getContext(), "No image available", android.widget.Toast.LENGTH_SHORT)
                        .show();
            }
        });

        View.OnClickListener mapListener = v -> {
            double lat = complaint.getLatitude();
            double lon = complaint.getLongitude();
            String uri;
            if (lat != 0 && lon != 0) {
                uri = "geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + Uri.encode(complaint.getTitle()) + ")";
            } else {
                uri = "geo:0,0?q=" + Uri.encode(complaint.getZone() + ", Chennai");
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            } else {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        };

        holder.layoutLocation.setOnClickListener(mapListener);

        holder.layoutMobile.setOnClickListener(v -> {
            String mobile = complaint.getReporterMobile();
            if (mobile != null && !mobile.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobile));
                if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(intent);
                } else {
                    android.widget.Toast
                            .makeText(v.getContext(), "No dialer app found", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnDirections.setOnClickListener(v -> {
            double lat = complaint.getLatitude();
            double lon = complaint.getLongitude();
            String uri;
            if (lat != 0 && lon != 0) {
                uri = "google.navigation:q=" + lat + "," + lon;
            } else {
                uri = "google.navigation:q=" + Uri.encode(complaint.getZone() + ", Chennai");
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            } else {
                // Fallback to browser directions if app not found
                String webUri = "https://www.google.com/maps/dir/?api=1&destination=" +
                        (lat != 0 ? lat + "," + lon : Uri.encode(complaint.getZone() + ", Chennai"));
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUri)));
            }
        });

        // Supervisor Evidence Population
        if (complaint.getSupervisorName() != null && !complaint.getSupervisorName().isEmpty()) {
            holder.layoutSupervisorEvidence.setVisibility(View.VISIBLE);
            holder.tvSupervisorName.setText(complaint.getSupervisorName());
            if (complaint.getSupervisorImage() != null && !complaint.getSupervisorImage().isEmpty()) {
                String supImgUrl = complaint.getSupervisorImage();
                if (supImgUrl.contains("localhost:8000")) supImgUrl = supImgUrl.replace("http://localhost:8000/", baseUrl);
                else if (!supImgUrl.startsWith("http")) {
                    if (baseUrl.endsWith("/") && supImgUrl.startsWith("/")) supImgUrl = baseUrl + supImgUrl.substring(1);
                    else supImgUrl = baseUrl + (supImgUrl.startsWith("/") ? "" : "/") + supImgUrl;
                }
                
                GlideUrl supGlideUrl = new GlideUrl(supImgUrl, new LazyHeaders.Builder()
                        .addHeader("ngrok-skip-browser-warning", "true")
                        .build());

                Glide.with(holder.itemView.getContext())
                        .load(supGlideUrl)
                        .placeholder(R.drawable.bg_login_gradient)
                        .into(holder.imgSupervisorProof);
            }
        } else {
            holder.layoutSupervisorEvidence.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvZoneInfo, tvMobileInfo, tvReportId, tvDate, tvPriority;
        ImageView imgComplaint, imgStatusIcon, imgEditStatus, imgSupervisorProof;
        CardView cardStatus;
        View layoutLocation, layoutMobile, btnDirections, layoutSupervisorEvidence;
        TextView tvSupervisorName;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvZoneInfo = itemView.findViewById(R.id.tvZoneInfo);
            tvMobileInfo = itemView.findViewById(R.id.tvMobileInfo);
            tvReportId = itemView.findViewById(R.id.tvReportId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            imgComplaint = itemView.findViewById(R.id.imgComplaint);
            imgStatusIcon = itemView.findViewById(R.id.imgStatusIcon);
            imgEditStatus = itemView.findViewById(R.id.imgEditStatus);
            cardStatus = itemView.findViewById(R.id.cardStatus);
            layoutLocation = itemView.findViewById(R.id.layoutLocation);
            layoutMobile = itemView.findViewById(R.id.layoutMobile);
            btnDirections = itemView.findViewById(R.id.btnDirections);
            layoutSupervisorEvidence = itemView.findViewById(R.id.layoutSupervisorEvidence);
            imgSupervisorProof = itemView.findViewById(R.id.imgSupervisorProof);
            tvSupervisorName = itemView.findViewById(R.id.tvSupervisorName);
        }
    }
}
