package tdtu.EStudy_App.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.adapters.WordListAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;

public class TopicDetail extends AppCompatActivity {
    TextView nameTopic,numWord,author,date;
    RecyclerView recyclerViewTatCaCacThe;
    List<Word> wordList;
    WordListAdapter wordListAdapter;
    FirebaseFirestore db;
    AppCompatButton btnCancel;
    Button btnEdit,btnDelete;
    CardView cardFlashcard, cardTracNghiem, cardGoTu, cardRank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.topic_detail);
        init();
        cardFlashcard.setOnClickListener(view -> {
            Intent intent = new Intent(TopicDetail.this, ChonOptionStudyFlashcard.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            startActivity(intent);
        });

        cardTracNghiem.setOnClickListener(view -> {
            Intent intent = new Intent(TopicDetail.this, ChonOptionStudy.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("type", "tracnghiem");
            startActivity(intent);
        });

        cardGoTu.setOnClickListener(view -> {
            Intent intent = new Intent(TopicDetail.this, ChonOptionStudy.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("type", "gotu");
            startActivity(intent);
        });

        cardRank.setOnClickListener(view -> {
            Intent intent = new Intent(TopicDetail.this, GetRank.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            startActivity(intent);
        });
        Intent intent=getIntent();
        String id=intent.getStringExtra("topicID");
        db.collection("topics").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference documentReference=task.getResult().getReference();
                nameTopic.setText(task.getResult().getString("name"));
                long numWords = task.getResult().getLong("numWord");
                numWord.setText(getString(R.string.num_words, String.format(Locale.getDefault(), "%d", numWords)));
                Timestamp createTime = task.getResult().getTimestamp("createTime");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(createTime.toDate());
                date.setText(getString(R.string.create_topic,formattedDate));
                DocumentReference userCreateRef = task.getResult().getDocumentReference("userCreate");
                if (userCreateRef != null) {
                    userCreateRef.get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            String userName = userTask.getResult().getString("fullName");
                            author.setText(getString(R.string.author, userName));
                        }
                    });
                }
                documentReference.collection("words").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        wordList.clear();
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            String name = document.getString("vietnameseMean");
                            String mean = document.getString("englishMean");
                            String state = document.getString("status");
                            boolean marked = document.getBoolean("marked");
                            Word word1 = new Word();
                            word1.setName(name);
                            word1.setMeaning(mean);
                            word1.setState(state);
                            word1.setMarked(marked);
                            wordList.add(word1);
                        }
                        wordListAdapter = new WordListAdapter(TopicDetail.this, wordList);
                        recyclerViewTatCaCacThe.setAdapter(wordListAdapter);
                    } else {
                        ToastUtils.showShortToast(TopicDetail.this, "Error: " + task1.getException().getMessage());
                    }
                });
            }
        });


        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xoá topic");
            builder.setMessage("Bạn có chắc chắn muốn xoá topic này không?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle the OK button click
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
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Handle the Cancel button click
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
    private void init(){
        // Find all views by ID
        nameTopic=findViewById(R.id.nameTopic);
        numWord=findViewById(R.id.numWord);
        author=findViewById(R.id.author);
        date=findViewById(R.id.date);
        btnCancel=findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> finish());
        btnEdit=findViewById(R.id.btnEdit);
        btnDelete=findViewById(R.id.btnDelete);
        cardFlashcard=findViewById(R.id.cardFlashcard);
        cardTracNghiem=findViewById(R.id.cardTracNghiem);
        cardGoTu=findViewById(R.id.cardGoTu);
        cardRank=findViewById(R.id.cardRank);
        // Set up the RecyclerView
        recyclerViewTatCaCacThe=findViewById(R.id.recyclerViewTatCaCacThe);
        recyclerViewTatCaCacThe.setLayoutManager(new LinearLayoutManager(this));
        wordList= new ArrayList<>();
        // Set up the Firestore database
        db=FirebaseFirestore.getInstance();
    }
}