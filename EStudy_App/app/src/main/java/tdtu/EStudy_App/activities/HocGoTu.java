package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.CardAdapter;
import tdtu.EStudy_App.adapters.CardGoTuAdapter;
import tdtu.EStudy_App.adapters.OnWordMarkedListener;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocGoTu extends AppCompatActivity implements OnWordMarkedListener {

    private AppCompatButton btnCancleGoTu, btnNextGoTu, btnPreviousGoTu;
    private CardGoTuAdapter cardGoTuAdapter;
    private List<Word> wordList;
    private TextView countNumGoTu;
    private ViewPager2 viewPagerCardGoTu;
    private TextView titleGoTu;
    private CardView cardViewNopBaiGoTu;
    private Set<Word> learnedWords = new HashSet<>();
    private Set<Word> wrongWordsList = new HashSet<>();
    private String topicId;
    private String userId;
    private FirebaseFirestore db;
    private String option;
    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_gotu);

        init();
        btnCancleGoTu.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        String topicId = intent.getStringExtra("topicID");
        String topicName = intent.getStringExtra("topicName");
        option = intent.getStringExtra("Option");
        titleGoTu.setText("Gõ từ - " + topicName);

        wordList = intent.getParcelableArrayListExtra("wordList");
        if (wordList == null || wordList.isEmpty()) {
            ToastUtils.showShortToast(this, "No words available!");
            finish();
            return;
        }

        cardGoTuAdapter = new CardGoTuAdapter(this, wordList, option, HocGoTu.this, viewPagerCardGoTu); //Chỗ na
        viewPagerCardGoTu.setAdapter(cardGoTuAdapter);
        countNumGoTu.setText("1/" + wordList.size());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (option != null && option.equals("AutoPronunciation")) {
                playWordSound(wordList.get(0).getName());
            }
        }, 1000);

        btnNextGoTu.setOnClickListener(v -> {
            int currentItem = viewPagerCardGoTu.getCurrentItem();
            if (currentItem < wordList.size() - 1) {
                viewPagerCardGoTu.setCurrentItem(currentItem + 1);
            }
        });

        btnPreviousGoTu.setOnClickListener(v -> {
            int currentItem = viewPagerCardGoTu.getCurrentItem();
            if (currentItem > 0) {
                viewPagerCardGoTu.setCurrentItem(currentItem - 1);
            }
        });

        viewPagerCardGoTu.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                countNumGoTu.setText((position + 1) + "/" + wordList.size());
                if(option != null && option.equals("AutoPronunciation" )){
                    playWordSound(wordList.get(position).getName());
                }
            }
        });

        cardViewNopBaiGoTu.setOnClickListener(v -> {
            learnedWords = cardGoTuAdapter.getLearnedWords();
            wrongWordsList = cardGoTuAdapter.getWrongWordsList();

            Intent intent1 = new Intent(HocGoTu.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicId);
            intent1.putExtra("topicName", topicName);
            intent1.putParcelableArrayListExtra("learnedWords", new ArrayList<>(learnedWords));
            intent1.putParcelableArrayListExtra("wrongWordsList", new ArrayList<>(wrongWordsList));
            intent1.putExtra("learningType", "translate");
            intent1.putParcelableArrayListExtra("topicWords", new ArrayList<Word>(wordList));

            startActivity(intent1);
            finish();
        });


    }


    protected void init(){
        btnCancleGoTu = findViewById(R.id.btnCancleGT);
        btnNextGoTu = findViewById(R.id.btnNextGT);
        btnPreviousGoTu = findViewById(R.id.btnPreviousGT);
        countNumGoTu = findViewById(R.id.countNumGT);
        viewPagerCardGoTu = findViewById(R.id.viewPagerCardGT);
        titleGoTu = findViewById(R.id.titleGT);
        cardViewNopBaiGoTu = findViewById(R.id.cardViewNopBaiGT);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
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

    private void playWordSound(String text) {
        if (textToSpeech != null) {
            String utteranceId = String.valueOf(System.currentTimeMillis());
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            ToastUtils.showShortToast(this, "Text to speech is not available!");
        }
    }

}
