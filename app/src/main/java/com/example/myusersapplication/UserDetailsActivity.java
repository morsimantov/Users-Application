package com.example.myusersapplication;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.squareup.picasso.Picasso;

public class UserDetailsActivity extends AppCompatActivity {

    private UsersViewModel usersViewModel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_details);

        user = (User) getIntent().getSerializableExtra("user");

        // Initialize ViewModel with factory
        UsersViewModelFactory factory = new UsersViewModelFactory(getApplication());
        usersViewModel = new ViewModelProvider(this, factory).get(UsersViewModel.class);

        TextView nameTitle = findViewById(R.id.name_title);
        TextView firstName = findViewById(R.id.first_name);
        TextView lastName = findViewById(R.id.last_name);
        TextView email = findViewById(R.id.email);
        ImageView avatarImg = findViewById(R.id.avatar_img);

        nameTitle.setText(user.getFirst_name() + " " + user.getLast_name());
        firstName.setText(user.getFirst_name());
        lastName.setText(user.getLast_name());
        email.setText(user.getEmail());

        String urlImg = user.getAvatar();

        // Check if urlImg is null or empty
        if (urlImg != null && !urlImg.isEmpty()) {
            if (urlImg.startsWith("https")) {
                Picasso.get()
                        .load(urlImg)
                        .placeholder(R.drawable.not_available) // Default drawable resource
                        .into(avatarImg);
            } else {
                avatarImg.setImageURI(Uri.parse(urlImg));
            }
        } else {
            // Set a default image if urlImg is null or empty
            avatarImg.setImageResource(R.drawable.not_available);
        }


    }

}
