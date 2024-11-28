package tdtu.EStudy_App.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.User;

public class HomeFragment extends Fragment {

    private boolean isFront = true;
    private CardView currentCard;
    private TextView tvMatTruoc, tvMatSau;
    private ImageButton btnSound, btnSave;
    private CardView cardThuVien, cardUser , cardBrowse;
    private TextView tenUserHome;
    private ImageView avtUserHome;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressHome;

    @Override
    public void onResume() {
        super.onResume();
        setUserData();  // Tải lại dữ liệu khi fragment trở lại
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        setOnClickListeners();
        return view;
    }

    private void initializeViews(View view) {
        currentCard = view.findViewById(R.id.currentCard);
        tvMatTruoc = view.findViewById(R.id.tvMatTruoc);
        tvMatSau = view.findViewById(R.id.tvMatSau);
        btnSound = view.findViewById(R.id.btnSound);
        btnSave = view.findViewById(R.id.btnSave);
        cardThuVien = view.findViewById(R.id.cardThuVien);
        cardUser  = view.findViewById(R.id.CardUser );
        cardBrowse = view.findViewById(R.id.cardBrowse);
        tenUserHome = view.findViewById(R.id.tenUserHome);
        avtUserHome = view.findViewById(R.id.avtUserHome);
        progressHome = view.findViewById(R.id.progressHome);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setOnClickListeners() {
        currentCard.setOnClickListener(view -> flipCard(currentCard, tvMatTruoc, tvMatSau));

        cardThuVien.setOnClickListener(view -> {
            highlightBottomNavItem(R.id.topic);
            replaceFragment(new BrowseFragment());
        });

        cardUser.setOnClickListener(view -> {
            highlightBottomNavItem(R.id.user);
            replaceFragment(new UserFragment());
        });

        cardBrowse.setOnClickListener(view -> {
            highlightBottomNavItem(R.id.browse);
            replaceFragment(new TopicFragment());
        });
    }

    private void highlightBottomNavItem(int itemId) {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(itemId);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void flipCard(CardView cardView, TextView textFront, TextView textBack) {
        cardView.setCameraDistance(getResources().getDisplayMetrics().density * 15000);
        cardView.setPivotX((float) cardView.getWidth() / 2);
        cardView.setPivotY((float) cardView.getHeight() / 2);

        if (isFront) {
            cardView.animate()
                    .rotationY(90)
                    .setDuration(100)
                    .withEndAction(() -> {
                        textFront.setVisibility(View.GONE);
                        btnSound.setVisibility(View.INVISIBLE);
                        textBack.setVisibility(View.VISIBLE);
                        cardView.setRotationY(-90); // Reset for the next half
                        cardView.animate()
                                .rotationY(0)
                                .setDuration(100)
                                .start();
                    })
                    .start();
        } else {
            cardView.animate()
                    .rotationY(-90)
                    .setDuration(100)
                    .withEndAction(() -> {
                        textBack.setVisibility(View.GONE);
                        textFront.setVisibility(View.VISIBLE);                        btnSound.setVisibility(View.VISIBLE);
                        cardView.setRotationY(90); // Reset for the next half
                        cardView.animate()
                                .rotationY(0)
                                .setDuration(100)
                                .start();
                    })
                    .start();
        }

        isFront = !isFront; // Toggle state
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
                            progressHome.setVisibility(View.VISIBLE);
                            String fullName = documentSnapshot.getString("fullName");
                            String imageUrl = documentSnapshot.getString("avatar");


                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                loadImageAsync(imageUrl);
                                progressHome.setVisibility(View.GONE);
                            } else {
                                avtUserHome.setImageResource(R.drawable.bg_main_cat); // Default image
                                progressHome.setVisibility(View.GONE);
                            }


                            tenUserHome.setText(fullName != null ? fullName : "Chưa có tên đầy đủ");
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            progressHome.setVisibility(View.GONE);
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
                        avtUserHome.setImageDrawable(resource);
                        progressHome.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        avtUserHome.setImageDrawable(placeholder);
                        progressHome.setVisibility(View.GONE);
                    }
                });
    }
}