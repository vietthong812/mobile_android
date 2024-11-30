package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class KetQuaHocTap extends AppCompatActivity {


    private AppCompatButton btnCancleResult;
    private TextView soLuongDung, soLuongSai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_result);
        btnCancleResult.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String learningType = intent.getStringExtra("learningType");
        switch (learningType){
            case "flashcard":
                getResultFlashCard();
                break;
            case "quiz":
                getResultQuiz();
                break;
            case "translate":
                getResultTranslate();
                break;
            default:
                break;
        }

        init();
        btnCancleResult.setOnClickListener(v -> finish());

    }

    private void getResultTranslate() {
    }

    private void getResultQuiz() {
    }

    protected void init(){
        btnCancleResult = findViewById(R.id.btnCancleResult);
        soLuongDung = findViewById(R.id.soLuongDung);
        soLuongSai = findViewById(R.id.soLuongSai);

    }

    private void getResultFlashCard(){
        String topicID = intent.getStringExtra("topicID");
        String topicName = intent.getStringExtra("topicName");
        ArrayList<Word> learnedWords = intent.getParcelableArrayListExtra("learnedWords");
    }
}