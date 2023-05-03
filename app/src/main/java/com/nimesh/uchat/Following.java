package com.nimesh.uchat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class Following extends AppCompatActivity implements UserViewAdapter.OnClickInterface{

    private RecyclerView recyclerView;
    private TextView pageType;
    private UserViewAdapter userViewAdapter;
    private List<String> followingList;
    private String targetUserId;
    private UserViewAdapter.BUTTON_VISIBILITY button_visibility;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_un_follow);

        try{
            if(getIntent().hasExtra("following")){
                followingList = getIntent().getStringArrayListExtra("following");

                button_visibility = UserViewAdapter.BUTTON_VISIBILITY.HIDDEN;
                targetUserId = getIntent().getStringExtra("userID");

                if(targetUserId.equals(FirebaseAuth.getInstance().getUid())){
                    button_visibility = UserViewAdapter.BUTTON_VISIBILITY.VISIBLE;
                }

                userViewAdapter = new UserViewAdapter(followingList, UserViewAdapter.TYPE.FOLLOWING, button_visibility, this);
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setAdapter(userViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                userViewAdapter.notifyDataSetChanged();

                pageType = findViewById(R.id.pageType);
                pageType.setText("Following ");
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Following.this, Profile.class));
            finish();
        }



        backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Following.this, MainActivity.class);
                intent.putExtra("tab", 4); // pass the tab number as an extra parameter
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClickMethod(String targetUserId) {
        //remove from current user following
        followingList.remove(targetUserId);

        Map<String, Object> followingMap = new HashMap<>();
        followingMap.put("following", followingList);
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).update(followingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    userViewAdapter = new UserViewAdapter(followingList, UserViewAdapter.TYPE.FOLLOWING, button_visibility, Following.this);
                    recyclerView.setAdapter(userViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Following.this, RecyclerView.VERTICAL, false));
                    userViewAdapter.notifyDataSetChanged();
                }
            }
        });


        //remove from target user followers
        FirebaseFirestore.getInstance().collection("Users").document(targetUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    List<String> targetUserFollowers = (List<String>) task.getResult().get("followers");
                    targetUserFollowers.remove(FirebaseAuth.getInstance().getUid());

                    Map<String, Object> targetUserFollowingMap = new HashMap<>();
                    targetUserFollowingMap.put("followers", targetUserFollowers);

                    FirebaseFirestore.getInstance().collection("Users").document(targetUserId).update(targetUserFollowingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Following.this, "User successfully unfollowed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}
