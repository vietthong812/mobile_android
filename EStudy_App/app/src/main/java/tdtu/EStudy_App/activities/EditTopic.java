package tdtu.EStudy_App.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.OnWordDeleteClickListener;
import tdtu.EStudy_App.adapters.WordAddAdapter;
import tdtu.EStudy_App.adapters.WordListAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;
import tdtu.EStudy_App.viewmodels.TopicViewModel;

public class EditTopic extends AppCompatActivity implements OnWordDeleteClickListener {
    AppCompatButton btnSave,btnCancel;
    FirebaseFirestore db;
    FirebaseUser user;
    EditText editTopicName;
    CardView themTuMoi,themTuFile;
    RecyclerView recyclerViewThemTu;
    List<Word> wordList;
    WordAddAdapter wordAddAdapter;
    QuizViewModel quizViewModel;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_SELECT_CSV = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_topic);
        init();
        Intent intent = getIntent();
        String id = intent.getStringExtra("topicID");
        db.collection("topics").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                editTopicName.setText(task.getResult().getString("name"));
            }
        });
        quizViewModel.loadWordList(id, new QuizViewModel.WordListCallback() {
            public void onWordListLoaded(List<Word> words) {
                wordList = words;
                wordAddAdapter = new WordAddAdapter(EditTopic.this, (ArrayList<Word>) wordList, EditTopic.this);
                recyclerViewThemTu.setAdapter(wordAddAdapter);
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showShortToast(EditTopic.this, "Không thể tải danh sách từ vựng");
            }
        });
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        themTuMoi.setOnClickListener(v -> {
            Word word = new Word();
            wordList.add(word);
            wordAddAdapter.notifyItemInserted(wordList.size() - 1);
            recyclerViewThemTu.scrollToPosition(wordList.size() - 1);
        });
        btnSave.setOnClickListener(v -> {
            String topicName = editTopicName.getText().toString();
            if (topicName.isEmpty()) {
                editTopicName.setError("Tên chủ đề không được để trống");
                return;
            }
            db.collection("topics").get().addOnSuccessListener(queryDocumentSnapshots -> {
                String name = editTopicName.getText().toString();
                long numWord=wordAddAdapter.getItemCount();
                Map<String, Object> topic = new HashMap<>();
                topic.put("name", name);
                topic.put("numWord", numWord);
                db.collection("topics").document(id).update(topic).addOnSuccessListener(aVoid -> {
                    for (int i = 0; i < wordList.size(); i++) {
                        Word word = wordList.get(i);
                        Map<String, Object> wordMap = new HashMap<>();
                        wordMap.put("name", word.getName() != null ? word.getName() : "");
                        wordMap.put("meaning", word.getMeaning() != null ? word.getMeaning() : "Chưa có nghĩa");
                        wordMap.put("pronunciation", word.getPronunciation() != null ? word.getPronunciation() : "");
                        wordMap.put("state", word.getState() != null ? word.getState() : "Chưa được học");
                        wordMap.put("marked", word.isMarked());
                        if (word.getId() != null && !word.getId().isEmpty()) {
                            // Update existing word
                            db.collection("topics").document(id).collection("words").document(word.getId()).update(wordMap);
                        } else {
                            // Add new word
                            db.collection("topics").document(id).collection("words").add(wordMap).addOnSuccessListener(documentReference -> {
                                word.setId(documentReference.getId());
                            });
                        }
                    }
                });
                ToastUtils.showShortToast(this, "Chỉnh sửa topic thành công");
                finish();
            });
        });
        themTuFile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            } else {
                selectCSVFile();
            }
        });
    }
    public void init() {
        // Initialize View
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        editTopicName = findViewById(R.id.editTopicName);
        themTuMoi = findViewById(R.id.themTuMoi);
        themTuFile = findViewById(R.id.themTuFile);
        // Initialize RecyclerView
        recyclerViewThemTu = findViewById(R.id.recyclerViewThemTu);
        recyclerViewThemTu.setLayoutManager(new LinearLayoutManager(this));
        wordList = new ArrayList<>();
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }
    @Override
    public void onWordDeleteClick(Word word) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá từ vựng")
                .setMessage("Bạn có chắc chắn muốn xoá từ vựng này không?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    Intent intent = getIntent();
                    String id = intent.getStringExtra("topicID");
                    wordList.remove(word);
                    wordAddAdapter.notifyItemRemoved(wordList.indexOf(word));
                    wordAddAdapter.notifyItemRangeChanged(wordList.indexOf(word), wordList.size() - wordList.indexOf(word));
                    if (word.getId() != null && !word.getId().isEmpty()) {
                        db.collection("topics").document(id).collection("words").document(word.getId()).delete();
                    }
                })
                .setNegativeButton("Thoát", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    private void selectCSVFile() {
        Intent intentAddFromCSV = new Intent(Intent.ACTION_GET_CONTENT);
        intentAddFromCSV.setType("text/csv");
        startActivityForResult(intentAddFromCSV, REQUEST_CODE_SELECT_CSV);
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
                    continue; // Bỏ qua dòng đầu tiên [Word],[Meaning]
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