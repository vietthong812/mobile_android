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
import tdtu.EStudy_App.adapters.CardTracNghiemAdapter;
import tdtu.EStudy_App.adapters.OnWordMarkedListener;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocTracNghiem extends AppCompatActivity implements OnWordMarkedListener {

    private AppCompatButton btnCancleTN, btnNextTN, btnPreviousTN;
    private TextView countNumTN, titleTN;
    private ViewPager2 viewPagerCardTN;
    private CardTracNghiemAdapter cardTracNghiemAdapter;
    private List<Word> wordList;
    private CardView cardViewNopBaiTN;
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
        setContentView(R.layout.study_tracnghiem);

        init();

        btnCancleTN.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        String topicID = intent.getStringExtra("topicID");
        String topicName = intent.getStringExtra("topicName");
        option = intent.getStringExtra("Option");

        titleTN.setText("Trắc nghiệm - " + topicName);

        wordList = intent.getParcelableArrayListExtra("wordList");
        if (wordList == null || wordList.isEmpty() || wordList.size() < 4) {
            ToastUtils.showShortToast(this, "Không có từ vựng nào được lưu trữ");
            finish();
        }

        cardTracNghiemAdapter = new CardTracNghiemAdapter(this, wordList, option, HocTracNghiem.this, viewPagerCardTN); //chỗ này
        viewPagerCardTN.setAdapter(cardTracNghiemAdapter);
        countNumTN.setText("1/" + wordList.size());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (option != null && option.equals("AutoPronunciation")) {
                playWordSound(wordList.get(0).getName());
            }
        }, 500);

        btnNextTN.setOnClickListener(v -> {
            int currentItem = viewPagerCardTN.getCurrentItem();
            if (currentItem < wordList.size() - 1) {
                viewPagerCardTN.setCurrentItem(currentItem + 1);
            }
        });

        btnPreviousTN.setOnClickListener(v -> {
            int currentItem = viewPagerCardTN.getCurrentItem();
            if (currentItem > 0) {
                viewPagerCardTN.setCurrentItem(currentItem - 1);
            }
        });

        viewPagerCardTN.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                countNumTN.setText((position + 1) + "/" + wordList.size());
                if(option != null && option.equals("AutoPronunciation" )){
                    playWordSound(wordList.get(position).getName());
                }
            }
        });

        cardViewNopBaiTN.setOnClickListener(v -> {
            learnedWords = cardTracNghiemAdapter.getLearnedWords();
            wrongWordsList = cardTracNghiemAdapter.getWrongWordsList();

            Intent intent1 = new Intent(HocTracNghiem.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicID);
            intent1.putExtra("topicName", topicName);
            intent1.putParcelableArrayListExtra("learnedWords", new ArrayList<>(learnedWords));
            intent1.putParcelableArrayListExtra("wrongWordsList", new ArrayList<>(wrongWordsList));
            intent1.putExtra("learningType", "quiz");
            intent1.putParcelableArrayListExtra("topicWords", new ArrayList<Word>(wordList));
            startActivity(intent1);
            finish();
        });


    }

    protected void init(){
        btnCancleTN = findViewById(R.id.btnCancleTN);
        btnNextTN = findViewById(R.id.btnNextTN);
        btnPreviousTN = findViewById(R.id.btnPreviousTN);
        countNumTN = findViewById(R.id.countNumTN);
        titleTN = findViewById(R.id.titleTN);
        viewPagerCardTN = findViewById(R.id.viewPagerCardTN);
        cardViewNopBaiTN = findViewById(R.id.cardViewNopBaiTN);

        db = FirebaseFirestore.getInstance();
        topicId = getIntent().getStringExtra("topicID");
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
