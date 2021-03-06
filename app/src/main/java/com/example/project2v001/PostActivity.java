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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

//todo
// get the image from a selection of images from the database
// currently filling description is mandatory it should be optional

public class PostActivity extends AppCompatActivity {
    //ids for radio button so we can check which one was checked.
    private static final int RADIO_BTN_ID_1 = 0; // need
    private static final int RADIO_BTN_ID_2 = 1;// give away
    private static final int RADIO_BTN_ID_3 = 2;// exchange

    //ui fields
    private Toolbar addPostToolbar;
    private ImageView postImg;
    private Button addPostBtn;
    private EditText postDesc;
    private Uri imguri = null;
    private RadioGroup postType;

    private RadioButton need;
    private RadioButton giveAway;
    private RadioButton exchange;

    //database references
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        addPostToolbar = findViewById(R.id.add_post_toolbar);
        setSupportActionBar(addPostToolbar);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init database reference
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //init ui
        postImg = findViewById(R.id.post_img);
        postDesc = findViewById(R.id.post_desc);
        addPostBtn = findViewById(R.id.add_new_post);

        postType = findViewById(R.id.post_type_rg);
        need = findViewById(R.id.post_type_need);
        giveAway = findViewById(R.id.post_type_give_away);
        exchange = findViewById(R.id.post_type_exhange);

        need.setId(RADIO_BTN_ID_1);
        giveAway.setId(RADIO_BTN_ID_2);
        exchange.setId(RADIO_BTN_ID_3);

        //obtain post data when add post button is clicked
        //check if the fields are not empty before submitting to the database

        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check device version to apply the right permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(PostActivity.this, "permission denied: Not Enough Permissions!", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        CropImage
                                .activity().setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(PostActivity.this);
                    }
                }
            }
        });


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String postDescription = postDesc.getText().toString();
                int checkedRadioId = postType.getCheckedRadioButtonId();
                final String userId = firebaseAuth.getCurrentUser().getUid();
                //check that fields are not empty
                if (!TextUtils.isEmpty(postDescription) && checkedRadioId != -1 && imguri != null) {
                    final Map<String, Object> post = new HashMap<>();
                    switch (checkedRadioId) {
                        case RADIO_BTN_ID_1:
                            post.put("post_type", "need");
                            break;
                        case RADIO_BTN_ID_2:
                            post.put("post_type", "give away");
                            break;
                        case RADIO_BTN_ID_3:
                            post.put("post_type", "exchange");
                            break;
                        default:
                            break;
                    }

                    final StorageReference imagePath = storageReference.child("posts_images").child(userId + ".jpg");
                    UploadTask uploadTask = imagePath.putFile(imguri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imagePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        private static final String TAG = "Tag";

                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: sss" + task.getResult());
                                post.put("img", task.getResult().toString());
                                post.put("desc", postDescription);
                                post.put("user_id", userId);
                                post.put("timestamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts").add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Toast.makeText(PostActivity.this, "post added: ", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });

                            } else {
                                Log.d(TAG, "onComplete: " + task.getException());
                            }
                        }
                    });



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
                imguri = result.getUri();
                postImg.setImageURI(imguri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
