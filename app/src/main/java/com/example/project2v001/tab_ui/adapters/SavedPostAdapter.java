package com.example.project2v001.tab_ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SavedPostAdapter extends RecyclerView.Adapter<SavedPostAdapter.ViewHolder> {
  private List<Post> postList;
  private Context context;

  public SavedPostAdapter(List<Post> postList) {
    this.postList = postList;
  }

  @NonNull
  @Override
  public SavedPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_post_item, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final SavedPostAdapter.ViewHolder holder, final int position) {
    holder.setIsRecyclable(false);

    String descText = postList.get(position).getDesc();
    holder.setPostDesc(descText);

    //gets user name and sets it to the user name textView
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        holder.setPostUserName(task.getResult().get("name").toString());
        holder.setUserImg(task.getResult().get("img").toString());

      }
    });

    holder.unsave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Unsave Post");
        dialogBuilder.setMessage("Are you sure you want to remove this post from your saved posts ?");
        dialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            firebaseFirestore.collection("Posts").document(postList.get(position).postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                  List<String> savedBy = (List<String>) task.getResult().get("saved");
                  if (savedBy.contains(FirebaseAuth.getInstance().getUid())) ;
                  savedBy.remove(FirebaseAuth.getInstance().getUid());
                  Map<String, Object> saved = new HashMap<>();
                  saved.put("saved", savedBy);
                  firebaseFirestore.collection("Posts").document(postList.get(position).postId).set(saved, SetOptions.merge());

                }
              }
            });
//            holder.container.setVisibility(View.GONE);
            holder.container.removeAllViews();
          }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });
        dialogBuilder.show();
      }
    });


    final String user_id = FirebaseAuth.getInstance().getUid();
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

    holder.requestButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Map<String,Object> exist = new HashMap<>();
        exist.put("exists",true);
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

    long timeInMS = postList.get(position).getTimestamp().getTime();
    String time = DateFormat.format("yyyy/MM/dd HH:mm", new Date(timeInMS)).toString();
    holder.setPostDate(time);

    String postImg = postList.get(position).getImg();
    holder.setPostImg(postImg);

  }

  @Override
  public int getItemCount() {
    return postList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private TextView postDescView;
    private TextView postUserNameView;
    private TextView postDateView;
    private ImageView postImgView;
    private CircleImageView userImgView;
    private ConstraintLayout container;
    private TextView unsave;
    private TextView requestButton;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      mView = itemView;
      requestButton = mView.findViewById(R.id.post_request);
      unsave = mView.findViewById(R.id.unsave_post);
      container = mView.findViewById(R.id.item_container);
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
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.rectangle_1)
              .into(userImgView);
    }

    public void setPostImg(String imgUri) {
      postImgView = mView.findViewById(R.id.post_img);
      Glide.with(context)
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

