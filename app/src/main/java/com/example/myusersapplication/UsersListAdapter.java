package com.example.myusersapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.activities.UserDetailsActivity;
import com.example.myusersapplication.models.User;
import com.example.myusersapplication.utils.ImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> usersList;
    // Use FragmentActivity for access to FragmentManager
    private final FragmentActivity activity;
    private final UserActionListener actionListener;

    public UsersListAdapter(List<User> usersList, FragmentActivity activity, UserActionListener actionListener) {
        this.usersList = usersList;
        this.activity = activity;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        // Get the current user based on position
        User user = usersList.get(position);

        // Get the current user based on position
        holder.userName.setText(user.getFirst_name() + " " + user.getLast_name());
        holder.userEmail.setText(user.getEmail());
        String urlImg = user.getAvatar();

        // Load avatar image
        ImageUtils.loadImage(holder.avatarImg, urlImg);

        // Handle edit button click (already existing code)
        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditUser(user);
            }
        });

        holder.deleteButton.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(activity)
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("DELETE", (dialogInterface, i) -> {
                            // Start the fade-out animation
                            ViewPropertyAnimator animator = holder.itemView.animate();
                            animator.alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    // Remove the user from the list and notify the adapter
                                    int position = holder.getBindingAdapterPosition();
                                    usersList.remove(position);
                                    notifyItemRemoved(position);
                                    if (actionListener != null) {
                                        actionListener.onDeleteUser(user);
                                    }
                                }
                            }).start();
                        })
                        .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show()
        );

        holder.cardView.setOnClickListener(view -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Get the selected user
                User selectedUser = usersList.get(adapterPosition);

                // Create an Intent to start UserDetailsActivity
                Intent intent = new Intent(activity, UserDetailsActivity.class);

                // Pass the selected user's details to the UserDetailsActivity
                intent.putExtra("user", selectedUser);

                // Set up the transition for shared elements
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(activity, holder.avatarImg, ViewCompat.getTransitionName(holder.avatarImg));

                // Start the UserDetailsActivity for result
                activity.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (usersList == null) {
            return 0;
        }
        return usersList.size();
    }

    public void updateList(List<User> users) {
        this.usersList = users;
        notifyDataSetChanged();
    }
}