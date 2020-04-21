package com.example.project2v001.chat_module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
  private static final String TAG = "tag";
  private List<ChatData> chatDataList;
  private Context context;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;


  public ChatAdapter(List<ChatData> chatDatatList) {
    this.chatDataList = chatDatatList;
  }

  /******************************
   Adapter Methods
   *****************************/

//onCreateViewHolder : inflates the layout with layout of single item that we created.
  @NonNull
  @Override
  public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);
  }

  //onBindViewHolder : inflates the layout with layout of single item that we created.
  @Override
  public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
    firebaseFirestore = FirebaseFirestore.getInstance();
    auth = FirebaseAuth.getInstance();


    final List<String> messages = (List<String>) chatDataList.get(position).getMessages();
    if (chatDataList.get(position).getOp_id().equals(auth.getUid())) {
      firebaseFirestore.collection("Users").document(chatDataList.get(position).getUser_id()).get()
              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  holder.setUsername(task.getResult().get("name").toString());
                  holder.setUserImg(task.getResult().get("img").toString());
                  if (messages.size() != 0)
                    holder.setText(messages.get(messages.size() - 1));
                }
              });
    } else {
      firebaseFirestore.collection("Users").document(chatDataList.get(position).getOp_id()).get()
              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  holder.setUsername(task.getResult().get("name").toString());
                  holder.setUserImg(task.getResult().get("img").toString());
//                              holder.setText(chatData.get(""));
                  if (messages.size() != 0)
                    holder.setText(messages.get(messages.size() - 1));
                }
              });

    }
  }


//    long timeInMS = postList.get(position).getTimestamp().getTime();
//    String time = DateFormat.format("yyyy/MM/dd HH:mm", new Date(timeInMS)).toString();
//    holder.setPostDate(time);


  //getItemCount : gets the size of data we retrieved.
  @Override
  public int getItemCount() {
    return chatDataList.size();
  }

  /******************************
   End Adapter Methods
   *****************************/

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private CircleImageView userImgView;
    private ConstraintLayout container;
    private TextView usernameView;
    private TextView textView;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
//      container = mView.findViewById(R.id.notif_item_container);
      usernameView = mView.findViewById(R.id.chat_item_username);
      textView = mView.findViewById(R.id.chat_item_text);
      userImgView = mView.findViewById(R.id.user_img);

    }


    public void setUserImg(String imgUri) {
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.rectangle_1)
              .into(userImgView);
    }

    public void setUsername(String username) {
      usernameView.setText(username);
    }

    public void setText(String text) {
      textView.setText(text);
    }

//    public void setPostDate(String date) {
//      postDateView = mView.findViewById(R.id.post_date);
//      postDateView.setText(date);
//    }
  }


}
