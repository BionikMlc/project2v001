package com.example.project2v001.chat_module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
  private static final String TAG = "tag";
  private List<ChatData> chatDataList;
  private Context context;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;


  public MessagesAdapter(List<ChatData> chatDataList) {
    this.chatDataList = chatDataList;
  }

  /******************************
   Adapter Methods
   *****************************/

//onCreateViewHolder : inflates the layout with layout of single item that we created.
  @NonNull
  @Override
  public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_box, parent, false);
    context = parent.getContext();
    return new ViewHolder(view);
  }

  //onBindViewHolder : inflates the layout with layout of single item that we created.
  @Override
  public void onBindViewHolder(@NonNull final MessagesAdapter.ViewHolder holder, final int position) {
    auth = FirebaseAuth.getInstance();
    firebaseFirestore = FirebaseFirestore.getInstance();

    if (chatDataList.get(position).getUser_id().equals(auth.getUid())) {
      holder.cardViewRight.setVisibility(View.VISIBLE);
      holder.textRight.setText(chatDataList.get(position).getMessage());
    }
    if(chatDataList.get(position).getReceiver_id().equals(auth.getUid())){
      holder.cardViewLeft.setVisibility(View.VISIBLE);
      holder.textLeft.setText(chatDataList.get(position).getMessage());
    }
//    firebaseFirestore.collection("Chats").document(chatDataList.get(position)
//            .chatId).collection("Messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//      @Override
//      public void onComplete(@NonNull Task<QuerySnapshot> task) {
//        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
//
//
//
//        }
//      }
//    });

//
//        holder.cardViewLeft.setVisibility(View.VISIBLE);
//        holder.textLeft.setText();


  }


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
    private TextView textRight;
    private TextView textLeft;
    private CardView cardViewRight;
    private CardView cardViewLeft;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      textRight = mView.findViewById(R.id.chat_box_text_right);
      textLeft = mView.findViewById(R.id.chat_box_text_left);
      cardViewLeft = mView.findViewById(R.id.chat_box_cardView_left);
      cardViewRight = mView.findViewById(R.id.chat_box_cardView_right);


    }


  }


}