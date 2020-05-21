package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportPostActivity extends AppCompatActivity {
  private TextView postDescView;
  private TextView postUserNameView;
  private TextView postDateView;
  private ImageView postImgView;
  private CircleImageView userImgView;
  private EditText reportDescEditTextView;
  private Button postReportButton;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_report_post);

    //init firebase variables
    auth = FirebaseAuth.getInstance();
    firebaseFirestore = FirebaseFirestore.getInstance();


    //init UI
    postDescView = findViewById(R.id.post_desc);
    postUserNameView = findViewById(R.id.user_name);
    postDateView = findViewById(R.id.post_date);
    postImgView = findViewById(R.id.post_img);
    userImgView = findViewById(R.id.user_img);
    reportDescEditTextView = findViewById(R.id.report_desc);
    postReportButton = findViewById(R.id.report_post_btn);


    //check if useer is logged in
    if(auth.getCurrentUser().getUid() != null){


    }else
      {

      }

    //get data from intent
    final Intent intent = getIntent();
    if (intent.hasExtra("postData")) {


      final Map<String, String> postData = (Map<String, String>) intent.getSerializableExtra("postData");
      postDescView.setText(postData.get("desc"));
      postUserNameView.setText(postData.get("name"));
      postDateView.setText(postData.get("timestamp"));
      //loads imgs from url
      Glide.with(this)
              .load(postData.get("img"))
              .placeholder(R.drawable.rectangle_1)
              .into(postImgView);
      Glide.with(this)
              .load(postData.get("userImg"))
              .placeholder(R.drawable.default_profile)
              .into(userImgView);

      postReportButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //check if report desc was filled
          String reportDesc = reportDescEditTextView.getText().toString();
          if(!TextUtils.isEmpty(reportDesc))
          {
            //add report info to reports the database
            postData.put("reportDesc",reportDesc);
            postData.put("reporterId",auth.getCurrentUser().getUid());
            firebaseFirestore.collection("Reports").document(postData.get("postID")).set(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ReportPostActivity.this, "Report Sent, and will be reviewed by moderator. thanks for making this app better", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ReportPostActivity.this,MainActivity.class));
                finish();
              }
            });

          }else {
            Toast.makeText(ReportPostActivity.this, "please fill report description", Toast.LENGTH_LONG).show();
          }
        }
      });


    }

  }
}
