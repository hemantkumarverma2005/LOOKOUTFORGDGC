package com.example.lookoutforgdgc;
import com.example.lookoutforgdgc.model.User;
import static com.example.lookoutforgdgc.UserEntry.checkUser;
import static com.example.lookoutforgdgc.UserEntry.getUser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private User currentUser=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // must exist

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvSignup   = findViewById(R.id.tvSignup);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> doLogin());
        }
        if (tvSignup != null) {
            tvSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        }
    }

    private void doLogin() {
        String u = etUsername != null ? etUsername.getText().toString().trim() : "";
        String p = etPassword != null ? etPassword.getText().toString() : "";
        if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkUser(this, u, p)) {
            Intent i = new Intent(this, UserCollectionActivity.class);
            currentUser=getUser(this,u);
            i.putExtra("username", u);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
