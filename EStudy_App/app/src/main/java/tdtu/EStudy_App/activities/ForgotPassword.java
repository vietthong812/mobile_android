package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.ToastUtils;

public class ForgotPassword extends AppCompatActivity {
    Button btnSend, btnCancel;
    EditText resetEmail;
    FirebaseAuth AUTH = FirebaseAuth.getInstance();
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        initialize();
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, SignIn.class);
            startActivity(intent);
            finish();
        });
        btnSend.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
            String email = resetEmail.getText().toString().trim();
            if (email.isEmpty()) {
                resetEmail.setError("Please fill in the required information");
                progressBar.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
            } else if (!email.matches(String.valueOf(android.util.Patterns.EMAIL_ADDRESS))) {
                resetEmail.setError("Invalid email address");
                progressBar.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
            } else {
                resetPassword(email);
            }
        });

    }
    public void initialize(){
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);
        resetEmail = findViewById(R.id.resetEmail);
        progressBar = findViewById(R.id.progressForgot);
    }
    protected void resetPassword(String email) {
        AUTH.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastUtils.showShortToast(ForgotPassword.this, "Password reset email sent");
                        progressBar.setVisibility(View.GONE);
                        btnSend.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtils.showShortToast(ForgotPassword.this, "Failed to send password reset email");
                        progressBar.setVisibility(View.GONE);
                        btnSend.setVisibility(View.VISIBLE);
                    }
                });
    }

}