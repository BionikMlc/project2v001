package com.example.project2v001.bottom_nav_ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.example.project2v001.RequestsActivity;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>  {
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
    firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseFirestore.collection("Posts")
            .document(postId).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> postRequests = (List<String>) documentSnapshot.get("requests");
                    if (!postRequests.isEmpty()) {
                      holder.setPostImg(postList.get(position).getImg());
                    }
                }
              }
            });

    holder.container.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, RequestsActivity.class);
//        Map<String, String> data = new HashMap<String, String>();
//        data.put("postID", postList.get(position).postId);
//        data.put("img", postList.get(position).getImg());
//        data.put("desc", postList.get(position).getDesc());
//        data.put("type", String.valueOf(postList.get(position).getPostType()));
//        intent.putExtra("postData", (Serializable) data);
        context.startActivity(intent);
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
    private ImageView postImgView;
    private ConstraintLayout container;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      container = mView.findViewById(R.id.notif_item_container);


    }


    public void setPostImg(String imgUri) {
      postImgView = mView.findViewById(R.id.notif_item_img);
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.default_profile)
              .into(postImgView);
    }


  }


}