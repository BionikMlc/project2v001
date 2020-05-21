package com.example.project2v001.tab_ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.project2v001.PostActivity;
import com.example.project2v001.R;
import com.example.project2v001.RequestsActivity;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {
  private List<Post> postList;
  private Context context;


  public MyPostAdapter(List<Post> postList) {
    this.postList = postList;
  }

  @NonNull
  @Override
  public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_post_item, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final MyPostAdapter.ViewHolder holder, final int position) {
    holder.setIsRecyclable(false);

    String descText = postList.get(position).getDesc();
    holder.setPostDesc(descText);

    //gets user name and sets it to the user name textView
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.setPostUserName(task.getResult().get("name").toString());
                holder.setUserImg(task.getResult().get("img").toString());
              }
            });

    holder.editButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, PostActivity.class);
        Map<String, String> data = new HashMap<String, String>();
        data.put("postID", postList.get(position).postId);
        data.put("img", postList.get(position).getImg());
        data.put("desc", postList.get(position).getDesc());
        data.put("type", String.valueOf(postList.get(position).getPost_type()));
        intent.putExtra("postData", (Serializable) data);
        context.startActivity(intent);
      }
    });

    holder.deleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Delete Post");
        dialogBuilder.setMessage("Are you sure you want to delete this post ?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            firebaseFirestore.collection("Posts").document(postList.get(position).postId).delete();
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

    holder.requestsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, RequestsActivity.class);
        intent.putExtra("postID", postList.get(position).postId);
        context.startActivity(intent);
      }
    });

    holder.postTypeTextView.setText(postList.get(position).getPost_type());

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
    private TextView editButton;
    private TextView deleteButton;
    private TextView requestsButton;
    private ConstraintLayout container;
    private TextView postTypeTextView;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      editButton = mView.findViewById(R.id.post_request);
      deleteButton = mView.findViewById(R.id.unsave_post);
      requestsButton = mView.findViewById(R.id.my_posts_requests_button);
      container = mView.findViewById(R.id.my_post_container);
      postTypeTextView = mView.findViewById(R.id.post_type_text_view2);
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
