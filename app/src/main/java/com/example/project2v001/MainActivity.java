package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2v001.bottom_nav_ui.AccountFragment;
import com.example.project2v001.bottom_nav_ui.HomeFragment;
import com.example.project2v001.bottom_nav_ui.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;

    private Toolbar mainToolBar;
    private FloatingActionButton addPostBtn;
    private BottomNavigationView mainBottomNav;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init firebase ref
        mAuth = FirebaseAuth.getInstance();
        //init UI
        mainToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle("AcademiaExchange");
        if(mAuth.getCurrentUser() != null) {
            addPostBtn = findViewById(R.id.add_post_float_btn);
            mainBottomNav = findViewById(R.id.mainBottomNav);
            homeFragment = new HomeFragment();
            accountFragment = new AccountFragment();
            notificationFragment = new NotificationFragment();
            mainBottomNav.setSelectedItemId(R.id.bottom_action_home);
            changeFragment(homeFragment);




            /*****************************************************
             *                  LISTENERS
             ******************************************************/
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                }
            });
            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.bottom_action_home:
                            changeFragment(homeFragment);
                            return true;
                        case R.id.bottom_action_notification:
                            changeFragment(notificationFragment);
                            return true;
                        case R.id.bottom_action_account:
                            changeFragment(accountFragment);
                            return true;
                        default:
                            changeFragment(homeFragment);
                            return false;
                    }
                }
            });
            //end listeners
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            sendToLogin();
        } else {
            String userId = mAuth.getCurrentUser().getUid();
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            startActivity(new Intent(MainActivity.this, AccountSettingsActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "error: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, AccountSettingsActivity.class));
                return true;
            default:
                return false;

        }


    }

    private void logout() {
        mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void changeFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container,fragment);
        transaction.commit();

    }
}
