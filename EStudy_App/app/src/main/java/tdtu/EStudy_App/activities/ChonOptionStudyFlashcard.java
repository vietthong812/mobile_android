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

import tdtu.EStudy_App.R;

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
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            intent.putExtra("topicName", getIntent().getStringExtra("topicName"));
            intent.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
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

            cardViewTuDongPhatAm = findViewById(R.id.cardPhatAmFC);
            cardViewDaoThuTu = findViewById(R.id.cardDaoThuTuFC);
            cardViewHocDanhDau = findViewById(R.id.cardHocDanhDauFC);
            cardDaoNgonNguFC = findViewById(R.id.cardDaoNgonNguFC);


        }
    protected void onClickToCheckBox(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

    }

    }

