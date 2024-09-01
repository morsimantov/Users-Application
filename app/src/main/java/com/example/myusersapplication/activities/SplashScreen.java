package com.example.myusersapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myusersapplication.R;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);

        // Initialize local variables
        ImageView logo = findViewById(R.id.logo);
        ImageView title = findViewById(R.id.title);
        TextView textBy = findViewById(R.id.text_by);

        // Initialize animations
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Set animations to elements
        logo.setAnimation(topAnim);
        title.setAnimation(bottomAnim);
        textBy.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, UsersListActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}