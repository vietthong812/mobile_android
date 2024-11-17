package tdtu.EStudy_App.activities;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import tdtu.EStudy_App.R;

public class Register extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText createFullname, createUsername, createBirth, createEmail, createPassword, createRePassword;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        intialize();
        btnRegister.setOnClickListener(v -> {
            String fullname = createFullname.getText().toString().trim();
            String username = createUsername.getText().toString().trim();
            String birth = createBirth.getText().toString().trim();
            String email = createEmail.getText().toString().trim();
            String password = createPassword.getText().toString();
            String rePassword = createRePassword.getText().toString();
            if (fullname.isEmpty()){
                createFullname.setError("Please fill in the required information");
            }
            if (username.isEmpty()){
                createUsername.setError("Please fill in the required information");
            }
            if (birth.isEmpty()){
                createBirth.setError("Please fill in the required information");
            }
            if (email.isEmpty()){
                createEmail.setError("Please fill in the required information");
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                createEmail.setError("Please enter a valid email address");
            }
            if (password.isEmpty()){
                createPassword.setError("Please fill in the required information");
            }
            if (rePassword.isEmpty()){
                createRePassword.setError("Please fill in the required information");
            }
            if (!fullname.isEmpty() && !username.isEmpty() && !birth.isEmpty() && !email.isEmpty() && !password.isEmpty() && !rePassword.isEmpty()) {
                if (password.equals(rePassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = db.collection("users").document();
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullname", fullname);
                            user.put("username", username);
                            user.put("birth", birth);
                            user.put("email", email);
                            user.put("password", password);
                            documentReference.set(user);
                            Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                Log.e("SignUpError", errorMessage);
                            }
                            Toast.makeText(Register.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void intialize(){
        createFullname = findViewById(R.id.createFullname);
        createUsername = findViewById(R.id.createUsername);
        createBirth = findViewById(R.id.createBirth);
        createEmail = findViewById(R.id.createEmail);
        createPassword = findViewById(R.id.createPassword);
        createRePassword = findViewById(R.id.createRePassword);
        btnRegister = findViewById(R.id.btnRegister);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
    }
}