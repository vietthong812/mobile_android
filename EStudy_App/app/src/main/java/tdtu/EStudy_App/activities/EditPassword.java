package tdtu.EStudy_App.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.FirebaseUserSingleton;
import tdtu.EStudy_App.utils.ToastUtils;

public class EditPassword extends AppCompatActivity {
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    ImageButton btnUpdatePassword;
    SharedPreferences sharedPreferences;

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

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedPassword = sharedPreferences.getString("password", "");

        btnUpdatePassword.setOnClickListener(view -> {
            String oldPass = edtOldPassword.getText().toString();
            String newPass = edtNewPassword.getText().toString();
            String reNewPass = edtConfirmPassword.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || reNewPass.isEmpty()) {
                ToastUtils.showShortToast(EditPassword.this, "Please fill in all fields");
                return;
            }

            if (!newPass.equals(reNewPass)) {
                ToastUtils.showShortToast(EditPassword.this, "New passwords do not match");
                return;
            }

            if (!oldPass.equals(storedPassword)) {
                ToastUtils.showShortToast(EditPassword.this, "Old password is incorrect");
                return;
            }

            user.updatePassword(newPass).addOnSuccessListener(aVoid -> {
                ToastUtils.showShortToast(EditPassword.this, "Password updated");
                sharedPreferences.edit().putString("password", newPass).apply();
            }).addOnFailureListener(e -> {
                ToastUtils.showShortToast(EditPassword.this, e.getMessage());
            });
        });
    }
}