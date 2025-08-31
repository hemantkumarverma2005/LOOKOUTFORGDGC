package com.example.lookoutforgdgc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    private String username;

    // Launcher for UploadActivity
    private final ActivityResultLauncher<Intent> uploadLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    data.putExtra("username", username);
                    setResult(RESULT_OK, data);   // ✅ forward it
                    finish();                     // ✅ go back to UserCollection
                }
            });

    public void openUpload(View v) {
        Intent i = new Intent(this, UploadActivity.class);
        i.putExtra("username", username);
        uploadLauncher.launch(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent i = getIntent();
        username = i != null ? i.getStringExtra("username") : "";
    }

    public void backMenu(View view) {
        Intent i1 = new Intent();
        i1.putExtra("username", username);
        setResult(RESULT_OK, i1); // ✅ send back result
        finish();
    }
    public void openRules(View v) {
        Intent i = new Intent(this, RulesActivity.class);
        i.putExtra("username", username); // ✅ Pass the username
        startActivity(i);
    }

    public void openLeaderboard(View v) {
        Intent i = new Intent(this, LeaderboardActivity.class);
        i.putExtra("username", username); // ✅ Pass the username
        startActivity(i);
    }

    public void openCollection(View v) {
        Intent i = new Intent();
        i.putExtra("username", username);
        setResult(RESULT_OK, i);
        finish();
    }
}
