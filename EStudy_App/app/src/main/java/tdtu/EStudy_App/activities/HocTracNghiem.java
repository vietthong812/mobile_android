package tdtu.EStudy_App.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import tdtu.EStudy_App.R;

public class HocTracNghiem extends AppCompatActivity {

    private AppCompatButton btnCacleTracNghiem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_tracnghiem);

        btnCacleTracNghiem = findViewById(R.id.btnCancelTracNghiem);
        btnCacleTracNghiem.setOnClickListener(v -> {
            finish();
        });



    }
}
