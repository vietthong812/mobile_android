package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.WordListResultAdapter;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class KetQuaHocTap extends AppCompatActivity {

    private AppCompatButton btnCancleResult;
    private TextView soLuongDung, soLuongSai, tvDung, tvSai, tvDanhGia;
    private Intent intent;

    private String topicID;
    private String topicName;
    private ArrayList<Word> learnedWords;
    private ArrayList<Word> wrongWordsList;
    private ArrayList<Word> topicWords;
    private QuizViewModel quizViewModel;
    private RecyclerView recyclerViewCorrectWords, recyclerViewWrongWords;
    private WordListResultAdapter wordListResultAdapterTrue, wordListResultAdapterFalse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_result);


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

    protected void init() {
        btnCancleResult = findViewById(R.id.btnCancleResult);
        soLuongDung = findViewById(R.id.soLuongDung);
        soLuongSai = findViewById(R.id.soLuongSai);

        tvDung = findViewById(R.id.tvDung);
        tvSai = findViewById(R.id.tvSai);
        tvDanhGia = findViewById(R.id.tvDanhGia);
        quizViewModel = new QuizViewModel();
        intent = getIntent();

        recyclerViewCorrectWords = findViewById(R.id.viewTraLoiDung);
        recyclerViewWrongWords = findViewById(R.id.viewTraLoiSai);

        recyclerViewCorrectWords.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWrongWords.setLayoutManager(new LinearLayoutManager(this));
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
        getFeekback();
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
        getFeekback();
    }

    private void getResultFlashCard() {
        topicID = intent.getStringExtra("topicID");
        topicName = intent.getStringExtra("topicName");
        learnedWords = intent.getParcelableArrayListExtra("learnedWords");
        wrongWordsList = intent.getParcelableArrayListExtra("wrongWordsList");
        topicWords = intent.getParcelableArrayListExtra("topicWords");

        tvDung.setText("Các từ đã học");
        soLuongDung.setText(learnedWords.size() + "/" + topicWords.size());

        tvSai.setText("Các từ chưa học");
        soLuongSai.setText(wrongWordsList.size() + "/" + topicWords.size());

        displayWrongWords(wrongWordsList);
        displayCorrectWords(learnedWords);
        getFeekback();
    }

    private void displayWrongWords(ArrayList<Word> wrongWordsList) {
        wordListResultAdapterFalse = new WordListResultAdapter(this, wrongWordsList, true);
        recyclerViewWrongWords.setAdapter(wordListResultAdapterFalse);
    }

    private void displayCorrectWords(ArrayList<Word> correctWordsList) {
        wordListResultAdapterTrue = new WordListResultAdapter(this, correctWordsList, false);
        recyclerViewCorrectWords.setAdapter(wordListResultAdapterTrue);
    }

    private void getFeekback() {
        int totalWords = topicWords.size();
        int correctWords = learnedWords.size();
        double percentage = (double) correctWords / totalWords * 100;

        // Set feedback text based on the percentage
        if (percentage == 100) {
            tvDanhGia.setText("Xuất Sắc");
            tvDanhGia.setTextColor(getResources().getColor(R.color.green));
        } else if (percentage >= 50) {
            tvDanhGia.setText("Tốt");
            tvDanhGia.setTextColor(getResources().getColor(R.color.orange));
        } else {
            tvDanhGia.setText("Cần Cố Gắng");
            tvDanhGia.setTextColor(getResources().getColor(R.color.red));
        }
    }
}