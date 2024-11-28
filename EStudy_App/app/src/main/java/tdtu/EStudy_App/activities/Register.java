package tdtu.EStudy_App.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.utils.ToastUtils;

public class Register extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText createFullname, createBirth, createEmail, createPassword, createRePassword;
    Button btnRegister, btnCancel;
    ProgressBar progressRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initialize();

        // Xử lý chọn ngày tháng năm sinh
        createBirth.setOnClickListener(v -> showDatePicker());

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, SignIn.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            progressRegister.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);

            String fullname = createFullname.getText().toString().trim();
            String birth = createBirth.getText().toString().trim();
            String email = createEmail.getText().toString().trim();
            String password = createPassword.getText().toString();
            String rePassword = createRePassword.getText().toString();

            if (validateInput(fullname, birth, email, password, rePassword)) {
                if (password.equals(rePassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = task.getResult().getUser().getUid();
                            DocumentReference documentReference = db.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullName", fullname);
                            user.put("birthday", birth);
                            user.put("email", email);
                            user.put("avatar", null);
                            documentReference.set(user);
                            ToastUtils.showShortToast(Register.this, "User created");
                            progressRegister.setVisibility(View.GONE);
                            btnRegister.setVisibility(View.VISIBLE);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.e("SignUpError", exception.getMessage());
                            }
                            ToastUtils.showShortToast(Register.this, "Error");
                            progressRegister.setVisibility(View.GONE);
                            btnRegister.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ToastUtils.showShortToast(Register.this, "Password does not match");
                    progressRegister.setVisibility(View.GONE);
                    btnRegister.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initialize() {
        createFullname = findViewById(R.id.createFullname);
        createBirth = findViewById(R.id.createBirth);
        createEmail = findViewById(R.id.createEmail);
        createPassword = findViewById(R.id.createPassword);
        createRePassword = findViewById(R.id.createRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        progressRegister = findViewById(R.id.progressRegister);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            createBirth.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);

        datePickerDialog.show();
    }

    private boolean validateInput(String fullname, String birth, String email, String password, String rePassword) {
        if (fullname.isEmpty()) {
            createFullname.setError("Please fill in the required information");
            return false;
        }
        if (birth.isEmpty()) {
            createBirth.setError("Please select your date of birth");
            return false;
        }
        if (email.isEmpty()) {
            createEmail.setError("Please fill in the required information");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            createEmail.setError("Please enter a valid email address");
            return false;
        }
        if (password.isEmpty()) {
            createPassword.setError("Please fill in the required information");
            return false;
        }
        if (rePassword.isEmpty()) {
            createRePassword.setError("Please fill in the required information");
            return false;
        }
        return true;
    }
}
