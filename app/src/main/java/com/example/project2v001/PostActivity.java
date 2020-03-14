package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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


        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postDescription = postDesc.getText().toString();
                int checkedRadioId = postType.getCheckedRadioButtonId();
                String userId = firebaseAuth.getCurrentUser().getUid();
                //check that fields are not empty
                if(!TextUtils.isEmpty(postDescription) && checkedRadioId != -1)
                {
                    Map<String, Object> post = new HashMap<>();
                    switch (checkedRadioId)
                    {
                        case RADIO_BTN_ID_1:
                            post.put("post_type","need");
                            break;
                        case RADIO_BTN_ID_2:
                            post.put("post_type","give away");
                            break;
                        case RADIO_BTN_ID_3:
                            post.put("post_type","exchange");
                            break;
                        default:
                            break;
                    }
                    post.put("desc",postDescription);
                    post.put("user_id",userId);
                    post.put("timeStamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts").add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(PostActivity.this, "post added: ", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(PostActivity.this, MainActivity.class));
                                finish();

                            }
                            else
                                {
                                    Toast.makeText(PostActivity.this, "error: "+task.getException(), Toast.LENGTH_LONG).show();
                                }
                        }
                    });
                }

            }
        });
    }


}
