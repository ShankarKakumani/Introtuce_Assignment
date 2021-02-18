package com.shankar.introtuceassignment.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.shankar.introtuceassignment.R;

public class UserView extends RecyclerView.ViewHolder {


    public ImageView deleteView;
    public TextView userNameText, userInfoText;
    public CircularImageView profileImage;
    public UserView(@NonNull View itemView) {
        super(itemView);
        deleteView = itemView.findViewById(R.id.deleteView);
        userNameText = itemView.findViewById(R.id.userNameText);
        userInfoText = itemView.findViewById(R.id.userInfoText);
        profileImage = itemView.findViewById(R.id.profileImage);
    }
}
