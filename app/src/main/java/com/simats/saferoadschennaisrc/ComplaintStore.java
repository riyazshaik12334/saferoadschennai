package com.simats.saferoadschennaisrc;

import java.util.ArrayList;
import java.util.List;
import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintStore {
    private static ComplaintStore instance;
    private List<ComplaintModel> complaints;

    private ComplaintStore() {
        complaints = new ArrayList<>();
    }

    public static synchronized ComplaintStore getInstance() {
        if (instance == null) {
            instance = new ComplaintStore();
        }
        return instance;
    }

    public List<ComplaintModel> getComplaints() {
        return complaints;
    }

    public void addComplaint(ComplaintModel complaint) {
        complaints.add(0, complaint);
    }

    public void updateStatus(String id, String newStatus, Callback<ComplaintModel> callback) {
        RetrofitClient.getApiService().updateStatus(id, new ApiService.StatusUpdate(newStatus))
                .enqueue(new Callback<ComplaintModel>() {
                    @Override
                    public void onResponse(Call<ComplaintModel> call, Response<ComplaintModel> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < complaints.size(); i++) {
                                ComplaintModel old = complaints.get(i);
                                if (old != null && old.getId() != null && old.getId().equals(id)) {
                                    ComplaintModel updated = new ComplaintModel(
                                            old.getId(), old.getTitle(), old.getZone(), old.getReporter(),
                                            newStatus, old.getDate(), old.getPriority(), old.getImageResId(),
                                            old.getImageUri(),
                                            old.getDescription(), old.getLatitude(), old.getLongitude(),
                                            old.getReporterMobile(), old.getReporterEmail());
                                    complaints.set(i, updated);
                                    break;
                                }
                            }
                        }
                        if (callback != null)
                            callback.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<ComplaintModel> call, Throwable t) {
                        if (callback != null)
                            callback.onFailure(call, t);
                    }
                });
    }

    public void updateComplaintStatus(String id, String newStatus) {
        updateStatus(id, newStatus, null);
    }

    public void deleteComplaint(String id) {
        for (int i = 0; i < complaints.size(); i++) {
            if (complaints.get(i).getId().equals(id)) {
                complaints.remove(i);
                break;
            }
        }
    }

    public int getPendingCount() {
        int count = 0;
        for (ComplaintModel c : complaints) {
            if (c != null && "Pending".equalsIgnoreCase(c.getStatus()))
                count++;
        }
        return count;
    }

    public int getInProgressCount() {
        int count = 0;
        for (ComplaintModel c : complaints) {
            if (c != null && "In Progress".equalsIgnoreCase(c.getStatus()))
                count++;
        }
        return count;
    }

    public int getResolvedCount() {
        int count = 0;
        for (ComplaintModel c : complaints) {
            if (c != null && "Resolved".equalsIgnoreCase(c.getStatus()))
                count++;
        }
        return count;
    }

    public int getTotalCount() {
        return complaints.size();
    }
}
