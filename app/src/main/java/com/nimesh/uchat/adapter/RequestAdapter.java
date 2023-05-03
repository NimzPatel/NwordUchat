package com.nimesh.uchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nimesh.uchat.R;
import com.nimesh.uchat.model.Request;

import org.w3c.dom.Document;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private List<Request> requestList;
    private OnRequestClickListener onRequestClickListener;
    private CollectionReference tbl_user;


    public RequestAdapter(Context context, List<Request> requestList, OnRequestClickListener onRequestClickListener) {
        this.context = context;
        this.requestList = requestList;
        this.onRequestClickListener = onRequestClickListener;

        tbl_user = FirebaseFirestore.getInstance().collection("Users");
    }

    public interface OnRequestClickListener {
        void onAcceptClick(Request request);
        void onRemoveClick(Request request);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the views
        Request request = requestList.get(position);

        tbl_user.document(request.getFromId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    holder.nameTV.setText((String) task.getResult().get("userName"));
                    holder.statusTV.setText((String) task.getResult().get("StaffOrStudent"));

                }
            }
        });

        holder.accept.setOnClickListener(click ->{
            onRequestClickListener.onAcceptClick(request);
        });

        holder.reject.setOnClickListener(click ->{
            onRequestClickListener.onRemoveClick(request);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // TODO: declare your views here
        CircleImageView profileImage;
        TextView nameTV, statusTV;
        Button accept, reject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            // TODO: find your views here
            profileImage = itemView.findViewById(R.id.profileImage);
            nameTV = itemView.findViewById(R.id.nameTV);
            statusTV = itemView.findViewById(R.id.statusTV);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);

            itemView.findViewById(R.id.accept).setOnClickListener(v -> {
                if (onRequestClickListener != null) {
                    onRequestClickListener.onAcceptClick(requestList.get(getAdapterPosition()));
                }
            });

            itemView.findViewById(R.id.reject).setOnClickListener(v -> {
                if (onRequestClickListener != null) {
                    onRequestClickListener.onRemoveClick(requestList.get(getAdapterPosition()));
                }
            });

        }
    }
}
