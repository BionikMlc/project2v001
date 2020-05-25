package com.example.project2v001.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.admin.adapters.User;
import com.example.project2v001.admin.adapters.UsersAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersAdminActivity extends AppCompatActivity {
  private static final String TAG = "";
  private RecyclerView userListView;
  private List<User> usersList;
  private UsersAdapter userAdapter;
  private FirebaseFirestore firebaseFirestore;
  private boolean isFirstLoad = true;

// ...

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_users);


//    firebaseFunctions.getHttpsCallable();
    usersList = new ArrayList<>();
    userAdapter = new UsersAdapter(usersList);
    userListView = findViewById(R.id.users_activity_users_list);
    userListView.setLayoutManager(new LinearLayoutManager(this));
    userListView.setAdapter(userAdapter);

    firebaseFirestore = FirebaseFirestore.getInstance();

    Log.i(TAG, "onCreate: settings" + firebaseFirestore.getFirestoreSettings().isPersistenceEnabled());



    firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e == null) {
          if (!documentSnapshots.isEmpty()) {
            if (isFirstLoad) {
              usersList.clear();
            }

            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

              if (doc.getType() == DocumentChange.Type.ADDED) {

                User user = doc.getDocument().toObject(User.class);
                if (doc.getDocument().exists()) {
                  usersList.add(user);
                  userAdapter.notifyDataSetChanged();
                }

              }
            }

            isFirstLoad = false;

          }
        }
      }
    });

  }

}
