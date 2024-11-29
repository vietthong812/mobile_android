package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tdtu.EStudy_App.R;

public class ChonOptionStudy extends AppCompatActivity {

    private AppCompatButton btnCancleOption, btnBatDauHoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.option_study);

        btnCancleOption = findViewById(R.id.btnCancelOption);
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
                startActivity(intent1);
                finish();
            });}
        else if (type.equals("tracnghiem")) {
            btnBatDauHoc.setOnClickListener(v -> {
                Intent intent1 = new Intent(ChonOptionStudy.this, HocTracNghiem.class);
                intent1.putExtra("topicID", getIntent().getStringExtra("topicID"));
                intent1.putExtra("topicName", getIntent().getStringExtra("topicName"));
                startActivity(intent1);
                finish();
            });
        }


    }
}