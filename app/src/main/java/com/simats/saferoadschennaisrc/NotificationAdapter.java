package com.simats.saferoadschennaisrc;

import com.simats.saferoadschennaisrc.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationModel> notifications;

    public NotificationAdapter(List<NotificationModel> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        String rawTime = notification.getTimestamp();
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            // Substring early to strip milliseconds safely before parsing
            java.util.Date parsedDate = inputFormat.parse(rawTime.substring(0, 19));
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMM dd, yyyy, hh:mm a", java.util.Locale.getDefault());
            holder.tvTime.setText(outputFormat.format(parsedDate));
        } catch (Exception e) {
            // Fallback
            holder.tvTime.setText(rawTime.replace("T", " ").substring(0, Math.min(rawTime.length(), 16)));
        }

        // Simple icon logic based on title or type
        String title = notification.getTitle();
        if (title != null && title.contains("Resolved")) {
            holder.imgIcon.setImageResource(R.drawable.ic_check);
        } else if (title != null && title.contains("Progress")) {
            holder.imgIcon.setImageResource(R.drawable.ic_timer);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_notifications);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvMessage, tvTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgNotificationIcon);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
        }
    }
}
