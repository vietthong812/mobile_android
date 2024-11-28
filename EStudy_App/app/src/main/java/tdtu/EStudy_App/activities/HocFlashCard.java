package tdtu.EStudy_App.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import tdtu.EStudy_App.R;

public class HocFlashCard extends AppCompatActivity {

    private AppCompatButton btnCancleFC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_flashcard);

        btnCancleFC = findViewById(R.id.btnCancelFC);
        btnCancleFC.setOnClickListener(v -> {
            finish();
        });



    }
}
