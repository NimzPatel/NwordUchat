package com.nimesh.uchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nimesh.uchat.adapter.ReportPostAdapter;
import com.nimesh.uchat.model.ReportedPost;

import java.util.ArrayList;
import java.util.List;

public class report extends AppCompatActivity implements ReportPostAdapter.OnPostActionListener{

    private ImageButton backBtn;
    private RecyclerView recyclerView;

    private ReportPostAdapter reportPostAdapter;

    private List<ReportedPost> reportedPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.recyclerView);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(report.this, AdminActivity.class);
            startActivity(intent);
        });

        reportedPosts = new ArrayList<>();
        reportPostAdapter = new ReportPostAdapter(reportedPosts, this);
        recyclerView.setAdapter(reportPostAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(report.this, RecyclerView.VERTICAL, false));
        reportPostAdapter.notifyDataSetChanged();
        loadDB();
    }

    private void loadDB(){
        FirebaseFirestore.getInstance().collection("ReportedPosts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try{
                    if(task.isSuccessful()){
                        reportedPosts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ReportedPost reportedPost = document.toObject(ReportedPost.class);
                            reportedPosts.add(reportedPost);
                        }
                        reportPostAdapter = new ReportPostAdapter(reportedPosts, report.this);
                        recyclerView.setAdapter(reportPostAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(report.this, RecyclerView.VERTICAL, false));
                        reportPostAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(report.this, "Could not get reports", Toast.LENGTH_LONG).show();;
                        startActivity(new Intent(report.this, AdminActivity.class));
                        finish();
                    }
                }catch (Exception e){
                    Toast.makeText(report.this, e.getMessage(), Toast.LENGTH_LONG).show();;
                    startActivity(new Intent(report.this, AdminActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onPostRemoved(ReportedPost post) {
        //Delete from the posted images
        FirebaseFirestore.getInstance().collection("Users")
                .document(post.getReportedBy()).collection("Post Images").whereEqualTo("id", post.getPostID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try{
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // delete the document
                                    document.getReference().delete();
                                    Toast.makeText(report.this, "Post successfully removed", Toast.LENGTH_LONG).show();
                                    deleteReportInstance(post);
                                }
                            }else{
                                Toast.makeText(report.this, "failed to do requested operation", Toast.LENGTH_LONG).show();;
                                startActivity(new Intent(report.this, AdminActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            Toast.makeText(report.this, "failed to do requested operation", Toast.LENGTH_LONG).show();;
                            startActivity(new Intent(report.this, AdminActivity.class));
                            finish();
                        }
                    }
                });
    }

    private void deleteReportInstance(ReportedPost post){
        //Remove all instances of this reported post from the db
        FirebaseFirestore.getInstance().collection("ReportedPosts").whereEqualTo("postID", post.getPostID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try{
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // delete the document
                            document.getReference().delete();
                            Toast.makeText(report.this, "successful to do requested operation", Toast.LENGTH_LONG).show();
                            loadDB();
                        }
                    }else{
                        Toast.makeText(report.this, "failed to do requested operation", Toast.LENGTH_LONG).show();;
                        startActivity(new Intent(report.this, AdminActivity.class));
                        finish();
                    }
                }catch (Exception e){
                    Toast.makeText(report.this, "failed to do requested operation", Toast.LENGTH_LONG).show();;
                    startActivity(new Intent(report.this, AdminActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onPostKept(ReportedPost post) {
        //Remove all instances of this reported post from the db
        deleteReportInstance(post);
    }
}