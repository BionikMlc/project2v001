package com.example.project2v001.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project2v001.LoginActivity;
import com.example.project2v001.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddAnnouncemenAdmintActivity extends AppCompatActivity {
  private EditText descEditText;
  private Button postButton;
  private ProgressBar progressBar;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;
  private Toolbar mainToolBar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_add_announcement);
    mainToolBar = findViewById(R.id.main_toolbar2);
    setSupportActionBar(mainToolBar);
    getSupportActionBar().setTitle("AcademiaExchange");


    //init firebase variables
    auth = FirebaseAuth.getInstance();
    firebaseFirestore = FirebaseFirestore.getInstance();


    //init UI
    descEditText = findViewById(R.id.post_desc);
    postButton = findViewById(R.id.add_new_post2);
    progressBar = findViewById(R.id.progressBar2);

    //listeners
    postButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String descText = descEditText.getText().toString();
        if(!descText.isEmpty())
        {
          progressBar.setVisibility(View.VISIBLE);
          Map<String,Object> anouncementData = new HashMap<>();
          anouncementData.put("img","https://firebasestorage.googleapis.com/v0/b/project2v001.appspot.com/o/posts_images%2Fannounce.png?alt=media&token=8dd7fb89-fb20-4263-b573-f26e8d9f64f5");
          anouncementData.put("desc", descText);
          anouncementData.put("user_id", auth.getUid());
          anouncementData.put("timestamp", FieldValue.serverTimestamp());
          anouncementData.put("requests", new ArrayList<>());
          anouncementData.put("saved", new ArrayList<>());
          anouncementData.put("post_type", "Announcement");
          anouncementData.put("reserved_for","");
          firebaseFirestore.collection("Posts").add(anouncementData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

              if(task.isSuccessful()) {
                progressBar.setVisibility(View.INVISIBLE);
              }
              else {
                Toast.makeText(AddAnnouncemenAdmintActivity.this, "error: " + task.getException(), Toast.LENGTH_LONG).show();
              }
              startActivity(new Intent(AddAnnouncemenAdmintActivity.this, AnnouncementAdminActivity.class));
              Toast.makeText(AddAnnouncemenAdmintActivity.this, "announcement posted: ", Toast.LENGTH_LONG).show();
              finish();
            }
          });

        }

      }
    });


  }
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
    startActivity(new Intent(AddAnnouncemenAdmintActivity.this, LoginActivity.class));
    finish();
  }

  private void logout() {
    auth.signOut();
    sendToLogin();

  }

}
