package com.nimesh.uchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nimesh.uchat.adapter.UserAdapter;
import com.nimesh.uchat.fragments.Search;
import com.nimesh.uchat.model.Users;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements UserAdapter.OnUserClicked{

    private ImageButton backButton;
    private SearchView searchView;
    RecyclerView recyclerView;
    UserAdapter adapter;
    private TextView statusTV, reportBtn, reportId;
    private List<Users> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        statusTV = findViewById(R.id.statusTV);
        backButton = findViewById(R.id.backBtn);
        reportBtn = findViewById(R.id.reportText);
        reportId = findViewById(R.id.reportId);
        searchView = findViewById(R.id.searchView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));

        list = new ArrayList<>();
        adapter = new UserAdapter(list, AdminActivity.this);
        recyclerView.setAdapter(adapter);
        loadUserData();

        FirebaseFirestore.getInstance().collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String userEmail = document.getString("email");
                                if (userEmail != null && !userEmail.startsWith("nimeshpatel595@gmail.com")) {
                                    count++;
                                }
                            }
                            statusTV.setText(String.valueOf(count));
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("ReportedPosts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int count = querySnapshot.size();
                            reportId.setText(String.valueOf(count));
                        }
                    }
                });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.putExtra("tab", 4); // pass the tab number as an extra parameter
                startActivity(intent);
            }
        });

        reportBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, report.class);
            startActivity(intent);
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                FirebaseFirestore.getInstance().collection("Users").whereEqualTo("name", query)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {

                                    list.clear();
                                    for (DocumentSnapshot snapshot : task.getResult()) {
                                        if (!snapshot.exists())
                                            return;

                                        Users users = snapshot.toObject(Users.class);
                                        list.add(users);

                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));

                                    adapter = new UserAdapter(list, AdminActivity.this);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                }

                            }
                        });


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals(""))
                    loadUserData();

                return false;
            }
        });
    }

    private void loadUserData() {

        FirebaseFirestore.getInstance().collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null)
                    return;

                if (value == null)
                    return;

                list.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    Users users = snapshot.toObject(Users.class);
                    list.add(users);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));

                adapter = new UserAdapter(list, AdminActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

    
    @Override
    public void onClicked(String uid) {
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        intent.putExtra("uid", uid); // pass the tab number as an extra parameter
        startActivity(intent);
    }
}
