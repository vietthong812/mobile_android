package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tdtu.EStudy_App.R;

public class KetQuaHocTap extends AppCompatActivity {


    private AppCompatButton btnCancleResult;
    private TextView soLuongDung, soLuongSai, viewTraLoiDung, viewTraLoiSai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_result);

        init();
        btnCancleResult.setOnClickListener(v -> finish());

    }

    protected void init(){
        btnCancleResult = findViewById(R.id.btnCancleResult);
        soLuongDung = findViewById(R.id.soLuongDung);
        soLuongSai = findViewById(R.id.soLuongSai);
        viewTraLoiDung = findViewById(R.id.ViewTraLoiDung);
        viewTraLoiSai = findViewById(R.id.ViewTraLoiSai);


    }
}