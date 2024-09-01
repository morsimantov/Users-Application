package com.example.myusersapplication.utils;

import android.view.View;

public class AnimationUtils {

    // Utility method to add click animation
    public static void addClickAnimation(View view, Runnable onClickAction) {
        view.setOnClickListener(v -> {
            // Simple click animation
            view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(200).withEndAction(() -> {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                // Execute additional action after animation ends
                if (onClickAction != null) {
                    onClickAction.run();
                }
            }).start();
        });
    }
}
