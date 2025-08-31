package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LeaderboardActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent i = getIntent();
        if (i != null) {
            String u = i.getStringExtra("username");
            if (u != null) username = u;
        }

        ImageButton menuBtn = findViewById(R.id.backbtn);
        if (menuBtn != null) {
            menuBtn.setOnClickListener(v -> {
                Intent i2 = new Intent(LeaderboardActivity.this, MenuActivity.class);
                i2.putExtra("username", username); // ✅ Pass the username
                startActivity(i2);
            });
        }
    }
}
