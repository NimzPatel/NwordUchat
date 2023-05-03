package com.nimesh.uchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ImageButton backButton;


    private EditText userNameEt, nameEt, lastNameEt, staffStudentEt, couserEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        userNameEt = findViewById(R.id.userName);
        nameEt = findViewById(R.id.nameET);
        lastNameEt = findViewById(R.id.lastName);
        staffStudentEt = findViewById(R.id.staffStudent);
        couserEt = findViewById(R.id.couser);
        backButton = findViewById(R.id.backBtn);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                intent.putExtra("tab", 4); // pass the tab number as an extra parameter
                startActivity(intent);
            }
        });

        Button updateBtn = findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEt.getText().toString().trim();
                String name = nameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String staffStudent = staffStudentEt.getText().toString().trim();
                String couser = couserEt.getText().toString().trim();

                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    Map<String, Object> data = new HashMap<>();
                    data.put("userName", userName);
                    data.put("name", name);
                    data.put("LastName", lastName);
                    data.put("StaffOrStudent", staffStudent);
                    data.put("Courser", couser);
                    data.put("search", name.toLowerCase());

                    db.collection("Users").document(userId)
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(UpdateActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateActivity.this, "Failed to update user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Retrieve user data from Firestore
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Set the values to the corresponding EditText fields
                                userNameEt.setText(documentSnapshot.getString("userName"));
                                nameEt.setText(documentSnapshot.getString("name"));
                                lastNameEt.setText(documentSnapshot.getString("LastName"));
                                staffStudentEt.setText(documentSnapshot.getString("StaffOrStudent"));
                                couserEt.setText(documentSnapshot.getString("Courser"));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this, "Failed to retrieve user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
