package com.simats.saferoadschennaisrc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    public AuthorityComplaintAdapter(List<ComplaintModel> complaintList, OnItemClickListener listener) {
        this.complaintList = complaintList;
        this.filteredList = new ArrayList<>(complaintList);
        this.listener = listener;
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
        holder.tvZoneInfo.setText(complaint.getZone() + " • " + complaint.getReporter());
        holder.tvReportId.setText(complaint.getId());
        holder.tvDate.setText(complaint.getDate());
        holder.imgComplaint.setImageResource(complaint.getImageResId());

        if ("High".equalsIgnoreCase(complaint.getPriority())) {
            holder.tvPriority.setVisibility(View.VISIBLE);
        } else {
            holder.tvPriority.setVisibility(View.GONE);
        }

        // Status Styling
        int bgColor, textColor;
        switch (complaint.getStatus().toLowerCase()) {
            case "in progress":
                bgColor = R.drawable.bg_status_inprogress;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_inprogress_text);
                break;
            case "resolved":
                bgColor = R.drawable.bg_status_resolved;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_resolved_text);
                break;
            case "pending":
            default:
                bgColor = R.drawable.bg_status_pending;
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending_text);
                break;
        }

        holder.tvStatus.setBackgroundResource(bgColor);
        holder.tvStatus.setTextColor(textColor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(complaint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String status) {
        filteredList.clear();
        if (status.equalsIgnoreCase("All")) {
            filteredList.addAll(complaintList);
        } else {
            for (ComplaintModel item : complaintList) {
                if (item.getStatus().equalsIgnoreCase(status)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void search(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(complaintList);
        } else {
            for (ComplaintModel item : complaintList) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getId().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setList(List<ComplaintModel> newList) {
        this.complaintList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        ImageView imgComplaint;
        TextView tvPriority, tvTitle, tvStatus, tvZoneInfo, tvReportId, tvDate;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            imgComplaint = itemView.findViewById(R.id.imgComplaint);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvZoneInfo = itemView.findViewById(R.id.tvZoneInfo);
            tvReportId = itemView.findViewById(R.id.tvReportId);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
