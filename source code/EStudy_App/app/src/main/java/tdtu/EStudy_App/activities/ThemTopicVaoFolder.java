package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TopicSelectionAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.viewmodels.TopicViewModel;

public class ThemTopicVaoFolder extends AppCompatActivity {

    AppCompatButton btnCancleThemTopicFolder, btnDoneThemTopicFolder;
    RecyclerView recyclerViewChonTopic;
    FirebaseFirestore db;
    TopicViewModel topicViewModel;
    TopicSelectionAdapter topicSelectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.them_topic_vao_folder);

        init();
        btnCancleThemTopicFolder.setOnClickListener(view -> finish());
        Intent intent = getIntent();
        String folderId = intent.getStringExtra("folderId");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        topicViewModel.loadTopicForAddFolder(userId, folderId);
        topicViewModel.getTopics().observe(this, topics -> {
            topicSelectionAdapter.updateTopics(topics);
        });
        btnDoneThemTopicFolder.setOnClickListener(view -> {
            // Save selectedTopicList to Firebase
            List<Topic> selectedTopics = new ArrayList<>();
            List<Topic> topicList = topicSelectionAdapter.getTopicList();
            for (Topic topic : topicList) {
                if (topic.isSelectedForFolder()) {
                    selectedTopics.add(topic);
                }
            }
            saveSelectedTopicsToFolder(folderId, selectedTopics);
            finish();
        });
    }
    private void saveSelectedTopicsToFolder(String folderId, List<Topic> selectedTopics) {
        DocumentReference folderRef = db.collection("folders").document(folderId);
        for (Topic topic : selectedTopics) {
            folderRef.update("topics", FieldValue.arrayUnion(db.collection("topics").document(topic.getId())));
        }
    }
    protected void init(){
        btnCancleThemTopicFolder = findViewById(R.id.btnCancelThemTopicFolder);
        btnDoneThemTopicFolder = findViewById(R.id.btnDoneThemTopicFolder);
        recyclerViewChonTopic = findViewById(R.id.recyclerViewChonTopic);
        recyclerViewChonTopic.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        topicSelectionAdapter = new TopicSelectionAdapter(ThemTopicVaoFolder.this, new ArrayList<>());
        recyclerViewChonTopic.setAdapter(topicSelectionAdapter);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

    }

}