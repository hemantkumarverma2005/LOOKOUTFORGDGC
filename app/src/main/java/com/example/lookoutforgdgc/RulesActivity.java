package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        Intent i = getIntent();
        if (i != null) {
            String u = i.getStringExtra("username");
            if (u != null) username = u;
        }
    }

    public void back(View view){
        Intent i = new Intent(RulesActivity.this, UserCollectionActivity.class);
        i.putExtra("username", username); // ✅ Pass the username back
        startActivity(i);
    }
}
