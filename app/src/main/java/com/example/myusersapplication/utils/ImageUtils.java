package com.example.myusersapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.myusersapplication.R;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    // Request code for image picking
    public static final int IMAGE_PICK_CODE = 1000;

    // Method to launch the image chooser
    public static void openImageChooser(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public static void openImageChooser(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // Method to handle the result from the image chooser
    public static String handleImageChooserResult(int requestCode, int resultCode, Intent data, ImageView imageView, Context context) {
        String imagePath = null;
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Load image into ImageView using Picasso
                Picasso.get().load(imageUri).into(imageView);
                // Save the image to internal storage and get the file path
                imagePath = saveImageToInternalStorage((Activity) context, imageUri);
            } else {
                Toast.makeText(context, "Image selection failed.", Toast.LENGTH_SHORT).show();
            }
        }
        return imagePath;
    }

    public static String saveImageToInternalStorage(Activity activity, Uri imageUri) {
        try {
            // Get the bitmap from the image URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            // Create a file in internal storage to save the image
            File filesDir = activity.getFilesDir();
            File imageFile = new File(filesDir, "avatar_" + System.currentTimeMillis() + ".png");

            // Write the bitmap to the file
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            // Return the absolute path of the saved image
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("https")) {
                // Load image from URL using Picasso
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.not_available) // Default drawable resource
                        .into(imageView);
            } else {
                // Load image from local URI
                imageView.setImageURI(Uri.parse(imageUrl));
            }
        } else {
            // Set a default image if imageUrl is null or empty
            imageView.setImageResource(R.drawable.not_available);
        }
    }
}
