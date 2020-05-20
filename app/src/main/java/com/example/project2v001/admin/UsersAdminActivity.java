package com.example.project2v001.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.admin.adapters.User;
import com.example.project2v001.admin.adapters.UsersAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersAdminActivity extends AppCompatActivity {
  private static final String TAG = "";
  private RecyclerView userListView;
  private List<User> usersList;
  private UsersAdapter userAdapter;

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

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if(!task.getResult().isEmpty())
        {
          for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments())
          {
            User user = documentSnapshot.toObject(User.class);
            usersList.add(user);
            userAdapter.notifyDataSetChanged();
          }
        }
      }
    });

  }

}
