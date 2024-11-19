package tdtu.EStudy_App.activities;

import android.content.Intent;
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
import tdtu.EStudy_App.utils.ToastUtils;

public class Register extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText createFullname, createUsername, createBirth, createEmail, createPassword, createRePassword;
    Button btnRegister,btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initialize();
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, SignIn.class);
            startActivity(intent);
            finish();
        });
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
                            String userId = task.getResult().getUser().getUid();
                            //tao document trong collection users
                            DocumentReference documentReference = db.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullname", fullname);
                            user.put("username", username);
                            user.put("birth", birth);
                            user.put("email", email);
                            //user.put("password", password);
                            documentReference.set(user);
                            ToastUtils.showShortToast(Register.this, "User created");
                            finish();
                        }
                        else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                Log.e("SignUpError", errorMessage);
                            }
                            ToastUtils.showShortToast(Register.this, "Error");
                        }
                    });
                } else {
                    ToastUtils.showShortToast(Register.this, "Password does not match");
                }
            }
        });
    }
    public void initialize(){
        createFullname = findViewById(R.id.createFullname);
        createUsername = findViewById(R.id.createUsername);
        createBirth = findViewById(R.id.createBirth);
        createEmail = findViewById(R.id.createEmail);
        createPassword = findViewById(R.id.createPassword);
        createRePassword = findViewById(R.id.createRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
    }
}