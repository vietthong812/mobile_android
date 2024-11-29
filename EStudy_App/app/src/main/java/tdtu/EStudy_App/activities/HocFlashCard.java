// HocFlashCard.java
package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.CardAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocFlashCard extends AppCompatActivity {

    private AppCompatButton btnCancelFC, btnNextFC, btnPreviousFC;
    private TextView countNumFC;
    private ViewPager2 viewPagerCardFC;
    private CardAdapter cardAdapter;
    private List<Word> wordList;
    private TextView titleFC;
    private CardView cardViewNopBaiFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_flashcard);

        init();
        btnCancelFC.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String topicID = intent.getStringExtra("topicID");
        String topicName = intent.getStringExtra("topicName");
        titleFC.setText("Flashcard - " + topicName);

        loadWords(topicID);



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
                countNumFC.setText((position + 1) + "/" + wordList.size());
            }
        });

        cardViewNopBaiFC.setOnClickListener(v -> {
            Intent intent1 = new Intent(HocFlashCard.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicID);
            intent1.putExtra("topicName", topicName);
            startActivity(intent1);
            finish();
        });
    }

    protected void init() {
        btnCancelFC = findViewById(R.id.btnCancelFC);
        btnNextFC = findViewById(R.id.btnNextFC);
        btnPreviousFC = findViewById(R.id.btnPreviousFC);
        countNumFC = findViewById(R.id.countNumFC);
        viewPagerCardFC = findViewById(R.id.viewPagerCardFC);
        titleFC = findViewById(R.id.titleFC);
        cardViewNopBaiFC = findViewById(R.id.cardViewNopBaiFC);
    }

    private void loadWords(String topicID) {
        QuizViewModel quizViewModel = new QuizViewModel();
        quizViewModel.loadWordList(topicID, new QuizViewModel.WordListCallback() {
            @Override
            public void onWordListLoaded(List<Word> words) {
                if (words == null || words.isEmpty()) {
                    ToastUtils.showShortToast(HocFlashCard.this, "No words available!");
                    return;
                }
                wordList = words;
                cardAdapter = new CardAdapter(HocFlashCard.this, wordList);
                viewPagerCardFC.setAdapter(cardAdapter);
                countNumFC.setText("1/" + wordList.size());
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showShortToast(HocFlashCard.this, "Error: " + e.getMessage());
            }
        });
    }

}