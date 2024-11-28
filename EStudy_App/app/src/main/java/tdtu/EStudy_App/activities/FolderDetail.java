package tdtu.EStudy_App.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.OnTopicClickListener;
import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.viewmodels.TopicViewModel;

public class FolderDetail extends AppCompatActivity implements OnTopicClickListener {
    private static final int REQUEST_CODE = 1; // Define REQUEST_CODE as a constant

    TextView nameFolder;
    RecyclerView recyclerViewTatCaCacTopic;
    TopicListAdapter topicListAdapter;
    TopicViewModel topicViewModel;
    AppCompatButton btnCancel;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String folderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.folder_detail);
        nameFolder = findViewById(R.id.tenFolder);
        db = FirebaseFirestore.getInstance();
        recyclerViewTatCaCacTopic = findViewById(R.id.recyclerViewTatCaCacTopic);
        recyclerViewTatCaCacTopic.setLayoutManager(new LinearLayoutManager(this));
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> finish());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        topicListAdapter = new TopicListAdapter(this, new ArrayList<>(), this);
        recyclerViewTatCaCacTopic.setAdapter(topicListAdapter);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        Intent intent = getIntent();
        folderId = intent.getStringExtra("folderId");
        db.collection("folders").document(folderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String name = document.getString("name");
                nameFolder.setText(getString(R.string.tenfolder, name));
            }
        });
        topicViewModel.loadTopicsByFolderId(folderId);
        topicViewModel.getTopics().observe(this, topics -> {
            topicListAdapter.updateTopics(topics);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        topicViewModel.loadTopicsByFolderId(folderId);
    }

    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(this, TopicDetail.class);
        intent.putExtra("topicID", topic.getId());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String topicId = data.getStringExtra("topicID");
            if (topicId != null) {
                topicListAdapter.removeTopicById(topicId);
            }
        }
    }
}