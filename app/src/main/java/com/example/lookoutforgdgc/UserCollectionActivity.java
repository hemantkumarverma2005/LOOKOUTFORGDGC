package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserCollectionActivity extends AppCompatActivity {
    private String username = "";
    private TextView scoreView;   // Optional, if you want numeric value
    private ProgressBar scoreBar;

    private final ActivityResultLauncher<Intent> menuLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // Firebase init
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // From intent (username passed earlier)
        Intent intent = getIntent();
        if (intent != null) {
            String u = intent.getStringExtra("username");
            if (u != null) username = u;
        }

        scoreBar = findViewById(R.id.progressBar2); // <-- your ProgressBar in XML

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tv = findViewById(R.id.username);
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
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        DocumentReference userDoc = db.collection("users").document(uid);

        userDoc.get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("score")) {
                Long score = doc.getLong("score");
                if (score != null) {
                    int safeScore = Math.toIntExact(score);

                    // Update ProgressBar
                    if (scoreBar != null) {
                        scoreBar.setMax(100); // or another max value
                        scoreBar.setProgress(Math.min(safeScore, 100));
                    }

                    // Optional: still show numeric score
                    if (scoreView != null) {
                        scoreView.setText(String.valueOf(safeScore));
                    }
                }
            }
        });
    }
}
