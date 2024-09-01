package com.example.myusersapplication;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    TextView userName, userEmail;
    ImageView avatarImg;
    CardView cardView;
    ImageButton editButton, deleteButton;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.name);
        userEmail = itemView.findViewById(R.id.email);
        avatarImg = itemView.findViewById(R.id.avatar_img);
        cardView = itemView.findViewById(R.id.main_container);
        editButton = itemView.findViewById(R.id.edit_button);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }
}

