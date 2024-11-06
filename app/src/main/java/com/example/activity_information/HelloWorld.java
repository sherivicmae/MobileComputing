package com.example.activity_information;


import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.animation.ValueAnimator; // Import ValueAnimator



public class HelloWorld extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hello_world);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.helloText);
        ImageView ribbon = findViewById(R.id.imageRibbon);

        // Slide
        textView.setTranslationX(-1000f); // Start off-screen
        textView.animate().translationX(0f).setDuration(1000); // Slide in over 1 second

        //Fade In
        textView.setAlpha(0f); // Initially invisible
        textView.animate().alpha(1f).setDuration(1000); // Fade in over 1 second

        //pulse
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.2f, 1f);

        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.5f);
        pulseAnimator.setDuration(500); // Duration of one pulse cycle
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE); // Pulse in and out
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE); // Infinite repeat

        pulseAnimator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            textView.setScaleX(scale);
            textView.setScaleY(scale);
        });

        pulseAnimator.start(); // Start

        //bounce
        ObjectAnimator bounce = ObjectAnimator.ofFloat(textView, "translationY", 0, -30, 0);
        bounce.setDuration(500);
        bounce.setRepeatCount(2); // Number of bounces
        bounce.start();

        //rotate
        ObjectAnimator rotate = ObjectAnimator.ofFloat(textView, "rotation", 0f, 360f);
        rotate.setDuration(1000); // Rotate over 1 second
        rotate.start();

        ribbon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentHome = new Intent(getApplicationContext(), Home.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                finish();
            }
        });

    }
}