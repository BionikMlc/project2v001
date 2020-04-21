package com.example.project2v001.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReqAdapter extends RecyclerView.Adapter<ReqAdapter.ViewHolder> {
  private static final String TAG = "requests";
  private List<Post> postList;
  private Context context;
  private static int index = 0;


  public ReqAdapter(List<Post> postList) {
    this.postList = postList;
    index = postList.size();
  }

  @NonNull
  @Override
  public ReqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_conformation_item, parent, false);
    context = parent.getContext();

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ReqAdapter.ViewHolder holder, final int position) {
    final String postID = postList.get(0).postId;
    final List<String> postReqID = postList.get(position).getRequests();


    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();




    firebaseFirestore.collection("Users").document(postReqID.get(position)).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.setPostUserName(task.getResult().get("name").toString());
                holder.setUserImg(task.getResult().get("img").toString());
              }
            });

    String postImg = postList.get(position).getImg();
    holder.setPostImg(postImg);

    holder.acceptButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Accept Request");
        dialogBuilder.setMessage("by accepting this request this item will be reserved for this user");
        final Map<String,Object> reqData = new HashMap<>();
        reqData.put("reserved_for",postReqID.get(position));
        reqData.put("requests",new ArrayList<String>());
        dialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
           firebaseFirestore.collection("Posts").document(postID).set(reqData, SetOptions.merge());
           Map<String,Object> messageMap = new HashMap<>();
           messageMap.put("op_id",postList.get(position).getUser_id());
           messageMap.put("user_id",reqData.get("reserved_for"));
//           messageMap.put("timestamp", FieldValue.serverTimestamp());
           messageMap.put("messages",new ArrayList<String>());
           firebaseFirestore.collection("Chats").add(messageMap);
           context.startActivity(new Intent());


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



  }

  @Override
  public int getItemCount() {
    return postList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private TextView postUserNameView;
    private ImageView postImgView;
    private CircleImageView userImgView;
    private TextView acceptButton;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      acceptButton = mView.findViewById(R.id.req_conf_accept_btn);
    }


    public void setPostUserName(String userName) {
      postUserNameView = mView.findViewById(R.id.req_conf_username);
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
      postImgView = mView.findViewById(R.id.req_conf_post_img);
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.default_profile)
              .into(postImgView);
    }

  }
}
