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

public class ChonOptionStudyFlashcard extends AppCompatActivity {


    private AppCompatButton btnCancleOptionFC, btnBatDauHocFC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.option_study_flashcard);

        btnCancleOptionFC = findViewById(R.id.btnCancelOptionFC);
        btnCancleOptionFC.setOnClickListener(v -> {
            finish();
        });

        btnBatDauHocFC = findViewById(R.id.btnBatDauHocFC);
        btnBatDauHocFC.setOnClickListener(v -> {
            Intent intent = new Intent(ChonOptionStudyFlashcard.this, HocFlashCard.class);
            intent.putExtra("topicID", getIntent().getStringExtra("topicID"));
            startActivity(intent);
            finish();
        });
        }

    }
