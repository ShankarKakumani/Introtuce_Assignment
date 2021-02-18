package com.shankar.introtuceassignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shankar.customtoast.Toasty;


public class UsersFragment extends Fragment {


    public UsersFragment() {

    }

    DatabaseReference mUsers;
    FirebaseRecyclerAdapter<UserModel,UserView> usersAdapter;
    RecyclerView usersRecycler;

    View mView;
    TextView no_users_found;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_users, container, false);

        no_users_found = mView.findViewById(R.id.no_users_found);
        progressBar = mView.findViewById(R.id.progressBar);

        initializeRecyclerView();

        return  mView;
    }

    private void initializeRecyclerView() {

        usersRecycler = mView.findViewById(R.id.userRecycler);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        usersRecycler.setLayoutManager(linearLayoutManager);
        mUsers = FirebaseDatabase.getInstance().getReference("Users");

        //Token is generated by Firebase and unique to device
        String token = FirebaseInstanceId.getInstance().getToken();
        Query query = mUsers.child(token).orderByChild("position");

        FirebaseRecyclerOptions<UserModel> userOptions = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();


        usersAdapter = new FirebaseRecyclerAdapter<UserModel, UserView>(userOptions) {
            @Override
            protected void onBindViewHolder(@NonNull UserView holder, int i, @NonNull UserModel model) {

                progressBar.setVisibility(View.GONE);
                Log.i("TAG6","Count : "+usersAdapter.getItemCount());


                holder.userNameText.setText(model.getFirstName()+" "+model.getLastName());
                holder.userInfoText.setText(model.getGender()+ " | " + model.getAge()+ " | "+ model.getHomeTown());


                Glide.with(holder.profileImage)
                        .load(model.getImagePath())
                        .placeholder(R.drawable.profile_pic_placeholder)
                        .into(holder.profileImage);
                holder.deleteView.setOnClickListener(v -> mUsers.child(token).child(model.getPhoneNumber()).setValue(null));
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(usersAdapter.getItemCount() == 0) {

                    progressBar.setVisibility(View.GONE);
                    no_users_found.setVisibility(View.VISIBLE);
                }
                else
                {
                    no_users_found.setVisibility(View.GONE);

                }

            }

            @NonNull
            @Override
            public UserView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_users, parent, false);
                return new UserView(itemView);
            }

        };

        usersRecycler.setAdapter(usersAdapter);
        usersAdapter.startListening();

    }
}