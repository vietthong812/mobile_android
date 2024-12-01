package tdtu.EStudy_App.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.WordAddAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;

public class AddTopic extends AppCompatActivity {
    AppCompatButton btnSave,btnCancel;
    FirebaseFirestore db;
    FirebaseUser user;
    EditText editTopicName;
    CardView themTuMoi;
    RecyclerView recyclerViewThemTu;
    ArrayList<Word> wordList;
    WordAddAdapter wordAddAdapter;
    CheckBox checkBox;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.them_tu_vao_topic);
        init();
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        themTuMoi.setOnClickListener(v -> {
            Word word = new Word();
            wordList.add(word);
            wordAddAdapter.notifyItemInserted(wordList.size() - 1);
        });
        btnSave.setOnClickListener(v -> {
            String topicName = editTopicName.getText().toString();
            if (topicName.isEmpty()) {
                editTopicName.setError("Tên chủ đề không được để trống");
                return;
            }
            db.collection("topics").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    if (Objects.equals(queryDocumentSnapshots.getDocuments().get(i).getString("name"), topicName)) {
                        editTopicName.setError("Tên chủ đề đã tồn tại");
                        return;
                    }
                }


                String userId = user.getUid();
                String name = editTopicName.getText().toString();
                long numWord=wordAddAdapter.getItemCount();

                //Kiểm tra để add trạng thái topic
                String status = "private";
                if (checkBox.isChecked()) {
                    status = "public";
                }
                Timestamp timestamp = Timestamp.now();
                DocumentReference reference=db.collection("users").document(userId);
                Map<String, Object> topic = new HashMap<>();
                topic.put("name", name);
                topic.put("numWord", numWord);
                topic.put("status", status);
                topic.put("userCreate", reference);
                topic.put("createTime", timestamp);
                db.collection("topics").add(topic).addOnSuccessListener(documentReference -> {
                    for (int i = 0; i < wordList.size(); i++) {
                        Word word = wordAddAdapter.getWordList().get(i);
                        Map<String, Object> wordMap = new HashMap<>();
                        wordMap.put("marked", false);
                        wordMap.put("name", word.getName());
                        if (word.getMeaning().isEmpty()||word.getMeaning()==null) {
                            wordMap.put("meaning", "Chưa có nghĩa");
                        } else{
                            wordMap.put("meaning", word.getMeaning());
                        }
                        wordMap.put("meaning", word.getMeaning());
                        wordMap.put("pronunciation", "");
                        wordMap.put("state","chưa được học");
                        documentReference.collection("words").add(wordMap);
                    }
                });
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                ToastUtils.showShortToast(this, "Thêm chủ đề thành công");
                finish();
            });
        });
    }
    public void init() {
        // Initialize View
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        editTopicName = findViewById(R.id.editTopicName);
        themTuMoi = findViewById(R.id.themTuMoi);
        checkBox = findViewById(R.id.checkShareTopic);
        // Initialize RecyclerView
        recyclerViewThemTu = findViewById(R.id.recyclerViewThemTu);
        recyclerViewThemTu.setLayoutManager(new LinearLayoutManager(this));
        wordList = new ArrayList<>();
        wordAddAdapter = new WordAddAdapter(this, wordList);
        recyclerViewThemTu.setAdapter(wordAddAdapter);

        // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }
}