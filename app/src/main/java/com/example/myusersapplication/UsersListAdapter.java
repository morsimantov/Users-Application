package com.example.myusersapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myusersapplication.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> usersList;
//    private SelectUserListener listener;
    private final FragmentActivity activity;  // Use FragmentActivity for access to FragmentManager


    public UsersListAdapter(List<User> usersList, FragmentActivity activity) {
        this.usersList = usersList;
        this.activity = activity;
//        this.listener = listener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UsersViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.userName.setText(usersList.get(position).getFirst_name() + " " + usersList.get(position).getLast_name());
        holder.userEmail.setText(usersList.get(position).getEmail());
        String urlImg = usersList.get(position).getAvatar();
        Picasso.get()
                    .load(urlImg)
                    .placeholder(R.drawable.not_available) // Default drawable resource
                    .into(holder.avatarImg);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
//                    listener.onSelectUser(usersList.get(adapterPosition));
                }
            }
        });

// Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            EditUserFragment editUserFragment = EditUserFragment.newInstance(user);
            editUserFragment.show(activity.getSupportFragmentManager(), "editUserFragment");
        });

//        // Handle delete button click
//        holder.deleteButton.setOnClickListener(v -> {
//            // Implement deletion logic (call ViewModel to delete user)
//            activity.getUsersViewModel().deleteUser(user.getId());
//        });
    }

    @Override
    public int getItemCount() {
        if (usersList == null) {
            return 0;
        }
        return usersList.size();
    }

    public void updateList(List<User> users) {
        usersList = users;
        notifyDataSetChanged();
    }
}