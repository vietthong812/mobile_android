package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.CardAdapter;
import tdtu.EStudy_App.adapters.CardGoTuAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocGoTu extends AppCompatActivity {

    private AppCompatButton btnCancleGoTu, btnNextGoTu, btnPreviousGoTu;
    private CardGoTuAdapter cardGoTuAdapter;
    private List<Word> wordList;
    private TextView countNumGoTu;
    private ViewPager2 viewPagerCardGoTu;
    private TextView titleGoTu;
    private CardView cardViewNopBaiGoTu;
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
        String topicID = intent.getStringExtra("topicID");
        String topicName = intent.getStringExtra("topicName");
        titleGoTu.setText("Gõ từ - " + topicName);

        wordList = intent.getParcelableArrayListExtra("wordList");
        if (wordList == null || wordList.isEmpty()) {
            ToastUtils.showShortToast(this, "No words available!");
            return;
        }

        cardGoTuAdapter = new CardGoTuAdapter(this, wordList, false); //Chỗ na
        viewPagerCardGoTu.setAdapter(cardGoTuAdapter);
        countNumGoTu.setText("1/" + wordList.size());

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
            }
        });

        cardViewNopBaiGoTu.setOnClickListener(v -> {
            Intent intent1 = new Intent(HocGoTu.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicID);
            intent1.putExtra("topicName", topicName);
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


    }



}
