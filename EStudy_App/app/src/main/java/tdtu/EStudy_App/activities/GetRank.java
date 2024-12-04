package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.FirebaseUserUtils;

public class GetRank extends AppCompatActivity {

    private AppCompatButton btnCancleRank;
    private Intent intent;
    private String topicId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseUserUtils.getInstance();
    Map<String, Long> finishTimes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ranking);

        btnCancleRank = findViewById(R.id.btnCancelRank);
        btnCancleRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        init();
        getRank();
        displayCurrentUser();
    }

    private void init(){
        intent = new Intent();
        topicId = getIntent().getStringExtra("topicID");
    }

    private void getRank() {
        db.collection("topics").document(topicId).collection("progress")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Long finishTime = document.getLong("FinishTime");
                        if (finishTime != null && finishTime > 0) {
                            finishTimes.put(document.getId(), finishTime);
                        }
                    }

                    // Sort the map by finish time
                    List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(finishTimes.entrySet());
                    sortedEntries.sort(Map.Entry.comparingByValue());

                    // Get top 3 userIds with the shortest finish times
                    List<String> topUserIds = sortedEntries.stream()
                            .limit(3)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    // Get full names and finish times of top 3 users
                    getTopUserNamesAndTimes(topUserIds, finishTimes);
                })
                .addOnFailureListener(e -> Log.w("getRank", "Error getting documents", e));
    }

    private void getTopUserNamesAndTimes(List<String> userIds, Map<String, Long> finishTimes) {
        List<String> topUserNames = new ArrayList<>();
        List<Long> topFinishTimes = new ArrayList<>();
        for (String userId : userIds) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String fullName = documentSnapshot.getString("fullName");
                        if (fullName != null) {
                            topUserNames.add(fullName);
                            topFinishTimes.add(finishTimes.get(userId));
                        }
                        if (topUserNames.size() == userIds.size()) {
                            displayRanking(topUserNames, topFinishTimes);
                        }
                    })
                    .addOnFailureListener(e -> Log.w("getTopUserNamesAndTimes", "Error getting user document", e));
        }
    }

    private void displayRanking(List<String> topUserNames, List<Long> topFinishTimes) {
        // Assuming you have TextViews with ids: rank1, rank2, rank3 in ranking.xml
        TextView rank1 = findViewById(R.id.name1st);
        TextView rank2 = findViewById(R.id.name2nd);
        TextView rank3 = findViewById(R.id.name3rd);

        TextView finishTimeRank1 = findViewById(R.id.finishTime1st);
        TextView finishTimeRank2 = findViewById(R.id.finishTime2nd);
        TextView finishTimeRank3 = findViewById(R.id.finishTime3rd);

        if (topUserNames.size() > 0) {
            rank1.setText(topUserNames.get(0));
            long minutes = (topFinishTimes.get(0) / 1000) / 60;
            long seconds = (topFinishTimes.get(0) / 1000) % 60;
            String formattedTime = String.format("%d:%02d", minutes, seconds);
            finishTimeRank1.setText(formattedTime);
        }
        if (topUserNames.size() > 1) {
            rank2.setText(topUserNames.get(1));
            long minutes = (topFinishTimes.get(1) / 1000) / 60;
            long seconds = (topFinishTimes.get(1) / 1000) % 60;
            String formattedTime = String.format("%d:%02d", minutes, seconds);
            finishTimeRank2.setText(formattedTime);
        }
        if (topUserNames.size() > 2) {
            rank3.setText(topUserNames.get(2));
            long minutes = (topFinishTimes.get(2) / 1000) / 60;
            long seconds = (topFinishTimes.get(2) / 1000) % 60;
            String formattedTime = String.format("%d:%02d", minutes, seconds);
            finishTimeRank3.setText(formattedTime);
        }
    }

    private void displayCurrentUser(){
        // Display current user's finish time
        String currentUserId = user.getUid();
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String currentUserName = documentSnapshot.getString("fullName");
                    Long currentUserFinishTime = finishTimes.get(currentUserId);

                    TextView currentUserTextView = findViewById(R.id.currentUser);
                    TextView finishTimeCurrentUser = findViewById(R.id.finishTimeCurrentUser);

                    if (currentUserName != null) {
                        currentUserTextView.setText(currentUserName);
                    }
                    if (currentUserFinishTime != null) {
                        long minutes = (currentUserFinishTime / 1000) / 60;
                        long seconds = (currentUserFinishTime / 1000) % 60;
                        String formattedTime = String.format("%d:%02d", minutes, seconds);
                        finishTimeCurrentUser.setText(formattedTime);
                    }
                })
                .addOnFailureListener(e -> Log.w("displayRanking", "Error getting current user document", e));

    }
}
