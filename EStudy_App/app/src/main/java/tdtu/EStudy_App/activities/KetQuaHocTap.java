package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class KetQuaHocTap extends AppCompatActivity {


    private AppCompatButton btnCancleResult;
    private TextView soLuongDung, soLuongSai, tvDung;
    private Intent intent;

    private String topicID;
    private String topicName;
    private ArrayList<Word> learnedWords;
    private ArrayList<Word> wrongWordsList;
    private ArrayList<Word> topicWords;
    private QuizViewModel quizViewModel;
    private TextView viewSai, viewDung;

    private LinearLayout correctAnswer, wrongAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_result);

        //Initialization
        init();
        btnCancleResult.setOnClickListener(v -> finish());

        String learningType = intent.getStringExtra("learningType");
        switch (learningType) {
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
        quizViewModel.saveLearningResult(topicID, learnedWords, topicWords);
    }

    private void getResultTranslate() {
        topicID = intent.getStringExtra("topicID");
        topicName = intent.getStringExtra("topicName");
        learnedWords = intent.getParcelableArrayListExtra("learnedWords");
        wrongWordsList = intent.getParcelableArrayListExtra("wrongWordsList");
        topicWords = intent.getParcelableArrayListExtra("topicWords");

        soLuongDung.setText(learnedWords.size() + "/" + topicWords.size());
        soLuongSai.setText(wrongWordsList.size() + "/" + topicWords.size());

        displayWrongWords(wrongWordsList);
        displayCorrectWords(learnedWords);
    }

    private void getResultQuiz() {
        topicID = intent.getStringExtra("topicID");
        topicName = intent.getStringExtra("topicName");
        learnedWords = intent.getParcelableArrayListExtra("learnedWords");
        wrongWordsList = intent.getParcelableArrayListExtra("wrongWordsList");
        topicWords = intent.getParcelableArrayListExtra("topicWords");

        soLuongDung.setText(learnedWords.size() + "/" + topicWords.size());
        soLuongSai.setText(wrongWordsList.size() + "/" + topicWords.size());

        displayWrongWords(wrongWordsList);
        displayCorrectWords(learnedWords);
    }

    protected void init(){
        btnCancleResult = findViewById(R.id.btnCancleResult);
        soLuongDung = findViewById(R.id.soLuongDung);
        soLuongSai = findViewById(R.id.soLuongSai);
        correctAnswer = findViewById(R.id.correctAnswer);
        wrongAnswer = findViewById(R.id.wrongAnswer);
        quizViewModel = new QuizViewModel();
        intent = getIntent();
        viewSai = findViewById(R.id.ViewTraLoiSai);
        viewDung = findViewById(R.id.ViewTraLoiDung);
    }

    private void getResultFlashCard(){
        topicID = intent.getStringExtra("topicID");
        topicName = intent.getStringExtra("topicName");
        learnedWords = intent.getParcelableArrayListExtra("learnedWords");
        topicWords = intent.getParcelableArrayListExtra("topicWords");

        wrongAnswer.setVisibility(LinearLayout.GONE);
        tvDung = findViewById(R.id.tvDung);
        tvDung.setText("Đã học");
        soLuongDung.setText(learnedWords.size() + "/" + topicWords.size());
    }

    private void displayWrongWords(ArrayList<Word> wrongWordsList) {
        StringBuilder wrongWordsBuilder = new StringBuilder();
        for (Word word : wrongWordsList) {
            wrongWordsBuilder.append(word.getName()).append(", ");
        }

        // Remove the last comma and space
        if (wrongWordsBuilder.length() > 0) {
            wrongWordsBuilder.setLength(wrongWordsBuilder.length() - 2);
        }

        viewSai.setText(wrongWordsBuilder.toString());
    }

    private void displayCorrectWords(ArrayList<Word> correctWordsList) {
        StringBuilder correctWordsBuilder = new StringBuilder();
        for (Word word : correctWordsList) {
            correctWordsBuilder.append(word.getName()).append(", ");
        }

        // Remove the last comma and space
        if (correctWordsBuilder.length() > 0) {
            correctWordsBuilder.setLength(correctWordsBuilder.length() - 2);
        }

        viewDung.setText(correctWordsBuilder.toString());
    }
}