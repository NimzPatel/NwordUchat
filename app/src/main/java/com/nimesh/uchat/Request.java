package com.nimesh.uchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nimesh.uchat.adapter.RequestAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request extends AppCompatActivity implements RequestAdapter.OnRequestClickListener{

    private RecyclerView recyclerView;
    private ImageButton backButton;
    private List<com.nimesh.uchat.model.Request> requestList;
    private FirebaseUser user;
    private DocumentReference myRef;
    RequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        myRef = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        backButton = findViewById(R.id.backBtn);
        requestList = new ArrayList<>();

        adapter = new RequestAdapter(this,requestList, Request.this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Request.this, MainActivity.class);
                intent.putExtra("tab", 0); // pass the tab number as an extra parameter
                startActivity(intent);
            }
        });


        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).get().addOnCompleteListener(task ->{
            ArrayList<HashMap<String, Object>> requestsMapList = (ArrayList<HashMap<String, Object>>) task.getResult().get("requests");
            if(requestsMapList != null){
               requestList = new ArrayList<>();
                for (HashMap<String, Object> requestMap : requestsMapList) {
                    com.nimesh.uchat.model.Request request = new com.nimesh.uchat.model.Request();
                    request.setToId((String) requestMap.get("toId"));
                    request.setFromId((String) requestMap.get("fromId"));
                    // Add the request to the list
                    requestList.add(request);
                }
                adapter = new RequestAdapter(this, requestList, Request.this);
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAcceptClick(com.nimesh.uchat.model.Request request) {
        //TODO : Let the user follow
        myRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    List<String> followers =  (List<String>) task.getResult().get("followers");
                    followers.add(request.getFromId());

                    requestList = new ArrayList<>();
                    ArrayList<HashMap<String, Object>> requestsMapList = (ArrayList<HashMap<String, Object>>) task.getResult().get("requests");
                    for (HashMap<String, Object> requestMap : requestsMapList) {
                        com.nimesh.uchat.model.Request request = new com.nimesh.uchat.model.Request();
                        request.setToId((String) requestMap.get("toId"));
                        request.setFromId((String) requestMap.get("fromId"));
                        // Add the request to the list
                        requestList.add(request);
                    }

                    Map<String, Object> followersMap = new HashMap<>();
                    followersMap.put("followers", followers);

                    DocumentReference from = FirebaseFirestore.getInstance().collection("Users")
                            .document(request.getFromId());

                    from.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            List<String> followings =  (List<String>) task.getResult().get("following");
                            followings.add(request.getToId());
                            Map<String, Object> followingsMap = new HashMap<>();
                            followingsMap.put("following", followings);
                            from.update(followingsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Removing request
                                        myRef.update(followersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    requestList.remove(request);
                                                    Toast.makeText(Request.this , "Started following you Request", Toast.LENGTH_SHORT).show();

                                                    final Map<String, Object> requestMap = new HashMap<>();
                                                    requestMap.put("requests", requestList);
                                                    myRef.update(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                createNotification(" Accepted your request", request.getFromId());

                                                                Toast.makeText(Request.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                requestList.add(request);
                                                                Toast.makeText(Request.this, "Could reject", Toast.LENGTH_SHORT).show();

                                                            }
                                                            adapter = new RequestAdapter(Request.this, requestList, Request.this);
                                                            recyclerView = findViewById(R.id.recyclerView);
                                                            recyclerView.setLayoutManager(new LinearLayoutManager(Request.this));
                                                            recyclerView.setAdapter(adapter);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRemoveClick(com.nimesh.uchat.model.Request request) {


        requestList.remove(request);
        final Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requests", requestList);

        myRef.update(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    createNotification(" Rejected your request", request.getFromId());
                    Toast.makeText(Request.this, "Rejected Request", Toast.LENGTH_SHORT).show();
                }else{
                    requestList.add(request);
                    Toast.makeText(Request.this, "Could reject", Toast.LENGTH_SHORT).show();

                }
                adapter = new RequestAdapter(Request.this, requestList, Request.this);
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(Request.this));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    void createNotification(String message, String userUID) {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

        String id = reference.document().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", user.getDisplayName() + message);
        map.put("id", id);
        map.put("uid", userUID);


        reference.document(id).set(map);

    }


}