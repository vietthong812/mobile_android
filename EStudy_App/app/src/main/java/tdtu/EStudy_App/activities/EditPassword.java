package tdtu.EStudy_App.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.FirebaseUserSingleton;
import tdtu.EStudy_App.utils.ToastUtils;

public class EditPassword extends AppCompatActivity {
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    ImageButton btnUpdatePassword;
    SharedPreferences sharedPreferences;
    AppCompatButton btnCancleEdtPass;
    FirebaseUser user;
    ProgressBar progressUpdatePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_password);

        init();
        user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedPassword = sharedPreferences.getString("password", "");

        btnUpdatePassword.setOnClickListener(view -> {
            progressUpdatePass.setVisibility(View.VISIBLE);
            btnUpdatePassword.setVisibility(View.GONE);
            String oldPass = edtOldPassword.getText().toString();
            String newPass = edtNewPassword.getText().toString();
            String reNewPass = edtConfirmPassword.getText().toString();


            if (oldPass.isEmpty() || newPass.isEmpty() || reNewPass.isEmpty()) {
                ToastUtils.showShortToast(EditPassword.this, "Vui lòng nhập đầy đủ thông tin");
                btnUpdatePassword.setVisibility(View.VISIBLE);
                progressUpdatePass.setVisibility(View.GONE);
                return;
            }

            if (newPass.length() < 6) {
                ToastUtils.showShortToast(EditPassword.this, "Mật khẩu phải có ít nhất 6 ký tự");
                btnUpdatePassword.setVisibility(View.VISIBLE);
                progressUpdatePass.setVisibility(View.GONE);
                return;
            }

            if (!newPass.equals(reNewPass)) {
                ToastUtils.showShortToast(EditPassword.this, "Mật khẩu mới không khớp");
                btnUpdatePassword.setVisibility(View.VISIBLE);
                progressUpdatePass.setVisibility(View.GONE);
                return;
            }

            if (!oldPass.equals(storedPassword)) {
                ToastUtils.showShortToast(EditPassword.this, "Mât khẩu cũ không đúng");
                progressUpdatePass.setVisibility(View.GONE);
                btnUpdatePassword.setVisibility(View.VISIBLE);
                return;
            }
            reauthentication(oldPass, newPass);

        });

        btnCancleEdtPass.setOnClickListener(view -> {
            finish();
        });
    }

    public void init(){
        btnCancleEdtPass = findViewById(R.id.btnCancelEdtPass);
        edtConfirmPassword = findViewById(R.id.reNewPass);
        edtNewPassword = findViewById(R.id.newPass);
        edtOldPassword = findViewById(R.id.oldPass);
        btnUpdatePassword = findViewById(R.id.btnSavePass);
        progressUpdatePass = findViewById(R.id.progressUpdatePass);
    }

    public void reauthentication(String oldPass, String newPass){
        final String email = user.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { // Kiểm tra nếu task thành công
                                ToastUtils.showShortToast(EditPassword.this, "Cập nhật mật khẩu thành công");
                                sharedPreferences.edit().putString("password", newPass).apply();
                                progressUpdatePass.setVisibility(View.GONE);
                                finish();
                            } else {
                                ToastUtils.showShortToast(EditPassword.this, "Cập nhật mật khẩu thất bại");
                                progressUpdatePass.setVisibility(View.GONE);
                                btnUpdatePassword.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }else {
                    ToastUtils.showShortToast(EditPassword.this, "Task thất bại");
                }
            }
        });
    }
}