package tdtu.EStudy_App.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.User;

public class UserFragment extends Fragment {

    private Button btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private TextView tvTenDayDu, tvEmail, tvBirth;
    private ImageView avtNguoiDung;
    private ProgressBar progressLoadAnh;
    User user;
    private androidx.cardview.widget.CardView cardAchive, cardEditProfile, cardEditPassword;


    @Override
    public void onResume() {
        super.onResume();
        setUserData();  // Tải lại dữ liệu khi fragment trở lại
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        initialize(view);
        setUserData();
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        cardEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            intent.putExtra("user", user);
            intent.putExtra("uid", mAuth.getCurrentUser().getUid());
            startActivity(intent);
        });


        return view;
    }

    private void initialize(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        tvTenDayDu = view.findViewById(R.id.tvTenDayDu);
        avtNguoiDung = view.findViewById(R.id.avtNguoiDung);
        cardAchive = view.findViewById(R.id.cardAchive);
        cardEditProfile = view.findViewById(R.id.cardEditProfile);
        cardEditPassword = view.findViewById(R.id.cardEditPassword);
        progressLoadAnh = view.findViewById(R.id.progressLoadAnh);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBirth = view.findViewById(R.id.tvBirth);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    logoutUser();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void setUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            progressLoadAnh.setVisibility(View.VISIBLE);
                            String fullName = documentSnapshot.getString("fullName");
                            String imageUrl = documentSnapshot.getString("avatar");
                            String email = documentSnapshot.getString("email");
                            String birth = documentSnapshot.getString("birthday");

                            user = new User( email, fullName, birth, imageUrl);

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                loadImageAsync(imageUrl);
                                progressLoadAnh.setVisibility(View.GONE);
                            } else {
                                avtNguoiDung.setImageResource(R.drawable.bg_main_cat); // Default image
                                progressLoadAnh.setVisibility(View.GONE);
                            }

                            tvBirth.setText(birth != null ? "Birthday: " + birth : "Chưa có ngày sinh");
                            tvEmail.setText(email != null ? "Email: " +email : "Chưa có email");
                            tvTenDayDu.setText(fullName != null ? fullName : "Chưa có tên đầy đủ");
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            progressLoadAnh.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageAsync(String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.bg_main_cat)
                .error(R.drawable.bg_main_cat)
                .override(200, 200)
                .centerCrop();

        Glide.with(requireContext())
                .load(imageUrl)
                .apply(requestOptions)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        avtNguoiDung.setImageDrawable(resource);
                        progressLoadAnh.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        avtNguoiDung.setImageDrawable(placeholder);
                        progressLoadAnh.setVisibility(View.GONE);
                    }
                });
    }


    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Sign out from FirebaseAuth
        mAuth.signOut();

        // Redirect to the SignIn activity
        Intent intent = new Intent(getActivity(), SignIn.class);
        startActivity(intent);

        // Finish the current activity
        requireActivity().finish();
    }
}
