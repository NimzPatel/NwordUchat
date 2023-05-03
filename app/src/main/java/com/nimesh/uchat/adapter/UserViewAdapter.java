package com.nimesh.uchat.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nimesh.uchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserViewHolder> {

    public static enum TYPE{
        FOLLOWING, FOLLOWERS, REPORT
    }
    public static enum BUTTON_VISIBILITY{
        VISIBLE, HIDDEN
    }

    private static BUTTON_VISIBILITY button_visibility;
    private TYPE type;
    private List<String> userIDList;
    private CollectionReference tbl_user;

    private OnClickInterface onClickInterface;
    public UserViewAdapter(List<String> userList, TYPE type, BUTTON_VISIBILITY button_visibility, OnClickInterface onClickInterface) {
        this.userIDList = userList;
        this.type = type;
        this.tbl_user = FirebaseFirestore.getInstance().collection("Users");
        this.button_visibility = button_visibility;
        this.onClickInterface = onClickInterface;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userID = userIDList.get(position);

        //Get user
        tbl_user.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    holder.username.setText(task.getResult().get("userName").toString());

                    holder.status.setText(task.getResult().get("StaffOrStudent").toString());


                    switch(type){
                        case FOLLOWERS:
                            holder.button.setText("Remove");
                            break;
                        case FOLLOWING:
                            holder.button.setText("Unfollow");
                            break;
                        case REPORT:
                            holder.button.setText("Remove User");
                            break;
                    }

                    switch (button_visibility){
                        case HIDDEN:
                            holder.button.setVisibility(View.INVISIBLE);
                            break;
                        case VISIBLE:
                            holder.button.setVisibility(View.VISIBLE);
                            break;
                    }

                    holder.button.setOnClickListener(click -> onClickInterface.onClickMethod(userID));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userIDList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userPhoto;
        TextView username, status;
        Button button;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.nameTV);
            status = itemView.findViewById(R.id.statusTV);
            button = itemView.findViewById(R.id.delete);
        }
    }

    public interface OnClickInterface{
        void onClickMethod(String targetId);
    }
}
