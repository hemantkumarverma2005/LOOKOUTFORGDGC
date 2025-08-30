package com.example.lookoutforgdgc;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.ORB;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SixthActivity extends AppCompatActivity {
    private static final String TAG = "SixthActivity";
    private ImageView uploadedImageView;
    private Uri selectedImageUri;
    private Mat gdgcLogoMat;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    uploadedImageView.setImageURI(selectedImageUri);
                    // Process the image after the user has selected it
                    try {
                        processImage(selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to process image.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main6);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Frame6), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uploadedImageView = findViewById(R.id.uploaded_image_view);

        // This is a crucial step for OpenCV: loading the native library
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded successfully!");
            // Load the GDGC logo from resources as a Mat object
            try {
                gdgcLogoMat = Utils.loadResource(this, R.drawable.gdgc_logo, Imgcodecs.IMREAD_COLOR);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load logo.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "OpenCV failed to load!");
            Toast.makeText(this, "OpenCV failed to load!", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectImage(View view) {
        // Launch the photo picker to select an image from the gallery
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void processImage(Uri imageUri) throws IOException {
        // Use a try-catch block to handle potential IOExceptions
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            Bitmap uploadedBitmap = BitmapFactory.decodeStream(inputStream);
            Mat uploadedImageMat = new Mat();
            Utils.bitmapToMat(uploadedBitmap, uploadedImageMat);

            // Create ORB detector
            ORB orb = ORB.create();

            // Create keypoint and descriptor objects for both images
            MatOfKeyPoint keypointsLogo = new MatOfKeyPoint();
            Mat descriptorsLogo = new Mat();

            MatOfKeyPoint keypointsUploaded = new MatOfKeyPoint();
            Mat descriptorsUploaded = new Mat();

            // Detect keypoints and compute descriptors for both images
            orb.detectAndCompute(gdgcLogoMat, new Mat(), keypointsLogo, descriptorsLogo);
            orb.detectAndCompute(uploadedImageMat, new Mat(), keypointsUploaded, descriptorsUploaded);

            // Create a BFMatcher object
            DescriptorMatcher matches = BFMatcher.create(BFMatcher.BRUTEFORCE_HAMMING);

            // CRITICAL FIX: The matchesMat object was not initialized.
            // It needs to be a new MatOfDMatch object to store the results.
            MatOfDMatch matchesMat = new MatOfDMatch();
            matches.match(descriptorsLogo, descriptorsUploaded, matchesMat);

            List<DMatch> matchesList = matchesMat.toList();
            List<DMatch> goodMatches = new ArrayList<>();
            float max_dist = 0;
            float min_dist = 100;

            for (int i = 0; i < descriptorsLogo.rows(); i++) {
                double dist = matchesList.get(i).distance;
                if (dist < min_dist) min_dist = (float) dist;
                if (dist > max_dist) max_dist = (float) dist;
            }

            for (int i = 0; i < descriptorsLogo.rows(); i++) {
                if (matchesList.get(i).distance <= 2 * min_dist) {
                    goodMatches.add(matchesList.get(i));
                }
            }

            // Calculate score based on the number of good matches
            int points = goodMatches.size();
            String message = "Image processed successfully! You got " + points + " points.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            uploadedImageMat.release();
            keypointsUploaded.release();
            descriptorsUploaded.release();

            // CRITICAL FIX: The release() method should be called on the MatOfDMatch object, not the DescriptorMatcher.
            matchesMat.release();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release native resources
        if (gdgcLogoMat != null) {
            gdgcLogoMat.release();
        }
    }
}
