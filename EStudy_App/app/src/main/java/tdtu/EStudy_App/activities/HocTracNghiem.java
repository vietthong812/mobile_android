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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.CardAdapter;
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
    private Set<Word> learnedWords = new HashSet<>();

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

        wordList = intent.getParcelableArrayListExtra("wordList");
        if (wordList == null || wordList.isEmpty()) {
            ToastUtils.showShortToast(this, "No words available!");
            return;
        }

        cardTracNghiemAdapter = new CardTracNghiemAdapter(this, wordList, false); //chỗ này
        viewPagerCardTN.setAdapter(cardTracNghiemAdapter);
        countNumTN.setText("1/" + wordList.size());

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

    private List<Word> suffleWordList(List<Word> wordList) {
        for (int i = 0; i < wordList.size(); i++) {
            int randomIndex = (int) (Math.random() * wordList.size());
            Word temp = wordList.get(i);
            wordList.set(i, wordList.get(randomIndex));
            wordList.set(randomIndex, temp);
        }
        return wordList;
    }


}
