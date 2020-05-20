package com.example.project2v001.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.project2v001.LoginActivity;
import com.example.project2v001.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

public class DashboardAdminActivity extends AppCompatActivity {
  private static final String TAG = "";
  private CardView usersCardView;
  private CardView postsCardView;
  private CardView announceCardView;
  private CardView reportsCardView;
  private FirebaseAuth auth;
  private Toolbar mainToolBar;
  private FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    auth = FirebaseAuth.getInstance();

//    setClaims(auth.getCurrentUser().getEmail());
    setContentView(R.layout.activity_admin_dashboard);
    mainToolBar = findViewById(R.id.main_toolbar);
    setSupportActionBar(mainToolBar);
    getSupportActionBar().setTitle("AcademiaExchange");
    usersCardView = findViewById(R.id.admin_dashboard_users_cardView);
    postsCardView = findViewById(R.id.admin_dashboard_posts_cardView);
    announceCardView = findViewById(R.id.admin_dashboard_announce_cardView);
    reportsCardView = findViewById(R.id.admin_dashboard_reports_cardView);


    usersCardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(DashboardAdminActivity.this, UsersAdminActivity.class));
      }
    });
    postsCardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(DashboardAdminActivity.this, PostsAdminActivity.class));
      }
    });
    announceCardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(DashboardAdminActivity.this, AnnouncementAdminActivity.class));
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
    startActivity(new Intent(DashboardAdminActivity.this, LoginActivity.class));
    finish();
  }

  private void logout() {
    auth.signOut();
    sendToLogin();

  }

//  private Task<String> setClaims(String email) {
//    // Create the arguments to the callable function.
//    Map<String, String> data = new HashMap<>();
//    data.put("email", email);
//    return firebaseFunctions
//            .getHttpsCallable("addAdminRole")
//            .call(data)
//            .continueWith(new Continuation<HttpsCallableResult, String>() {
//              @Override
//              public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                // This continuation runs on either success or failure, but if the task
//                // has failed then getResult() will throw an Exception which will be
//                // propagated down.
//                String result = (String) task.getResult().getData();
//                return result;
//              }
//            });
//  }
}