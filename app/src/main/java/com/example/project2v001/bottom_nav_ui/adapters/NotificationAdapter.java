package com.example.project2v001.bottom_nav_ui.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.project2v001.RequestsActivity;
import com.example.project2v001.post_module.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
  private static final String TAG = "tag";
  private List<Post> postList;
  private Context context;
  private FirebaseFirestore firebaseFirestore;


  public NotificationAdapter(List<Post> postList) {
    this.postList = postList;
  }

  /******************************
   Adapter Methods
   *****************************/

//onCreateViewHolder : inflates the layout with layout of single item that we created.
  @NonNull
  @Override
  public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);
  }

  //onBindViewHolder : inflates the layout with layout of single item that we created.
  @Override
  public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder holder, final int position) {

//        holder.setIsRecyclable(false);

    String postImg = postList.get(position).getImg();
    holder.setPostImg(postImg);

    //Request Feature
    final String user_id = FirebaseAuth.getInstance().getUid();
    final String postId = postList.get(position).postId;


    if (postList.get(position).getUser_id().equals(user_id) && !postList.get(position).getRequests().isEmpty()) {
        holder.notifIcon.setImageResource(R.drawable.bell_icon);
        holder.notifText.setText("you have requests for this item");
        holder.setPostImg(postList.get(position).getImg());
      } else if (user_id.equals(postList.get(position).getReserved_for())) {
        holder.notifIcon.setImageResource(R.drawable.icon_check);
        holder.notifText.setText("request accepted!\nyou can contact item owner");
        holder.setPostImg(postList.get(position).getImg());
      } else if (postList.get(position).getRequests().contains(user_id) && user_id.equals(postList.get(position).getReserved_for())){
        holder.notifIcon.setImageResource(R.drawable.icon_lock);
        holder.notifText.setText("this item was reserved for another user");
        holder.setPostImg(postList.get(position).getImg());
      }




    if (postList.get(position).getUser_id().equals(user_id)) {
      holder.container.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(context, RequestsActivity.class);
          intent.putExtra("postID", postId);
          context.startActivity(intent);
        }
      });


    }
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
    private ImageView postImgView;
    private ConstraintLayout container;
    private CircleImageView notifIcon;
    private TextView notifText;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      container = mView.findViewById(R.id.notif_item_container);
      notifIcon = mView.findViewById(R.id.user_img);
      notifText = mView.findViewById(R.id.text_sent_to);
      postImgView = mView.findViewById(R.id.notif_item_img);

    }


    public void setPostImg(String imgUri) {
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.default_profile)
              .into(postImgView);
    }

//    public void setNotifIcon(String imgUri) {
//
//      notifIcon = mView.findViewById(R.id.user_img);
//      Glide.with(context)
//              .load(imgUri)
//              .placeholder(R.drawable.rectangle_1)
//              .into(notifIcon);
//    }


  }


}