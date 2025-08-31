package com.example.lookoutforgdgc;

import static com.example.lookoutforgdgc.UserEntry.isUsernameTaken;
import static com.example.lookoutforgdgc.UserEntry.saveUser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lookoutforgdgc.model.User;

public class SignUpActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword, etPassword2;
    private Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // must exist

        etUsername = findViewById(R.id.etUsername);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassword2= findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnRegister);

        if (btnCreateAccount != null) {
            btnCreateAccount.setOnClickListener(v -> createAccount());
        }
    }

    private void createAccount() {
        String u = etUsername != null ? etUsername.getText().toString().trim() : "";
        String e = etEmail != null ? etEmail.getText().toString().trim() : "";
        String p1 = etPassword != null ? etPassword.getText().toString() : "";
        String p2 = etPassword2 != null ? etPassword2.getText().toString() : "";

        if (TextUtils.isEmpty(u)) { toast("Username required"); return; }
        if (!TextUtils.isEmpty(e) && !Patterns.EMAIL_ADDRESS.matcher(e).matches()) { toast("Invalid email"); return; }
        if (TextUtils.isEmpty(p1) || p1.length() < 4) { toast("Password too short"); return; }
        if (!p1.equals(p2)) { toast("Passwords do not match"); return; }

        if (isUsernameTaken(this, u)) { toast("Username already taken"); return; }

        User user = new User(0, u, e, p1, 0L);
        if (saveUser(this, user)) {
            toast("Account created");
            Intent i = new Intent(this, UserCollectionActivity.class);
            i.putExtra("username", u);
            startActivity(i);
            finish();
        } else {
            toast("Failed to create account");
        }
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
