package com.example.lookoutforgdgc;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SixthActivity extends AppCompatActivity {
    private ImageView uploadedImageView;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    uploadedImageView.setImageURI(selectedImageUri); // Display selected image
                    Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main6);

        uploadedImageView = findViewById(R.id.uploaded_image_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Frame6), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onUploadClicked(View view) {
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void onSubmitClicked(View view) {
        if (selectedImageUri != null) {
            Toast.makeText(this, "Image processed successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        finish();
    }
}