package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText regEmail;
    private EditText regPassword;
    private EditText regConformPass;
    private Button regCreateAccountButton;
    private Button regHaveAccountButton;
    private ProgressBar regProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regConformPass = findViewById(R.id.reg_conform_password);
        regCreateAccountButton = findViewById(R.id.reg_button);
        regHaveAccountButton = findViewById(R.id.reg_have_account_button);
        regProgressBar = findViewById(R.id.reg_progress);

        regCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = regEmail.getText().toString();
                final String password = regPassword.getText().toString();
                String conformPassword = regConformPass.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(conformPassword)) {
                    if (password.equals(conformPassword)) {
                        regProgressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegisterActivity.this, AccountSettingsActivity.class);
                                    Map<String,String> userData = new HashMap<>();
                                    userData.put("uid",mAuth.getUid());
                                    userData.put("email",email);
                                    intent.putExtra("userData",(Serializable) userData);
                                    startActivity(intent);
                                } else {
                                    String err = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Registration Error: " + err, Toast.LENGTH_LONG).show();
                                }
                                regProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "please fill required fields", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        regHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null)
            sendToMain();
    }

    private void sendToMain() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void sendToLogin() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
