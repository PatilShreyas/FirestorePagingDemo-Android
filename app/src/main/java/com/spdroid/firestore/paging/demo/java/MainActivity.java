package com.spdroid.firestore.paging.demo.java;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.spdroid.firestore.paging.demo.R;
import com.spdroid.firestore.paging.demo.java.model.Post;
import com.spdroid.firestore.paging.demo.java.viewholder.PostViewHolder;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFabAdd;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirestorePagingAdapter<Post, PostViewHolder> mAdapter;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private CollectionReference mPostsCollection = mFirestore.collection("posts");
    private Query mQuery = mPostsCollection.orderBy("authorName", Query.Direction.DESCENDING);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mFabAdd = findViewById(R.id.fab_add);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Init mRecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();

        // Add Random Posts
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRandomPosts();
            }
        });

        // Refresh Action on Swipe Refresh Layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });
    }

    private void addRandomPosts() {
        createPosts().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Posts Added!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Refresh Adapter
                    mAdapter.refresh();
                }
            }
        });
    }

    private Task<Void> createPosts() {
        WriteBatch writeBatch = mFirestore.batch();

        for (int i = 0; i < 255; i++) {
            String authorName = "Author " + i;
            String message = "Hi There! This is message " + i + ". Happy Coding!";

            String id = String.format(Locale.getDefault(), "post_%03d", i);
            Post post = new Post(authorName, message);

            writeBatch.set(mPostsCollection.document(id), post);
        }

        return writeBatch.commit();
    }

    private void setupAdapter() {

        // Init Paging Configuration
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build();

        // Init Adapter Configuration
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Post.class)
                .build();

        // Instantiate Paging Adapter
        mAdapter = new FirestorePagingAdapter<Post, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.item_post, parent, false);
                return new PostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int i, @NonNull Post post) {
                // Bind to ViewHolder
                viewHolder.bind(post);
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e("MainActivity", e.getMessage());
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        Toast.makeText(
                                getApplicationContext(),
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                        ).show();

                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }

        };

        // Finally Set the Adapter to mRecyclerView
        mRecyclerView.setAdapter(mAdapter);

    }
}
