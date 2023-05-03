package com.nimesh.uchat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nimesh.uchat.R;
import com.nimesh.uchat.Request;
import com.nimesh.uchat.adapter.HomeAdapter;
import com.nimesh.uchat.adapter.StoriesAdapter;
import com.nimesh.uchat.chat.ChatUsersActivity;
import com.nimesh.uchat.model.HomeModel;
import com.nimesh.uchat.model.StoriesModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    private ImageButton requestBtn;
    HomeAdapter adapter;
    RecyclerView storiesRecyclerView;
    StoriesAdapter storiesAdapter;
    List<StoriesModel> storiesModelList;
    private RecyclerView recyclerView;
    private List<HomeModel> list;
    private FirebaseUser user;
    Activity activity;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);





    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        init(view);

        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getActivity());
        recyclerView.setAdapter(adapter);
        requestBtn = view.findViewById(R.id.requestBtn);

        requestBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Request.class);
            startActivity(intent);
        });

        loadDataFromFirestore();

        adapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid()); // unlike
                } else {
                    likeList.add(user.getUid()); // like
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", likeList);

                reference.update(map);

            }

            @Override
            public void setCommentCount(final TextView textView) {

                commentCount.observe((LifecycleOwner) activity, integer -> {

                    assert commentCount.getValue() != null;

                    if (commentCount.getValue() == 0) {
                        textView.setVisibility(View.GONE);
                    } else
                        textView.setVisibility(View.VISIBLE);

                    StringBuilder builder = new StringBuilder();

                    textView.setText(builder);
//                    textView.setText("See all " + commentCount.getValue() + " comments");

                });

            }
        });

        view.findViewById(R.id.sendBtn).setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ChatUsersActivity.class);
            startActivity(intent);

        });

    }

    private void init(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView);
        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView
                .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        storiesModelList = new ArrayList<>();
        storiesModelList.add(new StoriesModel("", "", "", "", ""));
        storiesAdapter = new StoriesAdapter(storiesModelList, getActivity());
        storiesRecyclerView.setAdapter(storiesAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    private void loadDataFromFirestore() {

        final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");

        collectionReference.document(user.getUid()).collection("Post Images")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.d("Error: ", error.getMessage());
                        return;
                    }

                    if (value == null)
                        return;

                    list.clear();

                    for (final QueryDocumentSnapshot snapshot1 : value) {

                        if (!snapshot1.exists())
                            return;

                        HomeModel model = snapshot1.toObject(HomeModel.class);

                        list.add(new HomeModel(
                                model.getName(),
                                model.getProfileImage(),
                                model.getImageUrl(),
                                model.getUid(),
                                model.getDescription(),
                                model.getId(),
                                model.getTimestamp(),
                                model.getLikes()));

                        snapshot1.getReference().collection("Comments").get()
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {

                                        Map<String, Object> map = new HashMap<>();
                                        for (QueryDocumentSnapshot commentSnapshot : task.getResult()) {
                                            map = commentSnapshot.getData();
                                        }

                                        commentCount.setValue(map.size());

                                    }

                                });

                    }
                    adapter.notifyDataSetChanged();

                });

        final DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        reference.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.d("Error: ", error.getMessage());
                return;
            }

            if (value == null)
                return;

            List<String> uidList = (List<String>) value.get("following");

            if (uidList == null || uidList.isEmpty())
                return;

            for (String uid : uidList) {
                collectionReference.document(uid).collection("Post Images")
                        .addSnapshotListener((value11, error11) -> {

                            if (error11 != null) {
                                Log.d("Error: ", error11.getMessage());
                            }

                            if (value11 == null)
                                return;

                            for (final QueryDocumentSnapshot snapshot1 : value11) {

                                if (!snapshot1.exists())
                                    return;

                                HomeModel model = snapshot1.toObject(HomeModel.class);

                                list.add(new HomeModel(
                                        model.getName(),
                                        model.getProfileImage(),
                                        model.getImageUrl(),
                                        model.getUid(),
                                        model.getDescription(),
                                        model.getId(),
                                        model.getTimestamp(),
                                        model.getLikes()));

                                snapshot1.getReference().collection("Comments").get()
                                        .addOnCompleteListener(task -> {

                                            if (task.isSuccessful()) {

                                                Map<String, Object> map = new HashMap<>();
                                                for (QueryDocumentSnapshot commentSnapshot : task.getResult()) {
                                                    map = commentSnapshot.getData();
                                                }

                                                commentCount.setValue(map.size());

                                            }

                                        });

                            }
                            adapter.notifyDataSetChanged();

                        });
            }

            // todo: fetch stories
            loadStories(uidList);

        });
    }

    void loadStories(List<String> followingList) {

        Query query = FirebaseFirestore.getInstance().collection("Stories");
        query.whereIn("uid", followingList).addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.d("Error: ", error.getMessage());
            }

            if (value == null)
                return;

            for (QueryDocumentSnapshot snapshot : value) {

                if (!value.isEmpty()) {
                    StoriesModel model = snapshot.toObject(StoriesModel.class);
                    storiesModelList.add(model);
                }

            }
            storiesAdapter.notifyDataSetChanged();

        });

    }

}