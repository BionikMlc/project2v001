package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mainToolBar;
    private FloatingActionButton addPostBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mainToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);
//      getSupportActionBar().setTitle("AcademiaExchange");
        addPostBtn = findViewById(R.id.add_post_btn);

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
