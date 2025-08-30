package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lookoutforgdgc.model.User;
import com.google.android.material.textfield.TextInputEditText;


public class SecondActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second_activity_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void back_frame(View view){
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void registerUser(View view) {
        TextInputEditText textusername = findViewById(R.id.imageview01);
        TextInputEditText textemail = findViewById(R.id.email_edit_text);
        TextInputEditText textpassword = findViewById(R.id.password_edit_text);
        TextInputEditText textconfirmpassword = findViewById(R.id.confirm_password_edit_text);
        String username = String.valueOf(textusername.getText()).trim();
        String email = String.valueOf(textemail.getText()).trim();
        String password =String.valueOf(textpassword.getText()).trim();
        String confirmpassword =String.valueOf(textconfirmpassword.getText()).trim();

        if (confirmpassword.equals(password)) {
            User newuser = new User();
            newuser.setUserName(username);
            newuser.setEmail(email);
            newuser.setPassword(password);

            /*****if (userApi != null) {
                    userApi.save(newuser)
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(SecondActivity.this, "Save successful!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SecondActivity.this, "Save failed: " + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                                        Logger.getLogger(SecondActivity.class.getName()).log(Level.SEVERE, "API Error: " + response.code() + " " + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                    Toast.makeText(SecondActivity.this, "Save failed (Network/Other issue)!", Toast.LENGTH_LONG).show();
                                    Logger.getLogger(SecondActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
                                }
                            });
                } else {
                    Toast.makeText(this, "API service not initialized.", Toast.LENGTH_LONG).show();
                    Logger.getLogger(SecondActivity.class.getName()).log(Level.SEVERE, "userApi is null, check initialization.");
                }
             ****/

                textusername.setText("");
                textemail.setText("");
                textpassword.setText("");
                textconfirmpassword.setText("");
        } else {
            Toast.makeText(SecondActivity.this, "PASSWORD DOESN'T MATCH!", Toast.LENGTH_SHORT).show();
        }
    }
}