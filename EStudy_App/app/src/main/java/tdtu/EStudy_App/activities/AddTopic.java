package tdtu.EStudy_App.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tdtu.EStudy_App.R;

import tdtu.EStudy_App.adapters.OnWordDeleteClickListener;
import tdtu.EStudy_App.adapters.WordAddAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;

public class AddTopic extends AppCompatActivity implements OnWordDeleteClickListener {
    AppCompatButton btnSave,btnCancel;
    FirebaseFirestore db;
    FirebaseUser user;
    EditText editTopicName;
    CardView themTuMoi,themTuFile;
    RecyclerView recyclerViewThemTu;
    ArrayList<Word> wordList;
    WordAddAdapter wordAddAdapter;
    CheckBox checkBox;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_SELECT_CSV = 2;


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
            recyclerViewThemTu.scrollToPosition(wordList.size() - 1);
        });
        themTuFile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            } else {
                selectCSVFile();
            }
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
        themTuFile = findViewById(R.id.themTuFile);

        // Initialize RecyclerView
        recyclerViewThemTu = findViewById(R.id.recyclerViewThemTu);
        recyclerViewThemTu.setLayoutManager(new LinearLayoutManager(this));
        wordList = new ArrayList<>();
        wordAddAdapter = new WordAddAdapter(this, wordList,this );
        recyclerViewThemTu.setAdapter(wordAddAdapter);

        // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }
    public void onWordDeleteClick(Word word) {
        wordList.remove(word);
        wordAddAdapter.notifyItemRemoved(wordList.indexOf(word));
        wordAddAdapter.notifyItemRangeChanged(wordList.indexOf(word), wordList.size()-wordList.indexOf(word));
    }
    private void selectCSVFile() {
        Intent intentAddFromCSV = new Intent(Intent.ACTION_GET_CONTENT);
        intentAddFromCSV.setType("*/*");
        intentAddFromCSV.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentAddFromCSV, "Select a file"), REQUEST_CODE_SELECT_CSV);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectCSVFile();
            } else {
                ToastUtils.showShortToast(this, "Chưa cấp quyền đọc dữ liệu b nhớ ngoài");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_CSV && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    List<Word> wordsFromFile = readWordsFromCSV(uri);
                    wordList.addAll(wordsFromFile);
                    wordAddAdapter.notifyDataSetChanged();
                    ToastUtils.showShortToast(this, "Đã thêm từ từ file CSV");
                } catch (IOException e) {
                    ToastUtils.showShortToast(this, "Lỗi đọc file CSV");
                }
            }
        }
    }

    private List<Word> readWordsFromCSV(Uri uri) throws IOException {
        List<Word> words = new ArrayList<>();

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
                String[] tokens = line.split(",");

                if (tokens.length >= 2) {
                    Word word = new Word();
                    word.setName(tokens[0].trim());
                    word.setMeaning(tokens[1].trim());
                    words.add(word);
                }
            }
        }

        return words;
    }


}