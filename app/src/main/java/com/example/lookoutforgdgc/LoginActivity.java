package com.example.lookoutforgdgc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvSignup   = findViewById(R.id.tvSignup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> doLogin());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void doLogin() {
        String email = etUsername.getText().toString().trim();  // treat username as email
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // fetch user details from Firestore
                            db.collection("users")
                                    .document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(doc -> {
                                        if (doc.exists()) {
                                            String username = doc.getString("username");
                                            Intent i = new Intent(this, UserCollectionActivity.class);
                                            i.putExtra("username", username);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error fetching profile", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    } else {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
