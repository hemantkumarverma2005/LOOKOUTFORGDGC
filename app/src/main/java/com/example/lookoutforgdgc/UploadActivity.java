package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class UploadActivity extends AppCompatActivity {
    private String username = "";
    private Uri selectedImageUri = null;
    private ImageView imagePreview;
    private ImageButton btnUpload;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back(null);
            }
        });

        imagePreview = findViewById(R.id.imagePreview);
        btnUpload = findViewById(R.id.btnPickImage);

        if (imagePreview != null) {
            imagePreview.setOnClickListener(v -> pickImage());
        }

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

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();
        double matchRatio = mockCheckGDGC(selectedImageUri);
        int points = (int) (10*Math.floor(matchRatio/0.65));

        if (points > 6.5) {
            UserEntry.addScore(this, username, points);
            DocumentReference userRef = db.collection("users").document(currentUserId);
            userRef.get().addOnSuccessListener(snapshot -> {
                        long currentScore = 0;
                        if (snapshot.exists()) {
                            if (snapshot.contains("score") && snapshot.get("score") instanceof Number) {
                                currentScore = snapshot.getLong("score");
                            }
                        } else {
                            Toast.makeText(this, "User document not found! Creating it now.", Toast.LENGTH_SHORT).show();
                        }

                        long newScore = currentScore + points;

                        userRef.update("score", newScore,
                                        "lastMatchRatio", matchRatio)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this, "✅ Match ratio: " +
                                                String.format("%.2f", matchRatio) +
                                                " → +" + points +
                                                " points! (Score: " + newScore + ")", Toast.LENGTH_LONG).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "❌ Failed to update Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "❌ Failed to retrieve user document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


            selectedImageUri = null;
            imagePreview.setImageResource(R.drawable.box_frame6);
        } else {
            Toast.makeText(this, "❌ No GDGC match found. 0 points.", Toast.LENGTH_LONG).show();
        }
    }

    private double mockCheckGDGC(Uri imageUri) {
        try {
            Bitmap inputBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Mat inputMat = new Mat();
            Utils.bitmapToMat(inputBitmap, inputMat);
            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_RGBA2GRAY);

            Bitmap refBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gdgc_logo);
            Mat refMat = new Mat();
            Utils.bitmapToMat(refBitmap, refMat);
            Imgproc.cvtColor(refMat, refMat, Imgproc.COLOR_RGBA2GRAY);

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

            return Math.max(0, Math.min(1, bestScore));

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void back(android.view.View view) {
        long score = UserEntry.getScore(this, username);

        Intent result = new Intent();
        result.putExtra("username", username);
        result.putExtra("score", score);

        setResult(RESULT_OK, result);
        finish();
    }
}
