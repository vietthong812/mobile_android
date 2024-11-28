package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import tdtu.EStudy_App.R;

public class GetRank extends AppCompatActivity {

    private AppCompatButton btnCancleRank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ranking);

        btnCancleRank = findViewById(R.id.btnCancelRank);
        btnCancleRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
