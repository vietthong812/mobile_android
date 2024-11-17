package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import tdtu.EStudy_App.R;

public class SignIn extends AppCompatActivity {
    Button btnSignIn,btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
        });
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, Register.class);
            startActivity(intent);
        });
    }
}