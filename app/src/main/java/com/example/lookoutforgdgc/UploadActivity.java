package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class UploadActivity extends AppCompatActivity {
    private String username = "";
    private Uri selectedImageUri = null;
    private ImageView imagePreview;
    private ImageButton btnUpload;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (imagePreview != null) imagePreview.setImageURI(uri);
                    Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // ✅ Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back(null); // custom back logic
            }
        });

        // Get username from intent
        Intent i = getIntent();
        if (i != null) {
            String u = i.getStringExtra("username");
            if (u != null) username = u;
        }

        imagePreview = findViewById(R.id.imagePreview);
        btnUpload = findViewById(R.id.btnPickImage);

        // Click image box → pick image
        if (imagePreview != null) {
            imagePreview.setOnClickListener(v -> pickImage());
        }

        // Click upload button → process image
        if (btnUpload != null) {
            btnUpload.setOnClickListener(v -> processImage());
        }

        boolean ok = OpenCVLoader.initDebug();
        if (!ok) {
            Toast.makeText(this, "OpenCV not initialized. Some features disabled.", Toast.LENGTH_LONG).show();
        }
    }

    private void pickImage() {
        try {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open picker", Toast.LENGTH_SHORT).show();
        }
    }

    private void processImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image first!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean relatedToGDGC = mockCheckGDGC(selectedImageUri);

        if (relatedToGDGC) {
            // Add +10 points for this user
            UserEntry.addScore(this, username, 10);
            Toast.makeText(this, "✅ Image related to GDGC. +10 points!", Toast.LENGTH_LONG).show();
            selectedImageUri = null;
            imagePreview.setImageResource(R.drawable.box_frame6);
        } else {
            Toast.makeText(this, "❌ Not related to GDGC. 0 points.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean mockCheckGDGC(Uri imageUri) {
        try {
            // Load uploaded image as Mat
            Bitmap inputBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Mat inputMat = new Mat();
            Utils.bitmapToMat(inputBitmap, inputMat);
            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_RGBA2GRAY);

            // Load reference GDGC logo (from drawable)
            Bitmap refBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gdgc_logo);
            Mat refMat = new Mat();
            Utils.bitmapToMat(refBitmap, refMat);
            Imgproc.cvtColor(refMat, refMat, Imgproc.COLOR_RGBA2GRAY);

            // Resize reference logo to smaller/larger scales & check match
            double bestScore = 0;
            for (double scale = 0.5; scale <= 1.5; scale += 0.25) {
                int newW = (int) (refMat.width() * scale);
                int newH = (int) (refMat.height() * scale);
                if (newW <= 0 || newH <= 0 || newW > inputMat.width() || newH > inputMat.height())
                    continue;

                Mat resizedRef = new Mat();
                Imgproc.resize(refMat, resizedRef, new Size(newW, newH));

                int resultCols = inputMat.cols() - resizedRef.cols() + 1;
                int resultRows = inputMat.rows() - resizedRef.rows() + 1;
                Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

                Imgproc.matchTemplate(inputMat, resizedRef, result, Imgproc.TM_CCOEFF_NORMED);
                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

                if (mmr.maxVal > bestScore) {
                    bestScore = mmr.maxVal;
                }
            }

            // Decide based on threshold
            return bestScore >= 0.5;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // if error, return false
        }
    }


    public void back(View view) {
        long score = UserEntry.getScore(this, username);

        Intent result = new Intent();
        result.putExtra("username", username);
        result.putExtra("score", score);

        setResult(RESULT_OK, result);
        finish();
    }
}
