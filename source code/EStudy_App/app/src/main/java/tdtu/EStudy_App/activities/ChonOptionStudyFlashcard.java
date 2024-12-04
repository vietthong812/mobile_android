package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class ChonOptionStudyFlashcard extends AppCompatActivity {


    private AppCompatButton btnCancleOptionFC, btnBatDauHocFC;
    private CheckBox checkTuDongPhatAm, checkDaoThuTu, checkHocDanhDau, checkDaoNgonNguFC;
    private androidx.cardview.widget.CardView  cardViewTuDongPhatAm, cardViewDaoThuTu, cardViewHocDanhDau, cardDaoNgonNguFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.option_study_flashcard);

        init();


        btnCancleOptionFC.setOnClickListener(v -> {
            finish();
        });

        cardViewTuDongPhatAm.setOnClickListener(v -> onClickToCheckBox(checkTuDongPhatAm));
        cardViewDaoThuTu.setOnClickListener(v -> onClickToCheckBox(checkDaoThuTu));
        cardViewHocDanhDau.setOnClickListener(v -> onClickToCheckBox(checkHocDanhDau));
        cardDaoNgonNguFC.setOnClickListener(v -> onClickToCheckBox(checkDaoNgonNguFC));


        btnBatDauHocFC = findViewById(R.id.btnBatDauHocFC);
        btnBatDauHocFC.setOnClickListener(v -> {

            Intent intent = new Intent(ChonOptionStudyFlashcard.this, HocFlashCard.class);

            if(checkTuDongPhatAm.isChecked()){
                intent.putExtra("Option", "AutoPronunciation");
                intent.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
            }
            else if (checkDaoThuTu.isChecked()){
                intent.putParcelableArrayListExtra("wordList", shuffleWordList(getIntent().getParcelableArrayListExtra("wordList")));
            }
            else if (checkHocDanhDau.isChecked()){
                intent.putParcelableArrayListExtra("wordList", getMarkedWords(getIntent().getParcelableArrayListExtra("wordList")));
            }
            else if (checkDaoNgonNguFC.isChecked()){
                intent.putExtra("Option", "Reverse");
                intent.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
            }
            else{
                intent.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
            }


            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("topicName", getIntent().getStringExtra("topicName"));

            startActivity(intent);
            finish();
        });
    }

        protected void init(){
            btnCancleOptionFC = findViewById(R.id.btnCancelOptionFC);
            btnBatDauHocFC = findViewById(R.id.btnBatDauHocFC);

            checkTuDongPhatAm = findViewById(R.id.checkTuDongPhatAmFC);
            checkDaoThuTu = findViewById(R.id.checkDaoThuTuFC);
            checkHocDanhDau = findViewById(R.id.checkHocDanhDauFC);
            checkDaoNgonNguFC = findViewById(R.id.checkDaoNgonNguFC);

            checkTuDongPhatAm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkDaoThuTu.setChecked(false);
                    checkHocDanhDau.setChecked(false);
                    checkDaoNgonNguFC.setChecked(false);
                }
            });
            checkDaoNgonNguFC.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
                    checkDaoNgonNguFC.setChecked(false);
                }
            });
            checkHocDanhDau.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkTuDongPhatAm.setChecked(false);
                    checkDaoThuTu.setChecked(false);
                    checkDaoNgonNguFC.setChecked(false);
                }
            });



            cardViewTuDongPhatAm = findViewById(R.id.cardPhatAmFC);
            cardViewDaoThuTu = findViewById(R.id.cardDaoThuTuFC);
            cardViewHocDanhDau = findViewById(R.id.cardHocDanhDauFC);
            cardDaoNgonNguFC = findViewById(R.id.cardDaoNgonNguFC);


        }
    private void onClickToCheckBox(CheckBox checkBox) {
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

