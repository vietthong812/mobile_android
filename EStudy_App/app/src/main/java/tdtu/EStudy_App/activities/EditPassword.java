package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.FirebaseUserSingleton;

public class EditPassword extends AppCompatActivity {
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    ImageButton btnUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_password);

        FirebaseUser user = FirebaseUserSingleton.getInstance();
        edtConfirmPassword = findViewById(R.id.reNewPass);
        edtNewPassword = findViewById(R.id.newPass);
        edtOldPassword = findViewById(R.id.oldPass);

        btnUpdatePassword = findViewById(R.id.btnSavePass);

        btnUpdatePassword.setOnClickListener(view -> {
            String oldPass = edtOldPassword.getText().toString();
            String newPass = edtNewPassword.getText().toString();
            String reNewPass = edtConfirmPassword.getText().toString();
            if (oldPass.isEmpty() || newPass.isEmpty() || reNewPass.isEmpty()) {
                return;
            }
            if (!newPass.equals(reNewPass)) {
                return;
            }
            user.updatePassword(newPass);
        });
    }
}