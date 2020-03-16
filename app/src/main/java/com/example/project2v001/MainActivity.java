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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

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
        addPostBtn = findViewById(R.id.add_post_float_btn);
        mainBottomNav =  findViewById(R.id.mainBottomNav);


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user  = mAuth.getCurrentUser();
        if(user == null)
        {
            sendToLogin();
        }else
            {
                String userId = mAuth.getCurrentUser().getUid();
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            if(!task.getResult().exists())
                            {
                                startActivity(new Intent(MainActivity.this, AccountSettingsActivity.class));
                                finish();
                            }
                        }
                        else
                            {
                                Toast.makeText(MainActivity.this, "error: "+task.getException(), Toast.LENGTH_LONG).show();
                            }
                    }
                });

            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
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

    private void sendToLogin(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
