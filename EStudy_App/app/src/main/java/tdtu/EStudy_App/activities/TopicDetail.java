// TopicDetail.java
package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.OnWordMarkedListener;
import tdtu.EStudy_App.adapters.WordListAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;
import tdtu.EStudy_App.viewmodels.TopicViewModel;

public class TopicDetail extends AppCompatActivity  implements OnWordMarkedListener {
    TextView nameTopic, numWord, author, date, status;
    RecyclerView recyclerViewTatCaCacThe;
    List<Word> wordList;
    WordListAdapter wordListAdapter;
    FirebaseFirestore db;
    AppCompatButton btnCancel;
    Button btnEdit, btnDelete, btnLuuTopic;
    CardView cardFlashcard, cardTracNghiem, cardGoTu, cardRank, cardXuatFile;
    QuizViewModel quizViewModel;
    int count = 0;
    private String topicId;
    private String userId;
    private String statusTopic;
    TopicViewModel topicViewModel;


    @Override
    protected void onResume() {
        super.onResume();
        fetchWordList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.topic_detail);
        init();

        cardFlashcard.setOnClickListener(view -> {
            if (count < 1) {
                ToastUtils.showShortToast(TopicDetail.this, "Số lượng từ vựng trong topic phải lớn hơn hoặc bằng 1");
                return;
            }
            Intent intent = new Intent(TopicDetail.this, ChonOptionStudyFlashcard.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("type", "flashcard");
            intent.putExtra("topicName", nameTopic.getText().toString());
            intent.putParcelableArrayListExtra("wordList", new ArrayList<>(wordList));
            startActivity(intent);
        });

        cardTracNghiem.setOnClickListener(view -> {
            if (count < 4) {
                ToastUtils.showShortToast(TopicDetail.this, "Số lượng từ vựng trong topic phải lớn hơn hoặc bằng 4");
                return;
            }
            Intent intent = new Intent(TopicDetail.this, ChonOptionStudy.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("type", "tracnghiem");
            intent.putExtra("topicName", nameTopic.getText().toString());
            intent.putExtra("wordList", (ArrayList<Word>) wordList);
            startActivity(intent);
        });

        cardGoTu.setOnClickListener(view -> {
            if (count < 1) {
                ToastUtils.showShortToast(TopicDetail.this, "Số lượng từ vựng trong topic phải lớn hơn hoặc bằng 1");
                return;
            }
            Intent intent = new Intent(TopicDetail.this, ChonOptionStudy.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("type", "gotu");
            intent.putExtra("topicName", nameTopic.getText().toString());
            intent.putParcelableArrayListExtra("wordList", new ArrayList<>(wordList));
            startActivity(intent);
        });

        //Xử lý hiển thị xếp hạng dựa theo trạng thái của topic có public hay không nha
        cardRank.setOnClickListener(view -> {
            Intent intent = new Intent(TopicDetail.this, GetRank.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            startActivity(intent);
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("topicID");

        db.collection("topics").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference documentReference = task.getResult().getReference();
                nameTopic.setText(task.getResult().getString("name"));
                long numWords = task.getResult().getLong("numWord");
                count = (int) numWords;
                numWord.setText(getString(R.string.num_words, String.format(Locale.getDefault(), "%d", numWords)));
                statusTopic = task.getResult().getString("status");
                status.setText("Trạng thái: " + statusTopic);
                Timestamp createTime = task.getResult().getTimestamp("createTime");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(createTime.toDate());
                date.setText(getString(R.string.create_topic, formattedDate));
                DocumentReference userCreateRef = task.getResult().getDocumentReference("userCreate");
                if (userCreateRef != null) {
                    userCreateRef.get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            String userName = userTask.getResult().getString("fullName");
                            author.setText(getString(R.string.author, userName));
                        }
                    });
                }

                showCaseButton();
            }
        });

