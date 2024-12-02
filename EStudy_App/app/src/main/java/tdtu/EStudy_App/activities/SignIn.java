package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.ToastUtils;

public class SignIn extends AppCompatActivity {
    Button btnRegister,btnSignIn;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    EditText edtEmail, edtPassword;
    TextView forgotPassword;
    ProgressBar progressLogin;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Kiểm tra người dùng đã đăng nhập trước đó
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);
        if (userEmail != null) {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        initialize();
        btnSignIn.setOnClickListener(v -> {
            if (edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                ToastUtils.showShortToast(SignIn.this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }
            progressLogin.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // Save login state to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userEmail", email);
                    editor.putBoolean("isLoggedIn", true);// Set login state to true
                    editor.putString("password", password);
                    editor.apply();


                    ToastUtils.showShortToast(SignIn.this, "Đăng nhập thành công");
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    progressLogin.setVisibility(View.GONE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    startActivity(intent);
                } else {
                    ToastUtils.showShortToast(SignIn.this, "Email hoặc mật khẩu không chính xác");
                    progressLogin.setVisibility(View.GONE);
                    btnSignIn.setVisibility(View.VISIBLE);
                }
            });
        });
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, Register.class);
            startActivity(intent);
        });
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, ForgotPassword.class);
            startActivity(intent);
        });
    }
    public void initialize(){
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        btnSignIn = findViewById(R.id.btnSignIn);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressLogin = findViewById(R.id.progressLogin);

    }
}