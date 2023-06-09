package com.nimesh.uchat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.nimesh.uchat.R;
import com.nimesh.uchat.adapter.UserAdapter;
import com.nimesh.uchat.model.Users;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment implements UserAdapter.OnUserClicked{

    public OnDataPass OnDataPass;
    SearchView searchView;
    RecyclerView recyclerView;
    UserAdapter adapter;
    CollectionReference reference;
    OnDataPass onDataPass;
    private List<Users> list;

    public Search() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDataPass = (OnDataPass) context;

    }

    public OnDataPass getOnDataPass(){
        return onDataPass;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        reference = FirebaseFirestore.getInstance().collection("Users");

        loadUserData();

        searchUser();

    }


    private void searchUser() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                reference.orderBy("search").startAt(query.toLowerCase()).endAt(query.toLowerCase() + "\uf8ff")
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
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapter = new UserAdapter(list, Search.this);
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

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new UserAdapter(list, Search.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void init(View view) {

        searchView = view.findViewById(R.id.searchView);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new UserAdapter(list, Search.this);
        recyclerView.setAdapter(adapter);

    }

    public interface OnDataPass {
        void onChange( String uid);
    }

    @Override
    public void onClicked(String uid) {
        onDataPass.onChange(uid);
    }
}