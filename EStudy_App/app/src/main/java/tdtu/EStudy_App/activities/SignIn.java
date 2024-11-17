package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import tdtu.EStudy_App.R;

public class SignIn extends AppCompatActivity {
    Button btnRegister,btnSignIn;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    EditText edtEmail, edtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        initialize();
        btnSignIn.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SignIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignIn.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, Register.class);
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
    }
}