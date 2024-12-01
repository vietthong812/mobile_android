// TopicDetail.java
package tdtu.EStudy_App.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.WordListAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class TopicDetail extends AppCompatActivity {
    TextView nameTopic, numWord, author, date, status;
    RecyclerView recyclerViewTatCaCacThe;
    List<Word> wordList;
    WordListAdapter wordListAdapter;
    FirebaseFirestore db;
    AppCompatButton btnCancel;
    Button btnEdit, btnDelete, btnLuuTopic;
    CardView cardFlashcard, cardTracNghiem, cardGoTu, cardRank;
    QuizViewModel quizViewModel;
    int count = 0;
    String statusTopic;
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
            intent.putExtra("wordList", (ArrayList<Word>) wordList);
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
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        quizViewModel.loadWordList(id, new QuizViewModel.WordListCallback() {
            @Override
            public void onWordListLoaded(List<Word> words) {
                wordList = words;
                wordListAdapter = new WordListAdapter(TopicDetail.this, words);
                recyclerViewTatCaCacThe.setAdapter(wordListAdapter);
                numWord.setText(getString(R.string.num_words, String.format(Locale.getDefault(), "%d", words.size())));
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showShortToast(TopicDetail.this, "Error: " + e.getMessage());
            }
        });

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
        btnLuuTopic = findViewById(R.id.btnLuuTopic);
    }

    private void showCaseButton() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("topics").document(getIntent().getStringExtra("topicID")).get().addOnCompleteListener(task -> {
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
                                btnLuuTopic.setVisibility(View.VISIBLE);
                                cardRank.setVisibility(View.VISIBLE);
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
}