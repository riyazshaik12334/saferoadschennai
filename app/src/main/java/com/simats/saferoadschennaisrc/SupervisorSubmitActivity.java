package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.ProgressDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.textfield.TextInputEditText;
import com.simats.saferoadschennaisrc.network.RetrofitClient;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupervisorSubmitActivity extends AppCompatActivity {
    private ImageView ivPreview;
    private TextInputEditText etName;
    private String reportId;
    private File photoFile;
    private ProgressDialog progressDialog;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_supervisor_submit);

            reportId = getIntent().getStringExtra("REPORT_ID");
            String title = getIntent().getStringExtra("TITLE");

            TextView tvName = findViewById(R.id.tvComplaintName);
            if (tvName != null) tvName.setText(title != null ? title : "Unknown Complaint");
            
            ivPreview = findViewById(R.id.ivPreview);
            ImageView ivOriginal = findViewById(R.id.ivOriginal);
            etName = findViewById(R.id.etSupervisorName);

            // Load original complaint image if available
            String imageUrl = getIntent().getStringExtra("IMAGE_URL");
            if (ivOriginal != null && imageUrl != null && !imageUrl.isEmpty()) {
                String fullUrl = imageUrl.startsWith("http") ? imageUrl : RetrofitClient.BASE_URL + imageUrl;
                GlideUrl glideUrl = new GlideUrl(fullUrl,
                        new LazyHeaders.Builder()
                                .addHeader("Accept", "image/*")
                                .build());
                Glide.with(this).load(glideUrl).placeholder(R.drawable.bg_login_gradient).into(ivOriginal);
            }

            // Pre-fill supervisor name
            android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            if (etName != null) {
                etName.setText(prefs.getString("USER_NAME", ""));
                // Ensure it's not editable as per request
                etName.setEnabled(false);
                etName.setFocusable(false);
            }

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading evidence... Please wait.");
            progressDialog.setCancelable(false);

            initLaunchers();

            findViewById(R.id.btnCapture).setOnClickListener(v -> showImageSourceDialog());
            findViewById(R.id.cvPhoto).setOnClickListener(v -> showImageSourceDialog());
            findViewById(R.id.btnSubmit).setOnClickListener(v -> submitEvidence());

            // Add back button support
            View btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) btnBack.setOnClickListener(v -> finish());

            // Auto-open picker if this is a fresh launch
            if (savedInstanceState == null) {
                getWindow().getDecorView().postDelayed(this::showImageSourceDialog, 800);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Crash in onCreate: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            // Fallback: if layout fails, we at least don't close silently
        }
    }

    private void initLaunchers() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (photoFile != null) {
                    ivPreview.setImageURI(Uri.fromFile(photoFile));
                    ivPreview.setPadding(0, 0, 0, 0);
                    ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                try {
                    photoFile = copyUriToFile(selectedImage);
                    ivPreview.setImageURI(selectedImage);
                    ivPreview.setPadding(0, 0, 0, 0);
                    ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (IOException e) {
                    Toast.makeText(this, "Error loading image from gallery", Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                dispatchGalleryIntent();
            } else {
                Toast.makeText(this, "Permission denied to access gallery", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSourceDialog() {
        if (isFinishing() || isDestroyed()) return;
        String[] options = {"Take Photo", "Choose from Gallery"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Select Evidence Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) dispatchTakePictureIntent();
                    else dispatchGalleryIntent();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void dispatchGalleryIntent() {
        String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ?
                android.Manifest.permission.READ_MEDIA_IMAGES : android.Manifest.permission.READ_EXTERNAL_STORAGE;

        if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void dispatchTakePictureIntent() {
        try {
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = androidx.core.content.FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "Failed to create image file (null)", Toast.LENGTH_SHORT).show();
            }
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No camera app found on this device", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Camera Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }



    private File copyUriToFile(android.net.Uri uri) throws IOException {
        File copyFile = new File(getCacheDir(), "gallery_image_" + System.currentTimeMillis() + ".jpg");
        java.io.InputStream in = getContentResolver().openInputStream(uri);
        java.io.OutputStream out = new java.io.FileOutputStream(copyFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        out.close();
        in.close();
        return copyFile;
    }

    private void submitEvidence() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty() || photoFile == null) {
            Toast.makeText(this, "Please provide name and photo evidence", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody filePart = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("supervisor_image", photoFile.getName(), filePart);
        MultipartBody.Part proofPart = MultipartBody.Part.createFormData("proof", photoFile.getName(), filePart);

        progressDialog.show();
        RetrofitClient.getApiService().submitSupervisorEvidence(reportId, namePart, imagePart, proofPart).enqueue(new Callback<ComplaintModel>() {
            @Override
            public void onResponse(Call<ComplaintModel> call, Response<ComplaintModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    // Navigate to Success Screen instead of Home directly
                    Intent intent = new Intent(SupervisorSubmitActivity.this, SupervisorSuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SupervisorSubmitActivity.this, "Submission failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ComplaintModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SupervisorSubmitActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
