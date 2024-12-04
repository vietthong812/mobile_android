package tdtu.EStudy_App.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.User;
import tdtu.EStudy_App.network.ConfigCloudinary;
import tdtu.EStudy_App.utils.ToastUtils;

public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText fullname, ageUP, email;
    private ProgressBar progressUpdateUP;
    private ImageButton btnSaveProfile;
    private ImageView avtUserUP;
    private Uri selectedImageUri;
    private String currentAvt;
    private FirebaseFirestore db;
    private Button btnBackEdit;
    private int birthYear, birthMonth, birthDay;
    private String userId; // Store the user's UID here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        initialize();
        setUserData();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
        avtUserUP.setOnClickListener(v -> openImageGallery());
        btnBackEdit.setOnClickListener(v -> finish());

        // Chọn ngày bằng picker
        ageUP.setFocusable(false);
        ageUP.setOnClickListener(v -> showDatePickerDialog());
    }

    public void initialize() {
        fullname = findViewById(R.id.fullname);
        ageUP = findViewById(R.id.ageUP);
        progressUpdateUP = findViewById(R.id.progressUpdateUP);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        avtUserUP = findViewById(R.id.avtUserUP);
        btnBackEdit = findViewById(R.id.btnBackEdit);
        email = findViewById(R.id.emailUP);

        db = FirebaseFirestore.getInstance();
    }

    public void setUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            User temp = (User) intent.getSerializableExtra("user");
            userId = intent.getStringExtra("uid"); // Get the UID from the intent

            if (temp != null) {
                fullname.setText(temp.getFullName());
                ageUP.setText(temp.getBirthday());
                email.setText(temp.getEmail());

                if (temp.getAvatar() != null) {
                    currentAvt = temp.getAvatar();
                    loadImageAsync(temp.getAvatar());
                } else {
                    avtUserUP.setImageResource(R.drawable.bg_main_cat);
                }
            }
        }
    }

    private void loadImageAsync(String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.bg_main_cat)
                .error(R.drawable.bg_main_cat)
                .override(200, 200)
                .centerCrop();

        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        avtUserUP.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        avtUserUP.setImageDrawable(placeholder);
                    }
                });
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                updateImage(selectedImageUri.toString());
            }
        }
    }

    private void updateImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            avtUserUP.setImageResource(R.drawable.bg_main_cat);
        } else {
            Glide.with(this).load(imageUrl).into(avtUserUP);
        }
    }

    private void saveProfile() {
        progressUpdateUP.setVisibility(View.VISIBLE);
        btnSaveProfile.setVisibility(View.GONE);

        String newFullName = fullname.getText().toString().trim();
        String newAge = ageUP.getText().toString().trim();
        String email = this.email.getText().toString().trim();

        // If there's a new image, update the avatar
        String currentAvatar = selectedImageUri == null ? currentAvt : selectedImageUri.toString();

        if (selectedImageUri != null) {
            uploadAndSaveImage(email, newFullName, newAge, currentAvatar);
        } else {
            updateFirestore(email, newFullName, newAge, currentAvatar);
        }
    }

    private void updateFirestore(String email, String newFullName, String newAge, String imageUrl) {
        User updatedUser = new User(email, newFullName, newAge, imageUrl);
        db.collection("users")
                .document(userId)  // Use the actual user's UID here
                .set(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    progressUpdateUP.setVisibility(View.GONE);
                    ToastUtils.showShortToast(EditProfile.this, "Cập nhật hồ sơ thành công");
                    finish();  // Go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    progressUpdateUP.setVisibility(View.GONE);
                    btnSaveProfile.setVisibility(View.VISIBLE);
                    ToastUtils.showShortToast(EditProfile.this, "Cập nhật hồ sơ thất bại");
                });
    }

    private void uploadAndSaveImage(String email, String newFullName, String newAge, String userId) {
        progressUpdateUP.setVisibility(View.VISIBLE);

        ConfigCloudinary.initCloudinary(this);
        ConfigCloudinary.uploadImage(this, selectedImageUri, new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                ToastUtils.showShortToast(EditProfile.this, "Đang tải ảnh lên...");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {}

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String uploadedImageUrl = resultData.get("secure_url").toString();
                updateFirestore(email, newFullName, newAge, uploadedImageUrl);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                progressUpdateUP.setVisibility(View.GONE);
                btnSaveProfile.setVisibility(View.VISIBLE);
                Toast.makeText(EditProfile.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {}
        });
    }

    // Method for DatePicker
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        birthYear = calendar.get(Calendar.YEAR);
        birthMonth = calendar.get(Calendar.MONTH);
        birthDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile.this, (view, year, month, dayOfMonth) -> {
            ageUP.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, birthYear, birthMonth, birthDay);

        datePickerDialog.show();
    }
}
