package com.example.lookoutforgdgc;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View animatedRectangle = findViewById(R.id.animated_rectangle);
        GradientDrawable gradientDrawable = (GradientDrawable) animatedRectangle.getBackground();

        // 1. Rotation animation
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(animatedRectangle, "rotation", 0f, -360f);
        rotateAnimator.setDuration(2000); // Duration of the rotation

        // 2. Morph to circle animation
        ObjectAnimator cornerAnimator = ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", 16f, 1000f);
        cornerAnimator.setDuration(1000); // Duration of the morphing


        // 3. Scale-up animation to fill the screen
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(animatedRectangle, "scaleX", 1f, 20f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(animatedRectangle, "scaleY", 1f, 20f);

        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.playTogether(scaleXAnimator, scaleYAnimator);
        scaleSet.setDuration(1000); // Duration of the scale-up

        // Combine all animations into a single sequence
        AnimatorSet mainAnimatorSet = new AnimatorSet();
        mainAnimatorSet.playSequentially(rotateAnimator, cornerAnimator, scaleSet);
        mainAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Add a listener to start the next activity when the animation completes
        mainAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the splash activity
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });

        mainAnimatorSet.start();
    }
}