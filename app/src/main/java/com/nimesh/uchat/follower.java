package com.nimesh.uchat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nimesh.uchat.adapter.UserViewAdapter;
import com.nimesh.uchat.fragments.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class follower extends AppCompatActivity implements UserViewAdapter.OnClickInterface{

    private RecyclerView recyclerView;
    private UserViewAdapter userViewAdapter;
    private List<String> followersList;
    private String targetUserId;
    private ImageButton backButton;

    UserViewAdapter.BUTTON_VISIBILITY button_visibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_un_follow);

        try{
            if(getIntent().hasExtra("follower")){
                followersList = getIntent().getStringArrayListExtra("follower");

                button_visibility = UserViewAdapter.BUTTON_VISIBILITY.HIDDEN;
                targetUserId = getIntent().getStringExtra("userID");
                if(targetUserId.equals(FirebaseAuth.getInstance().getUid())){
                    button_visibility = UserViewAdapter.BUTTON_VISIBILITY.VISIBLE;
                }

                userViewAdapter = new UserViewAdapter(followersList, UserViewAdapter.TYPE.FOLLOWERS, button_visibility, this);
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setAdapter(userViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                userViewAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(follower.this, Profile.class));
            finish();
        }

        backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(follower.this, MainActivity.class);
                intent.putExtra("tab", 4); // pass the tab number as an extra parameter
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClickMethod(String targetUserId) {
        //remove from current user followers
        followersList.remove(targetUserId);

        Map<String, Object> followersMap = new HashMap<>();
        followersMap.put("followers", followersList);
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).update(followersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    userViewAdapter = new UserViewAdapter(followersList, UserViewAdapter.TYPE.FOLLOWERS, button_visibility, follower.this);
                    recyclerView.setAdapter(userViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(follower.this, RecyclerView.VERTICAL, false));
                    userViewAdapter.notifyDataSetChanged();
                }
            }
        });

        //remove from target user followings
        FirebaseFirestore.getInstance().collection("Users").document(targetUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    List<String> targetUserFollowing = (List<String>) task.getResult().get("following");
                    targetUserFollowing.remove(FirebaseAuth.getInstance().getUid());

                    Map<String, Object> targetFollowingMap = new HashMap<>();
                    targetFollowingMap.put("following", targetUserFollowing);

                    FirebaseFirestore.getInstance().collection("Users").document(targetUserId).update(targetFollowingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(follower.this, "User successfully removed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}