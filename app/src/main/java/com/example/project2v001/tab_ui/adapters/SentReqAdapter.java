package com.example.project2v001.tab_ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SentReqAdapter extends RecyclerView.Adapter<SentReqAdapter.ViewHolder> {
  private List<Post> postList;
  private Context context;
  private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

  public SentReqAdapter(List<Post> postList) {
    this.postList = postList;
  }

  @NonNull
  @Override
  public SentReqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);

  }

  @Override
  public void onBindViewHolder(@NonNull final SentReqAdapter.ViewHolder holder, final int position) {
//    holder.setIsRecyclable(false);

      holder.cancelImgButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
              MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
              dialogBuilder.setTitle("Delete Request");
              dialogBuilder.setMessage("Are you sure you want to delete this Request ?");
              dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  holder.container.removeAllViews();
                  firebaseFirestore.collection("Posts").document(postList.get(position).postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                      List<String> savedBy = (List<String>) task.getResult().get("requests");
                      if(savedBy.contains(FirebaseAuth.getInstance().getUid()))
                        savedBy.remove(FirebaseAuth.getInstance().getUid());
                      Map<String, Object> saved = new HashMap<>();
                      saved.put("requests",savedBy);
                      firebaseFirestore.collection("Posts").document(postList.get(position).postId).set(saved, SetOptions.merge());
                    }
                  });

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

    //gets user name and sets it to the user name textView
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        holder.setUserImg(task.getResult().get("img").toString());
        holder.setPostDesc(task.getResult().get("name").toString());

      }
    });

//    long timeInMS = postList.get(position).getTimestamp().getTime();
//    String time = DateFormat.format("yyyy/MM/dd HH:mm", new Date(timeInMS)).toString();
//    holder.setPostDate(time);
//
//    String postImg = postList.get(position).getImg();
//    holder.setPostImg(postImg);





  }

  @Override
  public int getItemCount() {
    return postList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private TextView postDescView;
    private ImageButton cancelImgButton;
    private CircleImageView userImgView;
    private ConstraintLayout container;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      cancelImgButton = mView.findViewById(R.id.cancel_botton);
      container = mView.findViewById(R.id.item_container);
    }

    public void setPostDesc(String descText) {
      postDescView = mView.findViewById(R.id.text_sent_to);
      postDescView.setText("a request has been sent to "+descText);

    }

//    public void setPostUserName(String userName) {
//      postUserNameView = mView.findViewById(R.id.user_name);
//      postUserNameView.setText(userName);
//    }

    public void setUserImg(String imgUri) {

      userImgView = mView.findViewById(R.id.user_img);
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.rectangle_1)
              .into(userImgView);
    }

//    public void setPostImg(String imgUri) {
//      postImgView = mView.findViewById(R.id.post_img);
//      Glide.with(context)
//              .load(imgUri)
//              .placeholder(R.drawable.default_profile)
//              .into(postImgView);
//    }

//    public void setPostDate(String date) {
//      postDateView = mView.findViewById(R.id.post_date);
//      postDateView.setText(date);
//    }
  }
}
