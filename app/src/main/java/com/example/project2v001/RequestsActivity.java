package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.adapters.ReqAdapter;
import com.example.project2v001.post_module.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {
  private static final String TAG = "on:";
  private FirebaseAuth auth;
  private RecyclerView postListView;
  private ReqAdapter reqAdapter;
  private List<Post> postsList;
  private FirebaseFirestore firebaseFirestore;
  private boolean firstLoad = true;
  private List<String> reqs;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_requests);

    auth = FirebaseAuth.getInstance();
    postListView = findViewById(R.id.requests_list);
    postsList = new ArrayList<>();
    reqAdapter = new ReqAdapter(postsList);
    postListView.setLayoutManager(new LinearLayoutManager(this));
    postListView.setAdapter(reqAdapter);
    firebaseFirestore = FirebaseFirestore.getInstance();



    final Intent intent = getIntent();
    String postID = null;
    if (intent.hasExtra("postID")) {
      postID = intent.getStringExtra("postID");
    }

    if (auth.getCurrentUser() != null) {
      postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);

          if (isReachedBottom) {
//                        loadPosts();
          }
        }
      });

      final String user_id = auth.getUid();
      final String finalPostID = postID;
      firebaseFirestore.collection("Posts").document(postID)
              .addSnapshotListener(RequestsActivity.this,new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
          if (e == null) {
            if (!documentSnapshot.exists()) {
              postsList.clear();
            }
            Post post = documentSnapshot.toObject(Post.class).withId(finalPostID);
            Log.i(TAG, "onEvent: "+post.getRequests());
            int i = 0;
            while (i < post.getRequests().size() && firstLoad) {
              postsList.add(post);
              ++i;
            }
            firstLoad = false;
            reqAdapter.notifyDataSetChanged();
          }
        }

      });

    }
  }
}