        wordList = new ArrayList<>();
        fetchWordList();



        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xoá topic");
            builder.setMessage("Bạn có chắc chắn muốn xoá topic này không?");
            builder.setPositiveButton("OK", (dialog, which) -> {
                db.collection("folders").whereArrayContains("topics", db.document("topics/" + id)).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> topics = (List<DocumentReference>) document.get("topics");
                            if (topics != null) {
                                for (int i = 0; i < topics.size(); i++) {
                                    if (topics.get(i).getId().equals(id)) {
                                        topics.remove(i);
                                        break;
                                    }
                                }
                                document.getReference().update("topics", topics);
                            }
                        }
                    }
                });
                db.collection("topics").document(id).delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastUtils.showShortToast(TopicDetail.this, "Xoá topic thành công");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("topicID", id);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        ToastUtils.showShortToast(TopicDetail.this, "Error: " + task.getException().getMessage());
                    }
                });
                dialog.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        btnLuuTopic.setOnClickListener(view -> alertSaveTopic());

        cardXuatFile.setOnClickListener(view -> {
            alertExportFile();
        });


    }

    private void init() {
        nameTopic = findViewById(R.id.nameTopic);
        numWord = findViewById(R.id.numWord);
        author = findViewById(R.id.author);
        date = findViewById(R.id.date);
        status = findViewById(R.id.status);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> finish());
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        cardFlashcard = findViewById(R.id.cardFlashcard);
        cardTracNghiem = findViewById(R.id.cardTracNghiem);
        cardGoTu = findViewById(R.id.cardGoTu);
        cardRank = findViewById(R.id.cardRank);
        recyclerViewTatCaCacThe = findViewById(R.id.recyclerViewTatCaCacThe);
        recyclerViewTatCaCacThe.setLayoutManager(new LinearLayoutManager(this));
        wordList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        topicId = getIntent().getStringExtra("topicID");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnLuuTopic = findViewById(R.id.btnLuuTopic);
        cardXuatFile = findViewById(R.id.xuatFile);
    }


    //Chỗ nầy để kiểm tra hiển thị c nút tương ứng
    private void showCaseButton() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String topicId = getIntent().getStringExtra("topicID");
        db.collection("topics").document(topicId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String statusTopic = task.getResult().getString("status");
                DocumentReference userCreateRef = task.getResult().getDocumentReference("userCreate");
                if (userCreateRef != null) {
                    userCreateRef.get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            String creatorId = userTask.getResult().getId();
                            if ("public".equals(statusTopic) && userId.equals(creatorId)) {
                                btnEdit.setVisibility(View.VISIBLE);
                                btnDelete.setVisibility(View.VISIBLE);
                                btnLuuTopic.setVisibility(View.GONE);
                                cardRank.setVisibility(View.VISIBLE);
                            } else if ("public".equals(statusTopic)) {
                                btnEdit.setVisibility(View.GONE);
                                btnDelete.setVisibility(View.GONE);
                                cardRank.setVisibility(View.VISIBLE);
                                db.collection("users").document(userId).get().addOnCompleteListener(userDocTask -> {
                                    if (userDocTask.isSuccessful()) {
                                        List<DocumentReference> savedTopics = (List<DocumentReference>) userDocTask.getResult().get("saveTopic");
                                        if (savedTopics != null && savedTopics.contains(db.collection("topics").document(topicId))) {
                                            btnLuuTopic.setBackgroundResource(R.drawable.unsave_public_topic);
                                            btnLuuTopic.setOnClickListener(view -> alertUnsaveTopic());
                                        } else {
                                            btnLuuTopic.setBackgroundResource(R.drawable.save_public_topic);
                                            btnLuuTopic.setOnClickListener(view -> alertSaveTopic());
                                        }
                                        btnLuuTopic.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                btnEdit.setVisibility(View.VISIBLE);
                                btnDelete.setVisibility(View.VISIBLE);
                                btnLuuTopic.setVisibility(View.GONE);
                                cardRank.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }


    private void alertSaveTopic(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lưu Topic");
        builder.setMessage("Bạn có chắc chắn muốn lưu Topic này không?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            saveTopicToUser();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveTopicToUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String topicId = getIntent().getStringExtra("topicID");
        DocumentReference topicRef = db.collection("topics").document(topicId);
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("saveTopic", FieldValue.arrayUnion(topicRef))
                .addOnSuccessListener(aVoid -> {
                    ToastUtils.showShortToast(TopicDetail.this, "Lưu Topic thành công");
                    btnLuuTopic.setBackgroundResource(R.drawable.unsave_public_topic);
                    btnLuuTopic.setOnClickListener(view -> alertUnsaveTopic());
                })
                .addOnFailureListener(e -> ToastUtils.showShortToast(TopicDetail.this, "Lỗi lưu topic: " + e.getMessage()));
    }

    private void unsaveTopicFromUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String topicId = getIntent().getStringExtra("topicID");
        DocumentReference topicRef = db.collection("topics").document(topicId);
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("saveTopic", FieldValue.arrayRemove(topicRef))
                .addOnSuccessListener(aVoid -> {
                    ToastUtils.showShortToast(TopicDetail.this, "Hủy lưu Topic thành công");
                    btnLuuTopic.setBackgroundResource(R.drawable.save_public_topic);
                    btnLuuTopic.setOnClickListener(view -> alertSaveTopic());

                    // Trả kết quả để cập nhật lại danh sách topic đã lưu
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                })
                .addOnFailureListener(e -> ToastUtils.showShortToast(TopicDetail.this, "Lỗi hủy lưu topic: " + e.getMessage()));
    }

    private void alertUnsaveTopic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hủy lưu Topic");
        builder.setMessage("Bạn có chắc chắn muốn hủy lưu Topic này không?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            unsaveTopicFromUser();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onWordMarked(Word word, boolean isMarked) {
        if (isMarked) {
            db.collection("topics").document(topicId)
                    .collection("markedList").document(userId)
                    .update("wordIds", FieldValue.arrayUnion(word.getId()))
                    .addOnFailureListener(e -> {
                        if (e.getMessage().contains("No document to update")) {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("wordIds", FieldValue.arrayUnion(word.getId()));
                            db.collection("topics").document(topicId)
                                    .collection("markedList").document(userId)
                                    .set(data);
                        } else {
                            Toast.makeText(this, "Marking failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            db.collection("topics").document(topicId)
                    .collection("markedList").document(userId)
                    .update("wordIds", FieldValue.arrayRemove(word.getId()));
        }
    }


    private void alertExportFile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xuất file CSV");
        builder.setMessage("Xuất danh sách từ vựng ra file CSV?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            String csvContent = generateCSVContent(wordList);
            saveCSVToFile(csvContent);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String generateCSVContent(List<Word> wordList) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("[Word],[Meaning]\n");
        for (Word word : wordList) {
            csvContent.append(word.getName()).append(",").append(word.getMeaning()).append("\n");
        }
        return csvContent.toString();
    }
    private void saveCSVToFile(String csvContent) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        String currentDate = new SimpleDateFormat("dd_MM_yyyy_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "word_list_ex_" + currentDate + ".csv";

        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File estudyDir = new File(downloadDir, "Estudy/exported");
        if (!estudyDir.exists()) {
            estudyDir.mkdirs();
        }
        File csvFile = new File(estudyDir, fileName);

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(csvContent);
            ToastUtils.showLongToast(this, "Xuất file thành công:\n " + csvFile.getAbsolutePath());
        } catch (IOException e) {
            ToastUtils.showLongToast(this, "Xuất file thất bại");
        }
    }


    private void fetchWordList() {

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.loadWordsAndMarkedWords(topicId, userId, new QuizViewModel.WordListCallback() {
            @Override
            public void onWordListLoaded(List<Word> words) {
                wordList = words;
                wordListAdapter = new WordListAdapter(TopicDetail.this, words, TopicDetail.this);

                topicViewModel = new ViewModelProvider(TopicDetail.this).get(TopicViewModel.class);

                topicViewModel.getTopicProgress(topicId, userId, (learnedWordsMap, learningWordsMap, unlearnWordsMap) -> {
                    wordListAdapter.setProgressMaps(learnedWordsMap, learningWordsMap, unlearnWordsMap);
                    wordListAdapter.notifyDataSetChanged();
                });

                recyclerViewTatCaCacThe.setAdapter(wordListAdapter);
                numWord.setText(getString(R.string.num_words, String.format(Locale.getDefault(), "%d", words.size())));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(TopicDetail.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}