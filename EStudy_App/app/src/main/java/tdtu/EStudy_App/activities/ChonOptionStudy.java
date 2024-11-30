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

import tdtu.EStudy_App.R;

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
                intent1.putExtra("topicID", getIntent().getStringExtra("topicID"));
                intent1.putExtra("topicName", getIntent().getStringExtra("topicName"));
                intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                startActivity(intent1);
                finish();
            });}
        else if (type.equals("tracnghiem")) {
            btnBatDauHoc.setOnClickListener(v -> {
                Intent intent1 = new Intent(ChonOptionStudy.this, HocTracNghiem.class);
                intent1.putExtra("topicID", getIntent().getStringExtra("topicID"));
                intent1.putExtra("topicName", getIntent().getStringExtra("topicName"));
                intent1.putParcelableArrayListExtra("wordList", getIntent().getParcelableArrayListExtra("wordList"));
                startActivity(intent1);
                finish();
            });
        }

        cardViewDaoThuTu.setOnClickListener(v -> {
            if (checkDaoThuTu.isChecked()) {
                checkDaoThuTu.setChecked(false);
            } else {
                checkDaoThuTu.setChecked(true);
            }
        });


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

    }

    protected void onClickToCheckBox(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

    }


}