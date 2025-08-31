package com.example.lookoutforgdgc;

import static com.example.lookoutforgdgc.UserEntry.getScore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class UserCollectionActivity extends AppCompatActivity {
    private String username = "";
    private TextView scoreView;

    private final ActivityResultLauncher<Intent> menuLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        Intent intent = getIntent();
        if (intent != null) {
            String u = intent.getStringExtra("username");
            if (u != null) username = u;
        }

        scoreView = findViewById(R.id.tv_score);

        TextView tv = findViewById(R.id.tvUsername);
        if (tv != null) tv.setText(username);

        ImageButton menuBtn = findViewById(R.id.menubtn);
        if (menuBtn != null) {
            menuBtn.setOnClickListener(v -> {
                Intent i2 = new Intent(UserCollectionActivity.this, MenuActivity.class);
                i2.putExtra("username", username);
                menuLauncher.launch(i2);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScore();
    }

    private void refreshScore() {
        if (username != null && !username.isEmpty()) {
            long score = getScore(this, username);
            if (scoreView != null) {
                scoreView.setText(String.valueOf(score));
            }
        }
    }
}