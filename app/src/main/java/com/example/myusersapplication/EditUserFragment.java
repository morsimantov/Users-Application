package com.example.myusersapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myusersapplication.models.User;
import com.example.myusersapplication.mvvm.UsersViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

public class EditUserFragment extends DialogFragment {

    private static final String ARG_USER = "user";
    private UsersViewModel usersViewModel;

    public static EditUserFragment newInstance(User user) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user); // Pass the full user object
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);

        TextInputLayout firstNameLayout = view.findViewById(R.id.outlined_first_name);
        TextInputLayout lastNameLayout = view.findViewById(R.id.outlined_last_name);
        TextInputLayout emailLayout = view.findViewById(R.id.outlined_email);
        ImageView avatarImageView = view.findViewById(R.id.edit_avatar);

        TextInputEditText firstNameInput = (TextInputEditText) firstNameLayout.getEditText();
        TextInputEditText lastNameInput = (TextInputEditText) lastNameLayout.getEditText();
        TextInputEditText emailInput = (TextInputEditText) emailLayout.getEditText();

        // Get the User object from the arguments
        User user = (User) getArguments().getSerializable(ARG_USER);

        if (user != null) {
            // Populate the fields with user data
            firstNameInput.setText(user.getFirst_name());
            lastNameInput.setText(user.getLast_name());
            emailInput.setText(user.getEmail());
            String urlImg = user.getAvatar();
            // Load avatar using Picasso
            if (urlImg != null) {
                Picasso.get().load(user.getAvatar()).into(avatarImageView);
            } else {
                // Log or handle the null case, avatarImageView was not found
                Picasso.get().load(user.getAvatar()).placeholder(R.drawable.not_available).into(avatarImageView);
            }
        }

        // Handle save button
        Button submitButton = view.findViewById(R.id.button_save_changes);
        submitButton.setOnClickListener(v -> {
            String firstName = firstNameInput != null ? firstNameInput.getText().toString().trim() : "";
            String lastName = lastNameInput != null ? lastNameInput.getText().toString().trim() : "";
            String email = emailInput != null ? emailInput.getText().toString().trim() : "";

            if (validateInputs(firstName, lastName, email)) {
                // Update the user with new data
                user.setFirst_name(firstName);
                user.setLast_name(lastName);
                user.setEmail(email);

                usersViewModel.updateUser(user.getId(), user.getEmail(), user.getFirst_name(), user.getLast_name(), user.getAvatar())
                        .observe(getViewLifecycleOwner(), result -> {
                            // Handle the result of the update (success or failure)
                        });
            }
        });
    }

    private void loadUserDetails(String userId, TextInputEditText firstNameInput, TextInputEditText lastNameInput, TextInputEditText emailInput, ImageView avatarImageView) {
        // Logic to load user details, including avatar URL
        // Example:
        String avatarUrl = "https://reqres.in/img/faces/" + userId + "-image.jpg";  // Modify this as needed

        // Load avatar image with Picasso
        Picasso.get().load(avatarUrl).placeholder(R.drawable.not_available).into(avatarImageView);

        // Populate other fields (pseudo-code)
        // firstNameInput.setText(user.getFirstName());
        // lastNameInput.setText(user.getLastName());
        // emailInput.setText(user.getEmail());
    }

    // Helper method to validate inputs
    private boolean validateInputs(String firstName, String lastName, String email) {
        // Example validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            // Show error messages or feedback to the user
            return false;
        }
        // Additional validation logic if needed
        return true;
    }
}
