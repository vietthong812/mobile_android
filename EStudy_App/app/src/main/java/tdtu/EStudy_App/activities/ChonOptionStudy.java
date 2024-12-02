package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class ChonOptionStudy extends AppCompatActivity {

    private AppCompatButton btnCancleOption, btnBatDauHoc;
    private CardView cardViewHienDapAn, cardViewTuDongPhatAm, cardViewDaoThuTu, cardViewHocDanhDau, cardDaoNgonNgu;
    private CheckBox checkHienDapAn, checkTuDongPhatAm, checkDaoThuTu, checkHocDanhDau, checkDaoNgonNgu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.option_study);

        init();

        cardViewHienDapAn.setOnClickListener(v -> onClickToCheckBox(checkHienDapAn));
        cardViewTuDongPhatAm.setOnClickListener(v -> onClickToCheckBox(checkTuDongPhatAm));
        cardViewDaoThuTu.setOnClickListener(v -> onClickToCheckBox(checkDaoThuTu));
        cardViewHocDanhDau.setOnClickListener(v -> onClickToCheckBox(checkHocDanhDau));
        cardDaoNgonNgu.setOnClickListener(v -> onClickToCheckBox(checkDaoNgonNgu));

        btnCancleOption.setOnClickListener(v -> {
            finish();
        });

        btnBatDauHoc = findViewById(R.id.btnBatDauHoc);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type.equals("gotu")) {
            btnBatDauHoc.setOnClickListener(v -> {
                Intent intent1 = new Intent(ChonOptionStudy.this, HocGoTu.class);

                if (checkDaoThuTu.isChecked()) {
                    intent1.putParcelableArrayListExtra("wordList", shuffleWordList(getIntent().getParcelableArrayListExtra("wordList")));
                }
                else if(checkDaoNgonNgu.isChecked()){
                    intent1.putExtra("Option", "Reverse");
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                else if(checkTuDongPhatAm.isChecked()){
                    intent1.putExtra("Option", "AutoPronunciation");
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                else if(checkHienDapAn.isChecked()){
                    intent1.putExtra("Option", "ShowAnswer");
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                else if(checkHocDanhDau.isChecked()){
                    intent1.putParcelableArrayListExtra("wordList", getMarkedWords(getIntent().getParcelableArrayListExtra("wordList")));
                }
                else{
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                intent1.putExtra("topicID", getIntent().getStringExtra("topicID"));
                intent1.putExtra("topicName", getIntent().getStringExtra("topicName"));
                startActivity(intent1);
                finish();
            });}
        else if (type.equals("tracnghiem")) {
            btnBatDauHoc.setOnClickListener(v -> {
                Intent intent1 = new Intent(ChonOptionStudy.this, HocTracNghiem.class);

                if (checkDaoThuTu.isChecked()) {
                    intent1.putParcelableArrayListExtra("wordList", shuffleWordList(getIntent().getParcelableArrayListExtra("wordList")));
                }
                else if(checkDaoNgonNgu.isChecked()){
                    intent1.putExtra("Option", "Reverse");
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                else if(checkTuDongPhatAm.isChecked()){
                    intent1.putExtra("Option", "AutoPronunciation");
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                else if(checkHienDapAn.isChecked()){
                    intent1.putExtra("Option", "ShowAnswer");
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }
                else if(checkHocDanhDau.isChecked()){
                    intent1.putExtra("Option", "Marked");
                    intent1.putParcelableArrayListExtra("wordList", getMarkedWords(getIntent().getParcelableArrayListExtra("wordList")));
                }
                else{
                    intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                }

                intent1.putExtra("topicID", getIntent().getStringExtra("topicID"));
                intent1.putExtra("topicName", getIntent().getStringExtra("topicName"));
                startActivity(intent1);
                finish();
            });
        }
    }

    protected void init(){
        btnCancleOption = findViewById(R.id.btnCancelOption);
        cardViewHienDapAn = findViewById(R.id.cardHienDapAn);
        cardViewTuDongPhatAm = findViewById(R.id.cardPhatAm);
        cardViewDaoThuTu = findViewById(R.id.cardDaoThuTu);
        cardViewHocDanhDau = findViewById(R.id.cardDanhDau);
        cardDaoNgonNgu = findViewById(R.id.cadDaoNgonNgu);
        checkHienDapAn = findViewById(R.id.checkHienDapAn);
        checkTuDongPhatAm = findViewById(R.id.checkTuDongPhatAm);
        checkDaoThuTu = findViewById(R.id.checkDaoThuTu);
        checkHocDanhDau = findViewById(R.id.checkHocDanhDau);
        checkDaoNgonNgu = findViewById(R.id.checkDaoNgonNgu);

        checkTuDongPhatAm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkDaoThuTu.setChecked(false);
                checkHocDanhDau.setChecked(false);
                checkDaoNgonNgu.setChecked(false);
            }
        });
        checkDaoNgonNgu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkDaoThuTu.setChecked(false);
                checkHocDanhDau.setChecked(false);
                checkTuDongPhatAm.setChecked(false);
            }
        });
        checkDaoThuTu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkTuDongPhatAm.setChecked(false);
                checkHocDanhDau.setChecked(false);
                checkDaoNgonNgu.setChecked(false);
            }
        });
        checkHocDanhDau.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkTuDongPhatAm.setChecked(false);
                checkDaoThuTu.setChecked(false);
                checkDaoNgonNgu.setChecked(false);
            }
        });
        checkHienDapAn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkTuDongPhatAm.setChecked(false);
                checkDaoThuTu.setChecked(false);
                checkHocDanhDau.setChecked(false);
                checkDaoNgonNgu.setChecked(false);
            }
        });
    }

    protected void onClickToCheckBox(CheckBox checkBox) {
        checkBox.setChecked(!checkBox.isChecked());
    }

    private ArrayList<Word> shuffleWordList(ArrayList<Word> wordList) {
        for (int i = 0; i < wordList.size(); i++) {
            int randomIndex = (int) (Math.random() * wordList.size());
            Word temp = wordList.get(i);
            wordList.set(i, wordList.get(randomIndex));
            wordList.set(randomIndex, temp);
        }
        return wordList;
    }

    private ArrayList<Word> getMarkedWords(ArrayList<Word> wordList) {
        ArrayList<Word> markedWords = new ArrayList<>();
        for (Word word : wordList) {
            if (word.isMarked()) {
                markedWords.add(word);
            }
        }
        return markedWords;
    }
}