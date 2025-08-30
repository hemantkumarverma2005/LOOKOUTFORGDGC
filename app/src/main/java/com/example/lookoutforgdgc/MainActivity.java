package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void logIn(View view){
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    public void Frame_3btn(View view) {
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.activity_main3, null);
        FrameLayout rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(popupView);

        View closeButton = popupView.findViewById(R.id.close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> rootLayout.removeView(popupView));
        }

        View rulesButton = popupView.findViewById(R.id.imageButton2);
        if (rulesButton != null) {
            rulesButton.setOnClickListener(v -> {
                Toast.makeText(this, "Rules Clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FifthActivity.class);
                startActivity(intent);
            });
        }
        View uploadButton = popupView.findViewById(R.id.imageButton5);
        if (uploadButton != null) {
            uploadButton.setOnClickListener(v -> {
                Toast.makeText(this, "Upload Clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SixthActivity.class);
                startActivity(intent);
            });
        }
        View collectionButton = popupView.findViewById(R.id.imageButton4);
        if (collectionButton != null) {
            collectionButton.setOnClickListener(v -> {
                Toast.makeText(this, "Collection Clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SeventhActivity.class);
                startActivity(intent);
            });
        }
        View leaderboardButton = popupView.findViewById(R.id.imageButton3);
        if (leaderboardButton != null) {
            leaderboardButton.setOnClickListener(v -> {
                Toast.makeText(this, "Leaderboard Clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FourthActivity.class);
                startActivity(intent);
            });
        }
    }
    public void signupbtn(View view){
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }
}