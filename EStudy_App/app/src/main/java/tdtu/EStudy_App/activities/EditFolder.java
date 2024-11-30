package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.exifinterface.media.ExifInterface;

import tdtu.EStudy_App.R;

public class EditFolder extends AppCompatActivity {

    AppCompatButton btnCancelEF, btnDoneEF;
    EditText editEF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_folder);

        init();
        btnCancelEF.setOnClickListener(view -> finish());
        btnDoneEF.setOnClickListener(view -> saveNameFolder());


    }

    protected void init(){
        btnCancelEF = findViewById(R.id.btnCancelEF);
        btnDoneEF = findViewById(R.id.btnDoneEF);
        editEF = findViewById(R.id.editEF);
    }

    private void saveNameFolder(){
        //Lưu tên moiws
    }


}