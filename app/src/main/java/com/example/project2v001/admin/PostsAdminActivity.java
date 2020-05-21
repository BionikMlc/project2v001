package com.example.project2v001.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.admin.adapters.AdminPostAdapter;
import com.example.project2v001.post_module.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsAdminActivity extends AppCompatActivity {
  private List<Post> postsList;
  private RecyclerView postListView;
  private AdminPostAdapter adminPostAdapter;
  private FirebaseFirestore firebaseFirestore;
  private FirebaseAuth auth;
  private boolean isFirstDataLoad = true;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_posts);

    //init firebase variables
    auth = FirebaseAuth.getInstance();
    firebaseFirestore = FirebaseFirestore.getInstance();

    //init UI
    postListView = findViewById(R.id.admin_posts_post_list_view);
    postsList = new ArrayList<>();
    adminPostAdapter = new AdminPostAdapter(postsList);
    postListView.setLayoutManager(new LinearLayoutManager(this));
    postListView.setAdapter(adminPostAdapter);

    if (auth.getCurrentUser() != null) {
      postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);


        }
      });
      String user_id = auth.getUid();
      Query firstQuery = firebaseFirestore.collection("Posts")
              .orderBy("timestamp", Query.Direction.DESCENDING);
      firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
          if (e == null) {
            if (isFirstDataLoad) {
              postsList.clear();
            }
            if (!documentSnapshots.isEmpty()) {
              for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                if (doc.getType() == DocumentChange.Type.ADDED) {

                  String postId = doc.getDocument().getId();
                  Post post = doc.getDocument().toObject(Post.class).withId(postId);
                      postsList.add(post);
                  adminPostAdapter.notifyDataSetChanged();
                  }


                }
              }


            }
          }
        });

      }
    }


  }


