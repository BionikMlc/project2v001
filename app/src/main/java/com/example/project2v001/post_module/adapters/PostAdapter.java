package com.example.project2v001.post_module.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.example.project2v001.ReportPostActivity;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
  private List<Post> postList;
  private Context context;

  public PostAdapter(List<Post> postList) {
    this.postList = postList;
  }

  /******************************
   Adapter Methods
   *****************************/

//onCreateViewHolder : inflates the layout with layout of single item that we created.
  @NonNull
  @Override
  public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);
  }

  //onBindViewHolder : inflates the layout with layout of single item that we created.
  @Override
  public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {

//        holder.setIsRecyclable(false);

    String descText = postList.get(position).getDesc();
    final String user_id = FirebaseAuth.getInstance().getUid();
    holder.setPostDesc(descText);
    if (!postList.get(position).getUser_id().equals(user_id) && !postList.get(position).getUser_id().equals("Yok8QtUMnthwUaBT6JdeSRcymNJ3")) {
      holder.savedButton.setVisibility(View.VISIBLE);
      holder.requestButton.setVisibility(View.VISIBLE);
      holder.reportButton.setVisibility(View.VISIBLE);
    }

    //gets user name and sets it to the user name textView
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        holder.setPostUserName(task.getResult().get("name").toString());
        holder.setUserImg(task.getResult().get("img").toString());
      }
    });

    long timeInMS = postList.get(position).getTimestamp().getTime();
    String time = DateFormat.format("yyyy/MM/dd HH:mm", new Date(timeInMS)).toString();
    holder.setPostDate(time);

    holder.postTypeTextView.setText(postList.get(position).getPost_type());
    String postImg = postList.get(position).getImg();
    holder.setPostImg(postImg);

    //Request Feature

    final String postId = postList.get(position).postId;

    firebaseFirestore.collection("Posts").document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          List<String> postRequests = (List<String>) documentSnapshot.get("requests");
          if (postRequests.contains(user_id)) {
            holder.requestButton.setText("Requested");
            holder.requestButton.setTextColor(holder.requestButton.getResources().getColor(R.color.colorAccent));
          }
        }
      }
    });

    firebaseFirestore.collection("Posts").document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
          List<String> postRequests = (List<String>) documentSnapshot.get("saved");
          if (postRequests.contains(user_id)) {
            holder.savedButton.setText("saved");
            holder.savedButton.setTextColor(holder.savedButton.getResources().getColor(R.color.colorPrimaryDark));
          } else {
            holder.savedButton.setText("save");
            holder.savedButton.setTextColor(holder.savedButton.getResources().getColor(R.color.colorPrimary));
          }
        }
      }
    });
    holder.savedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (holder.savedButton.getText().toString().equals("saved")) {
          holder.savedButton.setText("save");
          holder.savedButton.setTextColor(holder.savedButton.getResources().getColor(R.color.colorPrimary));
        } else {
          holder.savedButton.setText("saved");
          holder.savedButton.setTextColor(holder.savedButton.getResources().getColor(R.color.colorPrimaryDark));
        }
        firebaseFirestore.collection("Posts").document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot documentSnapshot) {
            List<String> savedBy = (List<String>) documentSnapshot.get("saved");
            if (savedBy.contains(user_id))
              savedBy.remove(user_id);
            else
              savedBy.add(user_id);
            Map<String, Object> saved = new HashMap<>();
            saved.put("saved", savedBy);
            firebaseFirestore.collection("Posts").document(postId).set(saved, SetOptions.merge());
          }
        });
      }
    });

    holder.requestButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String user_id = FirebaseAuth.getInstance().getUid();
        final String postId = postList.get(position).postId;
        Map<String, Object> exist = new HashMap<>();
        exist.put("exists", true);
        firebaseFirestore.collection("Notifs").document(postList.get(position).getUser_id()).set(exist);
        if (!holder.requestButton.getText().toString().equals("Request")) {
          holder.requestButton.setText("Request");
          holder.requestButton.setTextColor(holder.requestButton.getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        } else {
          holder.requestButton.setText("Requested");
          holder.requestButton.setTextColor(holder.requestButton.getResources().getColor(R.color.colorAccent));
        }

        firebaseFirestore.collection("Posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            Map<String, Object> req = new HashMap<>();
            List<String> userIds;
            userIds = (List<String>) task.getResult().get("requests");

            if (!userIds.contains(user_id)) {
              userIds.add(user_id);

            } else {
              userIds.remove(user_id);

            }

            req.put("requests", userIds);
            firebaseFirestore.collection("Posts").document(postId).set(req, SetOptions.merge());
          }
        });

      }
    });

    holder.reportButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Intent intent = new Intent(context, ReportPostActivity.class);
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("postID", postList.get(position).postId);
                    data.put("img", postList.get(position).getImg());
                    data.put("desc", postList.get(position).getDesc());
                    data.put("userImg", task.getResult().get("img").toString());
                    data.put("type", String.valueOf(postList.get(position).getPost_type()));
                    data.put("name", task.getResult().get("name").toString());
                    data.put("op_id",postList.get(position).getUser_id());
                    long timeInMS = postList.get(position).getTimestamp().getTime();
                    String time = DateFormat.format("yyyy/MM/dd HH:mm", new Date(timeInMS)).toString();
                    data.put("timestamp", time);
                    intent.putExtra("postData", (Serializable) data);
                    context.startActivity(intent);
                  }
                });
      }
    });
  }

  //getItemCount : gets the size of data we retrieved.
  @Override
  public int getItemCount() {
    return postList.size();
  }

  /******************************
   End Adapter Methods
   *****************************/

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private TextView postDescView;
    private TextView postUserNameView;
    private TextView postDateView;
    private ImageView postImgView;
    private CircleImageView userImgView;
    private TextView requestButton;
    private TextView savedButton;
    private TextView reportButton;
    private TextView postTypeTextView;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;

      requestButton = mView.findViewById(R.id.post_request);
      savedButton = mView.findViewById(R.id.unsave_post);
      reportButton = mView.findViewById(R.id.post_report2);
      postTypeTextView = mView.findViewById(R.id.post_type_text_view);
    }

    public void setPostDesc(String descText) {
      postDescView = mView.findViewById(R.id.post_desc);
      postDescView.setText(descText);
    }

    public void setPostUserName(String userName) {
      postUserNameView = mView.findViewById(R.id.user_name);
      postUserNameView.setText(userName);
    }

    public void setUserImg(String imgUri) {

      userImgView = mView.findViewById(R.id.user_img);
      Glide.with(context.getApplicationContext())
              .load(imgUri)
              .placeholder(R.drawable.rectangle_1)
              .into(userImgView);
    }

    public void setPostImg(String imgUri) {
      postImgView = mView.findViewById(R.id.post_img);
      Glide.with(context.getApplicationContext())
              .load(imgUri)
              .placeholder(R.drawable.default_profile)
              .into(postImgView);
    }

    public void setPostDate(String date) {
      postDateView = mView.findViewById(R.id.post_date);
      postDateView.setText(date);
    }
  }


}
