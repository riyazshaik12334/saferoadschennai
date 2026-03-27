package com.simats.saferoadschennaisrc.network;

import com.simats.saferoadschennaisrc.ComplaintModel;
import com.simats.saferoadschennaisrc.NotificationModel;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public interface ApiService {
    // Auth Endpoints
    @POST("api/login/")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/register/")
    Call<Void> register(@Body RegisterRequest request);

    // Complaint Endpoints
    @GET("api/complaints/")
    Call<List<ComplaintModel>> getComplaints();

    @Multipart
    @POST("api/complaints/")
    Call<ComplaintModel> createComplaint(
            @Part("report_id") RequestBody reportId,
            @Part("title") RequestBody title,
            @Part("zone") RequestBody zone,
            @Part("reporter") RequestBody reporter,
            @Part("status") RequestBody status,
            @Part("date") RequestBody date,
            @Part("priority") RequestBody priority,
            @Part("description") RequestBody description,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("reporter_mobile") RequestBody reporterMobile,
            @Part("reporter_email") RequestBody reporterEmail,
            @Part MultipartBody.Part image);

    @GET("api/notifications/")
    Call<List<NotificationModel>> getNotifications(
            @retrofit2.http.Query("type") String type,
            @retrofit2.http.Query("email") String email);

    @POST("api/notifications/mark_all_as_read/")
    Call<Void> markAllNotificationsAsRead(@Body NotificationMarkAllReadRequest request);

    @PATCH("api/notifications/{id}/")
    Call<NotificationModel> markNotificationAsRead(@Path("id") String id, @Body NotificationStatusUpdate update);

    @PATCH("api/complaints/{id}/")
    Call<ComplaintModel> updateStatus(@Path("id") String id, @Body StatusUpdate update);

    @PATCH("api/complaints/{id}/")
    Call<ComplaintModel> updateComplaintStatus(@Path("id") String id, @Body Map<String, Object> body);

    @retrofit2.http.DELETE("api/complaints/{id}/")
    Call<Void> deleteComplaint(@Path("id") String id);

    @Multipart
    @PATCH("api/complaints/{id}/")
    Call<ComplaintModel> submitSupervisorEvidence(
            @Path("id") String id,
            @Part("supervisor_name") RequestBody supervisorName,
            @Part MultipartBody.Part supervisorImage,
            @Part MultipartBody.Part proof);

    @POST("api/complaints/{id}/delete_evidence/")
    Call<Void> deleteSupervisorEvidence(@Path("id") String id);

    @POST("api/update-profile/")
    Call<AuthResponse> updateProfile(@Body ProfileUpdateRequest request);

    @POST("api/change-password/")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    @POST("api/reset-password/")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest request);

    @POST("api/send-otp/")
    Call<ResponseBody> sendOtp(@Body SendOtpRequest request);

    @POST("api/verify-otp/")
    Call<ResponseBody> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/delete-account/")
    Call<Void> deleteAccount(@Body DeleteAccountRequest request);

    // Data Classes for requests/responses
    public class DeleteAccountRequest {
        public String email;

        public DeleteAccountRequest(String email) {
            this.email = email;
        }
    }

    public class ResetPasswordRequest {
        public String email;
        public String new_password;

        public ResetPasswordRequest(String email, String new_password) {
            this.email = email;
            this.new_password = new_password;
        }
    }

    public class SendOtpRequest {
        public String email;

        public SendOtpRequest(String email) {
            this.email = email;
        }
    }

    public class VerifyOtpRequest {
        public String email;
        public String code;

        public VerifyOtpRequest(String email, String code) {
            this.email = email;
            this.code = code;
        }
    }

    public class ChangePasswordRequest {
        public String email, current_password, new_password;

        public ChangePasswordRequest(String email, String current_password, String new_password) {
            this.email = email;
            this.current_password = current_password;
            this.new_password = new_password;
        }
    }

    public class LoginRequest {
        public String email, password, role;

        public LoginRequest(String email, String password, String role) {
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }

    public class ProfileUpdateRequest {
        public String email, name;

        public ProfileUpdateRequest(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }

    public class RegisterRequest {
        public String name, email, password, mobile;

        public RegisterRequest(String n, String e, String p, String m) {
            this.name = n;
            this.email = e;
            this.password = p;
            this.mobile = m;
        }
    }

    public class AuthResponse {
        public String token, name, email, role;
    }

    public class StatusUpdate {
        public String status;

        public StatusUpdate(String s) {
            this.status = s;
        }
    }

    public class NotificationStatusUpdate {
        public boolean is_read;

        public NotificationStatusUpdate(boolean isRead) {
            this.is_read = isRead;
        }
    }

    public class NotificationMarkAllReadRequest {
        public String type;
        public String email;

        public NotificationMarkAllReadRequest(String type, String email) {
            this.type = type;
            this.email = email;
        }
    }
}
