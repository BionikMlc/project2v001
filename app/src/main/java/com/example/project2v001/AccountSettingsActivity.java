package com.example.project2v001;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
//todo
// break stuff into smaller functions and class for more code readability. [ ]
// add a contact number so students can contact each other if req approved. [ ]
// has been changed state [ ]
// add comments [ ]


public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "imageUrl";
    private CircleImageView profileImage;
    private Uri imageUri = null;
    private boolean isChanged = false;
    private final Map<String, String> userMap = new HashMap<>();
    private EditText nameText;
    private Button saveBtn;
    private ProgressBar progressBar;

    private String userId;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Toolbar accountSettingsToolBar = findViewById(R.id.account_settings);
        setSupportActionBar(accountSettingsToolBar);
        getSupportActionBar().setTitle("Account Settings");

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.account_settings_circle_profile_img);
        nameText = findViewById(R.id.account_settings_name);
        saveBtn = findViewById(R.id.account_settings_button);
        progressBar = findViewById(R.id.account_settings_progressBar);

        userId = firebaseAuth.getCurrentUser().getUid();
        //getting stored data from the database
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        progressBar.setVisibility(View.VISIBLE);
                        nameText.setText(name);
                        Glide.with(AccountSettingsActivity.this)
                                .load(task.getResult().get("img"))
                                .placeholder(R.drawable.default_profile)
                                .into(profileImage);
                        Toast.makeText(AccountSettingsActivity.this, "data  exist: ", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountSettingsActivity.this, "data doesn't exist: ", Toast.LENGTH_LONG).show();
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSettingsActivity.this, "Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });



        //saving the data to the database when the user taps the save button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameText.getText().toString();

                if (!TextUtils.isEmpty(name)) {
                    progressBar.setVisibility(View.VISIBLE);

                    if (imageUri != null)
                    {
                        final StorageReference imagePath = storageReference.child("profile_images").child(userId + ".jpg");
                        UploadTask uploadTask =   imagePath.putFile(imageUri);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful())
                                {
                                    throw task.getException();
                                }
                                return imagePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful())
                                {
                                    Log.d(TAG, "onComplete: sss" + task.getResult());
                                    HashMap <String,String> imgMap = new HashMap<>();
                                    imgMap.put("img",task.getResult().toString());
                                    firebaseFirestore.collection("Users").document(userId).set(imgMap, SetOptions.merge());
                                }
                                else
                                    {
                                        Log.d(TAG, "onComplete: "+task.getException());
                                    }
                            }
                        });
                    }

                    userMap.put("name", name);
                    firebaseFirestore.collection("Users").document(userId).set(userMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountSettingsActivity.this, "User settings updated ", Toast.LENGTH_LONG).show();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(AccountSettingsActivity.this, "imageError: " + error, Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


                    startActivity(new Intent(AccountSettingsActivity.this, MainActivity.class));
                    finish();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check device version to apply the right permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(AccountSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(AccountSettingsActivity.this, "permission denied: Not Enough Permissions!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AccountSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        CropImage
                                .activity().setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(AccountSettingsActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profileImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
