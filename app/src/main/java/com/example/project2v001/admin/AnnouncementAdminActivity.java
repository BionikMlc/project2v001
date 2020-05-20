package com.example.project2v001.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.LoginActivity;
import com.example.project2v001.R;
import com.example.project2v001.post_module.Post;
import com.example.project2v001.post_module.adapters.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdminActivity extends AppCompatActivity {
  private RecyclerView postListView;
  private List<Post> announcementsList;
  private PostAdapter postAdapter;
  private FloatingActionButton addAnnouncementsButton;
  private Toolbar mainToolBar;

  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_announcment);

    //setup toolBar
    mainToolBar = findViewById(R.id.main_toolbar3);
    setSupportActionBar(mainToolBar);
    getSupportActionBar().setTitle("AcademiaExchange");

    //init firebase variables
    auth = FirebaseAuth.getInstance();
    firebaseFirestore = FirebaseFirestore.getInstance();

    //init UI
    postListView = findViewById(R.id.admin_announcement_list_view);
    postListView.setLayoutManager(new LinearLayoutManager(this));
    announcementsList = new ArrayList<>();
    postAdapter = new PostAdapter(announcementsList);
    postListView.setAdapter(postAdapter);

    addAnnouncementsButton = findViewById(R.id.admin_add_post_float_btn);

    //getting all announcements
    Query firstQuery = firebaseFirestore.collection("Posts")
            .whereEqualTo("user_id",auth.getUid())
            .orderBy("timestamp", Query.Direction.DESCENDING);
    firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e == null) {
          if (!documentSnapshots.isEmpty()) {

            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

              if (doc.getType() == DocumentChange.Type.ADDED) {

                String postId = doc.getDocument().getId();
                Post post = doc.getDocument().toObject(Post.class).withId(postId);
                    announcementsList.add(post);

                  postAdapter.notifyDataSetChanged();
              }
            }



          }
        }
      }

    });

    //listeners
    addAnnouncementsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AnnouncementAdminActivity.this, AddAnnouncemenAdmintActivity.class));
      }
    });

  }

  //setup toolbar
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.admin_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_logout:
        logout();
        return true;

      default:
        return false;

    }

  }

  private void sendToLogin() {
    startActivity(new Intent(AnnouncementAdminActivity.this, LoginActivity.class));
    finish();
  }

  private void logout() {
    auth.signOut();
    sendToLogin();

  }
}
