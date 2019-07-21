package com.spdroid.firestore.paging.demo.java.viewholder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.spdroid.firestore.paging.demo.R;
import com.spdroid.firestore.paging.demo.java.model.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private TextView authorView;
    private TextView messageView;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        authorView = itemView.findViewById(R.id.post_AuthorName);
        messageView = itemView.findViewById(R.id.post_Message);
    }

    public void bind(Post post) {
        authorView.setText(post.authorName);
        messageView.setText(post.message);
    }

}