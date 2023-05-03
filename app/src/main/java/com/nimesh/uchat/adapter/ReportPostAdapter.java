package com.nimesh.uchat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nimesh.uchat.R;
import com.nimesh.uchat.model.ReportedPost;

import java.util.List;

public class ReportPostAdapter extends RecyclerView.Adapter<ReportPostAdapter.PostViewHolder> {

    private final List<ReportedPost> postList;
    private final OnPostActionListener onPostActionListener;

    public ReportPostAdapter(List<ReportedPost> postList, OnPostActionListener onPostActionListener) {
        this.postList = postList;
        this.onPostActionListener = onPostActionListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reomove_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        ReportedPost post = postList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .placeholder(R.drawable.ic_person)
                .into(holder.imageView);

        holder.removeBtn.setOnClickListener(v -> onPostActionListener.onPostRemoved(post));
        holder.keepBtn.setOnClickListener(v -> onPostActionListener.onPostKept(post));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public interface OnPostActionListener {
        void onPostRemoved(ReportedPost post);

        void onPostKept(ReportedPost post);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final Button removeBtn;
        private final Button keepBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            removeBtn = itemView.findViewById(R.id.remove);
            keepBtn = itemView.findViewById(R.id.keep);
        }
    }
}
