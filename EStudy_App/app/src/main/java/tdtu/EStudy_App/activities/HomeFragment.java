package tdtu.EStudy_App.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TopicHomeAdapter;
import tdtu.EStudy_App.models.Topic;


public class HomeFragment extends Fragment {

    private TextView tenUserHome;
    private ImageView avtUserHome;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressHome;
    private List<Topic> topicListHome;
    private TopicHomeAdapter topicHomeAdapter;
    private RecyclerView recyclerViewTatCaCacTopic;
    private CardView cardThuVien, cardBrowse, cardUser;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    @Override
    public void onResume() {
        super.onResume();
        setUserData();  // Reload user data when fragment resumes
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        setOnClickListeners();
        topicListHome = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        recyclerViewTatCaCacTopic = view.findViewById(R.id.recyclerViewCacTopicTiepTucHoc);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTatCaCacTopic.setLayoutManager(horizontalLayoutManager);

        loadUserTopics(); // Load topics for the user

        return view;
    }

    private void loadUserTopics() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.e("HomeFragment", "User ID: " + userId);

            executorService.execute(() -> {
                try {
                    List<Topic> loadedTopics = new ArrayList<>();

                    db.collection("topics")
                            .whereEqualTo("userCreate", db.collection("users").document(userId))
                            .limit(5) //Giới hạn hiển thị 5 topic
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String id = document.getId();
                                        String name = document.getString("name");
                                        Timestamp createTime = document.getTimestamp("createTime");
                                        int numWord = document.getLong("numWord").intValue();
                                        String status = document.getString("status");

                                        // Thêm Topic vào danh sách tạm
                                        Topic topic = new Topic(id, name, status, userId, createTime, numWord);
                                        loadedTopics.add(topic);
                                    }

                                    // Chuyển kết quả lên UI Thread
                                    mainThreadHandler.post(() -> {
                                        topicListHome.clear();
                                        topicListHome.addAll(loadedTopics);

                                        if (topicHomeAdapter == null) {
                                            topicHomeAdapter = new TopicHomeAdapter(getContext(), topicListHome);
                                            recyclerViewTatCaCacTopic.setAdapter(topicHomeAdapter);
                                        } else {
                                            topicHomeAdapter.notifyDataSetChanged();
                                        }
                                    });
                                } else {
                                    mainThreadHandler.post(() -> {
                                        Toast.makeText(getContext(), "Error getting topics: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("HomeFragment", "Error getting topics: ", e);
                                mainThreadHandler.post(() -> {
                                    Toast.makeText(getContext(), "Failed to load topics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            });
                } catch (Exception e) {
                    mainThreadHandler.post(() -> {
                        Toast.makeText(getContext(), "Unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }



    private void initializeViews(View view) {
        tenUserHome = view.findViewById(R.id.tenUserHome);
        avtUserHome = view.findViewById(R.id.avtUserHome);
        progressHome = view.findViewById(R.id.progressHome);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cardThuVien = view.findViewById(R.id.cardThuVien);
        cardBrowse = view.findViewById(R.id.cardBrowse);
        cardUser = view.findViewById(R.id.cardUser);
    }

    private void setOnClickListeners() {
        cardThuVien.setOnClickListener(v -> navigateToFragment(new TopicFragment(), R.id.topic));
        cardBrowse.setOnClickListener(v -> navigateToFragment(new BrowseFragment(), R.id.browse));
        cardUser.setOnClickListener(v -> navigateToFragment(new UserFragment(), R.id.user));

    }

    private void navigateToFragment(Fragment fragment, int navId) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Highlight the bottom navigation item
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(navId);
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
                            } else {
                                avtUserHome.setImageResource(R.drawable.bg_main_cat); // Default image
                            }

                            tenUserHome.setText(fullName != null ? fullName : "Chưa có tên đầy đủ");
                            progressHome.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                            progressHome.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
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
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        avtUserHome.setImageDrawable(placeholder);
                    }
                });
    }
}