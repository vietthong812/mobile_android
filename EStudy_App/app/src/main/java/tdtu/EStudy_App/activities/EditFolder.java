package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.firestore.FirebaseFirestore;

import tdtu.EStudy_App.R;

public class EditFolder extends AppCompatActivity {

    AppCompatButton btnCancelEF, btnDoneEF;
    EditText editEF;
    FirebaseFirestore db;
    String folderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_folder);

        init();
        Intent intent = getIntent();
        String nameFolder = intent.getStringExtra("nameFolder");
        folderId = intent.getStringExtra("folderId");

        editEF.setText(nameFolder);
        btnCancelEF.setOnClickListener(view -> finish());
        btnDoneEF.setOnClickListener(view -> saveNameFolder());



    }

    protected void init(){
        btnCancelEF = findViewById(R.id.btnCancelEF);
        btnDoneEF = findViewById(R.id.btnDoneEF);
        editEF = findViewById(R.id.editEF);
        db = FirebaseFirestore.getInstance();
    }

    private void saveNameFolder() {
        String nameFolder = editEF.getText().toString();
        if (nameFolder.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên folder", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("folders").document(folderId).update("name", nameFolder).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedName", nameFolder);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(this, "Cập nhật thư mục thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thư mục thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }


}