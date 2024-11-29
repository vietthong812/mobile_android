package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.CardGoTuAdapter;
import tdtu.EStudy_App.adapters.CardTracNghiemAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocTracNghiem extends AppCompatActivity {

    private AppCompatButton btnCancleTN, btnNextTN, btnPreviousTN;
    private TextView countNumTN, titleTN;
    private ViewPager2 viewPagerCardTN;
    private CardTracNghiemAdapter cardTracNghiemAdapter;
    private List<Word> wordList;
    private CardView cardViewNopBaiTN;



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
        titleTN.setText("Trắc nghiệm - " + topicName);

        loadWords(topicID);

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
            }
        });

        cardViewNopBaiTN.setOnClickListener(v -> {
            Intent intent1 = new Intent(HocTracNghiem.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicID);
            intent1.putExtra("topicName", topicName);
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
    }

    private void loadWords(String topicID) {
        QuizViewModel quizViewModel = new QuizViewModel();
        quizViewModel.loadWordList(topicID, new QuizViewModel.WordListCallback() {
            @Override
            public void onWordListLoaded(List<Word> words) {
                if (words == null || words.isEmpty()) {
                    ToastUtils.showShortToast(HocTracNghiem.this, "No words available!");
                    return;
                }
                wordList = new ArrayList<>(words); // Khởi tạo wordList
                cardTracNghiemAdapter = new CardTracNghiemAdapter(HocTracNghiem.this, wordList);
                viewPagerCardTN.setAdapter(cardTracNghiemAdapter);
                countNumTN.setText("1/" + wordList.size());
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showShortToast(HocTracNghiem.this, "Error: " + e.getMessage());
            }
        });
    }

}
