// HocFlashCard.java
package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.ArraySet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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
import tdtu.EStudy_App.adapters.OnWordMarkedListener;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocFlashCard extends AppCompatActivity implements OnWordMarkedListener {

    private AppCompatButton btnCancelFC, btnNextFC, btnPreviousFC, btnAutoPlayFC;
    private TextView countNumFC;
    private ViewPager2 viewPagerCardFC;
    private CardAdapter cardAdapter;
    private List<Word> wordList;
    private TextView titleFC;
    private CardView cardViewNopBaiFC;
    private Set<Word> learnedWords = new HashSet<>();
    private boolean isAutoPlaying = false;
    private Handler autoPlayHandler = new Handler(Looper.getMainLooper());
    private TextToSpeech textToSpeech;
    private String option;
    private String topicId;
    private String userId;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_flashcard);

        init();

        btnCancelFC.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String topicName = intent.getStringExtra("topicName");
        option = intent.getStringExtra("Option");
        titleFC.setText("Flashcard - " + topicName);

        wordList = intent.getParcelableArrayListExtra("wordList");
        if (wordList == null || wordList.isEmpty()) {
            ToastUtils.showShortToast(this, "No words available!");

            return;
        }


        cardAdapter = new CardAdapter(this, wordList, option, HocFlashCard.this);
        viewPagerCardFC.setAdapter(cardAdapter);
        countNumFC.setText("1/" + wordList.size());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (option != null && option.equals("AutoPronunciation")) {
                playWordSound(wordList.get(0).getName());
            }
        }, 500);

        btnNextFC.setOnClickListener(v -> {
            int currentItem = viewPagerCardFC.getCurrentItem();
            if (currentItem < wordList.size() - 1) {
                viewPagerCardFC.setCurrentItem(currentItem + 1);
            }
        });

        btnPreviousFC.setOnClickListener(v -> {
            int currentItem = viewPagerCardFC.getCurrentItem();
            if (currentItem > 0) {
                viewPagerCardFC.setCurrentItem(currentItem - 1);
            }
        });

        viewPagerCardFC.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                learnedWords.add(wordList.get(position));
                cardAdapter.setEnglishFront(true);
                countNumFC.setText((position + 1) + "/" + wordList.size());
                if(option != null && option.equals("AutoPronunciation" )){
                    playWordSound(wordList.get(position).getName());
                }
            }
        });

        cardViewNopBaiFC.setOnClickListener(v -> {

            Intent intent1 = new Intent(HocFlashCard.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicId);
            intent1.putExtra("topicName", topicName);
            intent1.putParcelableArrayListExtra("learnedWords", new ArrayList<Word>(learnedWords));
            intent1.putParcelableArrayListExtra("topicWords", new ArrayList<Word>(wordList));
            intent1.putExtra("learningType", "flashcard");
            startActivity(intent1);
            finish();
        });

        btnAutoPlayFC.setOnClickListener(v -> toggleAutoPlay());
    }

    protected void init() {
        btnCancelFC = findViewById(R.id.btnCancelFC);
        btnNextFC = findViewById(R.id.btnNextFC);
        btnPreviousFC = findViewById(R.id.btnPreviousFC);
        countNumFC = findViewById(R.id.countNumFC);
        viewPagerCardFC = findViewById(R.id.viewPagerCardFC);
        titleFC = findViewById(R.id.titleFC);
        cardViewNopBaiFC = findViewById(R.id.cardViewNopBaiFC);
        btnAutoPlayFC = findViewById(R.id.btnAutoPlayFC);

        db = FirebaseFirestore.getInstance();
        topicId = getIntent().getStringExtra("topicID");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }

    private void toggleAutoPlay() {
        isAutoPlaying = !isAutoPlaying;
        if (isAutoPlaying) {
            btnAutoPlayFC.setBackgroundResource(R.drawable.pause_icon);
            autoPlayHandler.postDelayed(autoPlayRunnable, 3000);
        } else {
            btnAutoPlayFC.setBackgroundResource(R.drawable.auto_icon);
            autoPlayHandler.removeCallbacks(autoPlayRunnable);
        }
    }

    private final Runnable autoPlayRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAutoPlaying) {
                return;
            }

            int currentItem = viewPagerCardFC.getCurrentItem();
            if (currentItem < wordList.size()) {
                cardAdapter.flipCardAtPosition(currentItem);


                autoPlayHandler.postDelayed(() -> {
                    if (currentItem < wordList.size() - 1) {
                        viewPagerCardFC.setCurrentItem(currentItem + 1);
                        autoPlayHandler.postDelayed(autoPlayRunnable, 3000);
                    } else {
                        isAutoPlaying = false;
                        btnAutoPlayFC.setBackgroundResource(R.drawable.auto_icon);
                    }
                }, 3000);
            }
        }
    };
    private void playWordSound(String text) {
        if (textToSpeech != null) {
            String utteranceId = String.valueOf(System.currentTimeMillis());
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            ToastUtils.showShortToast(this, "Text to speech is not available!");
        }
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

}