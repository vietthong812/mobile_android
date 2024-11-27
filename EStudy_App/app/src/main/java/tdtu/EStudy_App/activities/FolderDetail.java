package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.models.Topic;

public class FolderDetail extends AppCompatActivity {
    TextView nameFolder;
    RecyclerView recyclerViewTatCaCacTopic;
    List<Topic> topicList;
    TopicListAdapter topicListAdapter;
    FirebaseFirestore db;
    AppCompatButton btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.folder_detail);
        nameFolder = findViewById(R.id.tenFolder);
        topicList=new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        recyclerViewTatCaCacTopic = findViewById(R.id.recyclerViewTatCaCacTopic);
        recyclerViewTatCaCacTopic.setLayoutManager(new LinearLayoutManager(this));
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> finish());
        Intent intent = getIntent();
        String folderId = intent.getStringExtra("folderId");
        db.collection("folders").document(folderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference document = task.getResult().getReference();
                nameFolder.setText(task.getResult().getString("name"));
                ArrayList<DocumentReference> topicIds = (ArrayList<DocumentReference>) task.getResult().get("topics");
                if (topicIds != null) {
                    for (DocumentReference topicId : topicIds) {
                        topicId.get().addOnCompleteListener(topicTask -> {
                            if (topicTask.isSuccessful()) {
                                DocumentSnapshot topicDoc = topicTask.getResult();
                                String name = topicDoc.getString("name");
                                String status = topicDoc.getString("status");
                                String id = topicDoc.getId();
                                Timestamp createTime = topicDoc.getTimestamp("createTime");
                                int numWord = topicDoc.getLong("numWord").intValue();
                                DocumentReference userCreate = (DocumentReference) topicDoc.get("userCreate");
                                String userId = userCreate.getId();
                                Topic topic = new Topic(id, name, status, userId, createTime, numWord);
                                topicList.add(topic);
                                topicListAdapter = new TopicListAdapter(this, topicList);
                                recyclerViewTatCaCacTopic.setAdapter(topicListAdapter);
                            }
                        });
                    }
                }
            }
        });
    }
}