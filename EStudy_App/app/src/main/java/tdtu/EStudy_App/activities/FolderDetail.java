package tdtu.EStudy_App.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.OnTopicClickListener;
import tdtu.EStudy_App.adapters.OnTopicDeleteClickListener;
import tdtu.EStudy_App.adapters.Topic_folderAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.TopicViewModel;

public class FolderDetail extends AppCompatActivity implements OnTopicDeleteClickListener {
    private static final int REQUEST_CODE = 1; // Define REQUEST_CODE as a constant
    Button btnDeleteFolder;
    TextView nameFolder;
    RecyclerView recyclerViewTatCaCacTopic;
    Topic_folderAdapter topic_folderAdapter;
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

        init();

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
            topic_folderAdapter.updateTopics(topics);
        });
        btnDeleteFolder.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc chắn muốn xóa thư mục này không?");
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                db.collection("folders").document(folderId).delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastUtils.showShortToast(this, "Xóa thư mục thành công");
                        finish();
                    }
                });
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
    private void init(){
        // Initialize views
        nameFolder = findViewById(R.id.tenFolder);
        btnDeleteFolder = findViewById(R.id.btnDeleteFolder);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> finish());

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Set up the RecyclerView
        recyclerViewTatCaCacTopic = findViewById(R.id.recyclerViewTatCaCacTopic);
        recyclerViewTatCaCacTopic.setLayoutManager(new LinearLayoutManager(this));
        topic_folderAdapter = new Topic_folderAdapter(this, new ArrayList<>(), this);
        recyclerViewTatCaCacTopic.setAdapter(topic_folderAdapter);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
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
    public void onDeleteTopicClick(Topic topic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa topic khỏi thư mục này không?");
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            db.collection("folders").document(folderId).update("topics", FieldValue.arrayRemove(db.document("topics/" + topic.getId())))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ToastUtils.showShortToast(this, "Xóa chủ đề thành công");
                            topic_folderAdapter.removeTopicById(topic.getId());
                        }
                    });
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String topicId = data.getStringExtra("topicID");
            if (topicId != null) {
                topic_folderAdapter.removeTopicById(topicId);
            }
        }
    }
}