package com.example.lookoutforgdgc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // must exist
    }

    public void logIn(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void signupbtn(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
