package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.ToastUtils;

public class AddFolder extends AppCompatActivity {
    AppCompatButton btnCancel, btnSave;
    FirebaseFirestore db;
    FirebaseUser user;
    EditText edtFolderName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_folder);
        init();
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        btnSave.setOnClickListener(v -> {
            String folderName = edtFolderName.getText().toString();
            if (folderName.isEmpty()) {
                ToastUtils.showShortToast(this, "Please enter folder name");
                return;
            }
            if (user != null) {
                String userId = user.getUid();
                db.collection("folders").add(new HashMap<String, Object>() {{
                    put("name", folderName);
                    DocumentReference userRef = db.collection("users").document(userId);
                    put("userCreate", userRef);
                }}).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastUtils.showShortToast(this, "Add folder successfully");
                        finish();
                    } else {
                        ToastUtils.showShortToast(this, "Add folder failed");
                    }
                });
            }
        });
    }
    public void init() {
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        edtFolderName = findViewById(R.id.editFolderName);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}