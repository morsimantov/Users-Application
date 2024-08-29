package com.example.myusersapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.example.myusersapplication.mvvm.UsersViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void deleteUser() {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Call the deleteUser method from the ViewModel
                        usersViewModel.deleteUser(user.getId());

                        // Set the result to indicate a user has been deleted
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("isDeleted", true);
                        setResult(Activity.RESULT_OK, returnIntent);

                        // Finish the activity and return to the previous one
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Close the dialog without doing anything
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_vertical, menu);
        return true;
    }

    // this event will enable the back function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_user:
                // Handle the delete user action
                deleteUser();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
