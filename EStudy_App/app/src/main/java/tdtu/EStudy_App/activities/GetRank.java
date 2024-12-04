package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.RankAdapter;

public class GetRank extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Long> finishTimes = new HashMap<>();
    private RecyclerView recyclerViewRank;
    private RankAdapter rankAdapter;
    private TextView tvMyRank, tvMyFinishTime, tvMyName;
    private ImageView avtCuaBan, badgeCuaBan;
    private AppCompatButton btnCancleRank;
    private String currentUserId;
    private int rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        init();
        getRank();
        btnCancleRank.setOnClickListener(v -> finish());
    }

    private void init() {
        tvMyRank = findViewById(R.id.tvMyRank);
        tvMyFinishTime = findViewById(R.id.finishTimeCurrentUser);
        tvMyName = findViewById(R.id.currentUser);
        avtCuaBan = findViewById(R.id.avtCuaBan);
        badgeCuaBan = findViewById(R.id.imageMyRank);
        recyclerViewRank = findViewById(R.id.viewRanking);
        recyclerViewRank.setLayoutManager(new LinearLayoutManager(this));
        btnCancleRank = findViewById(R.id.btnCancelRank);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void getRank() {
        String topicId = getIntent().getStringExtra("topicID");
        db.collection("topics").document(topicId).collection("progress")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Long finishTime = document.getLong("FinishTime");
                        if (finishTime != null && finishTime > 0) {
                            finishTimes.put(document.getId(), finishTime);
                        }
                    }

                    List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(finishTimes.entrySet());
                    sortedEntries.sort(Map.Entry.comparingByValue());
                    fetchUserDetails(sortedEntries);
                })
                .addOnFailureListener(e -> Log.w("getRank", "Error getting documents", e));
    }

    private void fetchUserDetails(List<Map.Entry<String, Long>> sortedEntries) {
        List<String> topUserNames = new ArrayList<>(sortedEntries.size());
        List<String> topFinishTimes = new ArrayList<>(sortedEntries.size());
        List<String> topAvatarUris = new ArrayList<>(sortedEntries.size());

        for (int i = 0; i < sortedEntries.size(); i++) {
            topUserNames.add(null);
            topFinishTimes.add(null);
            topAvatarUris.add(null);
        }

        int totalUsers = sortedEntries.size();
        final int[] completedFetches = {0};

        for (int i = 0; i < sortedEntries.size(); i++) {
            int index = i;
            String userId = sortedEntries.get(index).getKey();
            Long finishTime = sortedEntries.get(index).getValue();

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String fullName = documentSnapshot.getString("fullName");
                        String avatarUri = documentSnapshot.getString("avatar");

                        if (fullName != null && avatarUri != null && finishTime != null) {
                            topUserNames.set(index, fullName);
                            long minutes = (finishTime / 1000) / 60;
                            long seconds = (finishTime / 1000) % 60;
                            String formattedTime = String.format("%d:%02d", minutes, seconds);
                            topFinishTimes.set(index, formattedTime);
                            topAvatarUris.set(index, avatarUri);
                        }

                        completedFetches[0]++;
                        if (completedFetches[0] == totalUsers) {
                            updateAdapter(topUserNames, topFinishTimes, topAvatarUris);
                            setCurrentUserInfo(sortedEntries);
                        }
                    })
                    .addOnFailureListener(e -> Log.w("fetchUserDetails", "Error getting user document", e));
        }
    }


    private void updateAdapter(List<String> userNames, List<String> finishTimes, List<String> avatarUris) {
        rankAdapter = new RankAdapter(this, userNames, finishTimes, avatarUris);
        recyclerViewRank.setAdapter(rankAdapter);
    }
    private void setCurrentUserInfo(List<Map.Entry<String, Long>> sortedEntries) {
        rank = 1;
        boolean userFound = false;
        for (Map.Entry<String, Long> entry : sortedEntries) {
            if (entry.getKey().equals(currentUserId)) {
                userFound = true;
                db.collection("users").document(currentUserId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String fullName = documentSnapshot.getString("fullName");
                            String avatarUri = documentSnapshot.getString("avatar");
                            if (fullName != null && avatarUri != null) {
                                tvMyName.setText(fullName);
                                long finishTime = entry.getValue();
                                long minutes = (finishTime / 1000) / 60;
                                long seconds = (finishTime / 1000) % 60;
                                String formattedTime = String.format("%d:%02d", minutes, seconds);
                                tvMyFinishTime.setText(formattedTime);
                                tvMyRank.setText(String.valueOf(rank));
                                Glide.with(this).load(avatarUri).into(avtCuaBan);
                                if (rank == 1) {
                                    badgeCuaBan.setImageResource(R.drawable.badge1_icon);
                                    badgeCuaBan.setVisibility(View.VISIBLE);
                                    tvMyRank.setVisibility(View.GONE);
                                } else if (rank == 2) {
                                    badgeCuaBan.setImageResource(R.drawable.badge2);
                                    badgeCuaBan.setVisibility(View.VISIBLE);
                                    tvMyRank.setVisibility(View.GONE);
                                } else if (rank == 3) {
                                    badgeCuaBan.setImageResource(R.drawable.badge3);
                                    badgeCuaBan.setVisibility(View.VISIBLE);
                                    tvMyRank.setVisibility(View.GONE);
                                } else {
                                    badgeCuaBan.setVisibility(View.GONE);
                                    tvMyRank.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.w("setCurrentUserInfo", "Error getting user document", e));
                break;
            }
            rank++;
        }
        if (!userFound) {
            db.collection("users").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String fullName = documentSnapshot.getString("fullName");
                        String avatarUri = documentSnapshot.getString("avatar");
                        if (fullName != null && avatarUri != null) {
                            tvMyName.setText(fullName);
                            tvMyFinishTime.setText("--");
                            tvMyRank.setText("--");
                            Glide.with(this).load(avatarUri).into(avtCuaBan);
                            badgeCuaBan.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> Log.w("setCurrentUserInfo", "Error getting user document", e));
        }
    }
}