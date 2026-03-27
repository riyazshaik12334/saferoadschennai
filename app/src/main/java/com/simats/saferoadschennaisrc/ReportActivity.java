package com.simats.saferoadschennaisrc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.CancellationTokenSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.simats.saferoadschennaisrc.network.ApiService;
import com.simats.saferoadschennaisrc.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportActivity extends AppCompatActivity {

    private TextView btnLow, btnMedium, btnHigh;
    private EditText etLocation, etDescription, etMobile;
    private ImageView imgEvidencePreview;
    private Button btnSubmit;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_CODE = 100;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private Uri currentImageUri;
    private String currentPhotoPath;
    private String severity = "High"; // Default from layout selection
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private String currentZone = "Chennai"; // Default zone
    private ProgressDialog progressDialog;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Views
        btnLow = findViewById(R.id.btnLow);
        btnMedium = findViewById(R.id.btnMedium);
        btnHigh = findViewById(R.id.btnHigh);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etMobile = findViewById(R.id.etMobile);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Severity Selection Logic
        View.OnClickListener severityListener = v -> {
            resetSeverityButtons();
            v.setBackgroundResource(R.drawable.bg_severity_selected);
            ((TextView) v).setTextColor(ContextCompat.getColor(this, android.R.color.white));
        };

        btnLow.setOnClickListener(v -> {
            severity = "Low";
            severityListener.onClick(v);
        });
        btnMedium.setOnClickListener(v -> {
            severity = "Medium";
            severityListener.onClick(v);
        });
        btnHigh.setOnClickListener(v -> {
            severity = "High";
            severityListener.onClick(v);
        });

        // Evidence Photo Selection
        imgEvidencePreview = findViewById(R.id.imgEvidencePreview);
        findViewById(R.id.layoutEvidencePhoto).setOnClickListener(v -> showImageSourceDialog());

        // Auto-detect Location Logic
        findViewById(R.id.btnAutoDetect).setOnClickListener(v -> {
            checkLocationPermission();
        });

        findViewById(R.id.frameMapPreview).setOnClickListener(v -> {
            if (currentLatitude != 0 && currentLongitude != 0) {
                String uri = "geo:" + currentLatitude + "," + currentLongitude + "?q=" + currentLatitude + ","
                        + currentLongitude + "(Report Location)";
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Google Maps not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                checkLocationPermission();
            }
        });

        // Submit Button Mock
        btnSubmit.setOnClickListener(v -> {
            String location = etLocation.getText().toString();
            String description = etDescription.getText().toString(); // Optional
            String mobile = etMobile.getText().toString().trim();

            if (location.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Please fill Location and Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentImageUri == null) {
                Toast.makeText(this, "Please attach an evidence photo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mobile.length() < 10) {
                Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to Mock Store
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String reportId = "SRC-" + timestamp.substring(timestamp.length() - 5);
            String date = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());

            // Fetch real user name from session
            android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            String userName = prefs.getString("USER_NAME", "Resident");
            String userEmail = prefs.getString("USER_EMAIL", "unknown@link.com");

            // API Submission
            showProgress("Compressing Evidence...");

            executorService.execute(() -> {
                MultipartBody.Part body = null;
                if (currentImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), currentImageUri);

                        // Image Compression Logic
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        // Scale down if too large (e.g., max 1920px)
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if (width > 1920 || height > 1920) {
                            float ratio = Math.min(1920f / width, 1920f / height);
                            bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(width * ratio),
                                    Math.round(height * ratio), true);
                        }

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        byte[] bitmapData = bos.toByteArray();

                        File compressedFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "UPLOAD_" + reportId + ".jpg");
                        FileOutputStream fos = new FileOutputStream(compressedFile);
                        fos.write(bitmapData);
                        fos.flush();
                        fos.close();

                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), compressedFile);
                        body = MultipartBody.Part.createFormData("image", compressedFile.getName(), requestFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                MultipartBody.Part finalBody = body;
                runOnUiThread(() -> {
                    progressDialog.setMessage("Submitting Report...");

                    RequestBody fbReportId = RequestBody.create(MediaType.parse("text/plain"), reportId);
                    RequestBody fbTitle = RequestBody.create(MediaType.parse("text/plain"), location);
                    RequestBody fbZone = RequestBody.create(MediaType.parse("text/plain"), currentZone);
                    RequestBody fbReporter = RequestBody.create(MediaType.parse("text/plain"), userName);
                    RequestBody fbStatus = RequestBody.create(MediaType.parse("text/plain"), "Pending");
                    RequestBody fbDate = RequestBody.create(MediaType.parse("text/plain"), date);
                    RequestBody fbPriority = RequestBody.create(MediaType.parse("text/plain"), severity);
                    RequestBody fbDescription = RequestBody.create(MediaType.parse("text/plain"),
                            description != null ? description : "");
                    RequestBody fbLat = RequestBody.create(MediaType.parse("text/plain"),
                            currentLatitude != 0 ? String.valueOf(currentLatitude) : "13.0827");
                    RequestBody fbLon = RequestBody.create(MediaType.parse("text/plain"),
                            currentLongitude != 0 ? String.valueOf(currentLongitude) : "80.2707");
                    RequestBody fbMobile = RequestBody.create(MediaType.parse("text/plain"), mobile);
                    RequestBody fbEmail = RequestBody.create(MediaType.parse("text/plain"), userEmail);

                    RetrofitClient.getApiService().createComplaint(
                            fbReportId, fbTitle, fbZone, fbReporter, fbStatus, fbDate, fbPriority,
                            fbDescription, fbLat, fbLon, fbMobile, fbEmail, finalBody)
                            .enqueue(new Callback<ComplaintModel>() {
                                @Override
                                public void onResponse(Call<ComplaintModel> call, Response<ComplaintModel> response) {
                                    hideProgress();
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ReportActivity.this, "Complaint Reported Successfully!",
                                                Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        btnSubmit.setEnabled(true);
                                        btnSubmit.setText("SUBMIT REPORT");
                                        Toast.makeText(ReportActivity.this, "Submission Failed: Server Error",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ComplaintModel> call, Throwable t) {
                                    hideProgress();
                                    btnSubmit.setEnabled(true);
                                    btnSubmit.setText("SUBMIT REPORT");
                                    Toast.makeText(ReportActivity.this, "Network Error: Check Connection",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                });
            });
        });

        // Setup Bottom Navigation
        View navHome = findViewById(R.id.navHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> finish());
        }

        View navMyReports = findViewById(R.id.navMyReports);
        if (navMyReports != null) {
            navMyReports.setOnClickListener(v -> {
                Intent intent = new Intent(ReportActivity.this, MyReportsActivity.class);
                startActivity(intent);
                finish();
            });
        }

        View navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ReportActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void showImageSourceDialog() {
        String[] options = { "Take Photo", "Choose from Gallery" };
        new AlertDialog.Builder(this)
                .setTitle("Select Evidence Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        checkGalleryPermission();
                    }
                }).show();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    LOCATION_PERMISSION_CODE);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Toast.makeText(this, "Detecting exact location...", Toast.LENGTH_SHORT).show();

        // Request fresh location with high precision
        com.google.android.gms.location.CurrentLocationRequest locationRequest = new com.google.android.gms.location.CurrentLocationRequest.Builder()
                .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(10000)
                .setMaxUpdateAgeMillis(5000)
                .build();

        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(locationRequest, cts.getToken())
                .addOnSuccessListener(this, freshLoc -> {
                    if (freshLoc != null) {
                        updateCoordinates(freshLoc);
                    } else {
                        // Step 2: Fallback to Last Known Location if fresh fails
                        fusedLocationClient.getLastLocation().addOnSuccessListener(this, lastLoc -> {
                            if (lastLoc != null) {
                                updateCoordinates(lastLoc);
                            } else {
                                Toast.makeText(ReportActivity.this,
                                        "Location failed. Please enable High Accuracy in GPS settings.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void updateCoordinates(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        getAddressFromLocation(currentLatitude, currentLongitude);
    }

    private void getAddressFromLocation(double lat, double lon) {
        showProgress("Updating location...");
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                runOnUiThread(() -> {
                    hideProgress();
                    if (addresses != null && !addresses.isEmpty()) {
                        Address addr = addresses.get(0);
                        String address = addr.getAddressLine(0);
                        etLocation.setText(address);

                        if (addr.getLocality() != null) {
                            currentZone = addr.getLocality();
                        } else if (addr.getSubLocality() != null) {
                            currentZone = addr.getSubLocality();
                        }
                        Toast.makeText(this, "Location detected: " + address, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideProgress();
                    etLocation.setText(lat + ", " + lon);
                    Toast.makeText(this, "Coordinates detected", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showProgress(String message) {
        if (isFinishing() || isDestroyed())
            return;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgress() {
        if (isFinishing() || isDestroyed())
            return;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, PERMISSION_CODE);
        } else {
            launchCamera();
        }
    }

    private void checkGalleryPermission() {
        String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, PERMISSION_CODE);
        } else {
            launchGallery();
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
        if (photoFile != null) {
            currentImageUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (currentImageUri != null) {
                    imgEvidencePreview.setImageURI(currentImageUri);
                    imgEvidencePreview.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tvUploadPrompt)).setText("Click to change photo");
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null && data.getData() != null) {
                currentImageUri = data.getData();
                imgEvidencePreview.setImageURI(currentImageUri);
                imgEvidencePreview.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tvUploadPrompt)).setText("Click to change photo");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera/Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera/Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            boolean fineLocationGranted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean coarseLocationGranted = grantResults.length > 1
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (fineLocationGranted || coarseLocationGranted) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File tempFile = File.createTempFile("UPLOAD_" + timeStamp, ".jpg", storageDir);
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            java.io.OutputStream outputStream = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void resetSeverityButtons() {
        int unselectedBg = R.drawable.bg_severity_unselected;
        int textColor = ContextCompat.getColor(this, R.color.text_primary);

        btnLow.setBackgroundResource(unselectedBg);
        btnLow.setTextColor(textColor);

        btnMedium.setBackgroundResource(unselectedBg);
        btnMedium.setTextColor(textColor);

        btnHigh.setBackgroundResource(unselectedBg);
        btnHigh.setTextColor(textColor);
    }
}
