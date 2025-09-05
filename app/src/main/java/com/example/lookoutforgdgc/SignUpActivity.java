package com.example.lookoutforgdgc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword, etPassword2;
    private Button btnCreateAccount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCreateAccount.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String u = etUsername.getText().toString().trim();
        String e = etEmail.getText().toString().trim();
        String p1 = etPassword.getText().toString();
        String p2 = etPassword2.getText().toString();

        if (TextUtils.isEmpty(u) || TextUtils.isEmpty(e) || TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
            toast("All fields are required.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            toast("Invalid email address.");
            return;
        }

        if (p1.length() < 6) {
            toast("Password must be at least 6 characters.");
            return;
        }

        if (!p1.equals(p2)) {
            toast("Passwords do not match.");
            return;
        }

        // Firebase Authentication
        mAuth.createUserWithEmailAndPassword(e, p1)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        // Save extra user details in Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("username", u);
                        userMap.put("email", e);
                        userMap.put("score", 0);

                        db.collection("users").document(uid).set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    toast("Account created successfully. Please log in.");

                                    // Redirect to LoginActivity after successful registration
                                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish(); // Close SignUpActivity
                                })
                                .addOnFailureListener(e1 -> {
                                    toast("Failed to save user details: " + e1.getMessage());
                                });
                    } else {
                        // If auth fails, handle the exception
                        String errorMessage = "Authentication failed.";
                        if (task.getException() != null) {
                            errorMessage = "Authentication failed: " + task.getException().getMessage();
                        }
                        toast(errorMessage);
                    }
                });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void back_frame(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}