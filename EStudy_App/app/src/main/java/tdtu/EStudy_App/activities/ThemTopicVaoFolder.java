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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TopicSelectionAdapter;
import tdtu.EStudy_App.models.Topic;

public class ThemTopicVaoFolder extends AppCompatActivity {

    AppCompatButton btnCancleThemTopicFolder, btnDoneThemTopicFolder;
    RecyclerView recyclerViewChonTopic;
    FirebaseFirestore db;
    List<Topic> topicList, selectedTopicList;

    TopicSelectionAdapter topicSelectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.them_topic_vao_folder);

        init();
        btnCancleThemTopicFolder.setOnClickListener(view -> finish());






    }

    protected void init(){
        btnCancleThemTopicFolder = findViewById(R.id.btnCancelThemTopicFolder);
        btnDoneThemTopicFolder = findViewById(R.id.btnDoneThemTopicFolder);
        recyclerViewChonTopic = findViewById(R.id.recyclerViewChonTopic);

        db = FirebaseFirestore.getInstance();
        topicList = new ArrayList<>();

        topicSelectionAdapter = new TopicSelectionAdapter(ThemTopicVaoFolder.this, topicList);
        recyclerViewChonTopic.setAdapter(topicSelectionAdapter);


    }

    protected void getTopicList(){
        //Truy cấn lấy danh sách Topic từ Firebase và cập nhật vào topicList
    }

    protected void saveTopicList(ArrayList<Topic> topicList){
        //Cập nhật danh sách Topic vào adapter
    }
}