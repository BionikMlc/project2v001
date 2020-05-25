package com.example.project2v001;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.chat_module.ChatData;
import com.example.project2v001.chat_module.MessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
  private static final String TAG = "cats";
  private CircleImageView userImageView;
  private EditText messageEditTextView;
  private TextView username;
  private Button sendButtonView;
  private RecyclerView messagesListView;
  private FirebaseFirestore firebaseFirestore;
  private FirebaseAuth auth;
  private Map<String, String> msg = new HashMap<>();
  private MessagesAdapter messagesAdapter;
  private List<ChatData> chatDataList;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    Intent intent = getIntent();

    firebaseFirestore = FirebaseFirestore.getInstance();
    auth = FirebaseAuth.getInstance();

    userImageView = findViewById(R.id.user_img);
    messageEditTextView = findViewById(R.id.chat_activity_message_edit_text);
    username = findViewById(R.id.activity_chat_username);
    sendButtonView = findViewById(R.id.chat_activity_send_button);
    messagesListView = findViewById(R.id.chat_activity_message_list);
    messagesListView.setLayoutManager(new LinearLayoutManager(this));
    chatDataList = new ArrayList<>();
    messagesAdapter = new MessagesAdapter(chatDataList);
    messagesListView.setAdapter(messagesAdapter);


    if (intent.hasExtra("chatData")) {
      final Map<String, String> chatData = (Map<String, String>) intent.getSerializableExtra("chatData");
      if (auth.getUid().equals(chatData.get("op_id"))) {
        firebaseFirestore.collection("Users").document(chatData.get("user_id")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    username.setText(task.getResult().get("name").toString());
                    Glide.with(ChatActivity.this)
                            .load(task.getResult().get("img"))
                            .placeholder(R.drawable.rectangle_1)
                            .into(userImageView);
                  }
                });
      } else {
        firebaseFirestore.collection("Users").document(chatData.get("op_id")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()) {
                      username.setText(task.getResult().get("name").toString());
                      Glide.with(ChatActivity.this)
                              .load(task.getResult().get("img"))
                              .placeholder(R.drawable.rectangle_1)
                              .into(userImageView);
                    }
                  }
                });
      }


      firebaseFirestore.collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {

          for (final DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
            sendButtonView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String msg = messageEditTextView.getText().toString();
                messageEditTextView.setText("");
                if (!msg.isEmpty()) {
                  Map<String, Object> msgData = new HashMap<>();
                  msgData.put("message", msg);
                  msgData.put("timestamp", FieldValue.serverTimestamp());
                  msgData.put("user_id", auth.getUid());
                  if (chatData.get("op_id").equals(auth.getUid()))
                    msgData.put("receiver_id", chatData.get("user_id"));
                  else
                    msgData.put("receiver_id", chatData.get("op_id"));
                  firebaseFirestore.collection("Chats").document(documentSnapshot.getId()).collection("Messages").add(msgData);
                }
              }
            });
            firebaseFirestore.collection("Chats").document(documentSnapshot.getId())
                    .collection("Messages").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                  for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    String chatDataId = documentSnapshot.getDocument().getId();
                    ChatData chatMessages = documentSnapshot.getDocument().toObject(ChatData.class).withId(chatDataId);
                    Log.i(TAG, "onEvent: " + chatMessages.getOp_id());
                    Log.i(TAG, "onEvent: " + chatMessages.getUser_id());
                    if (documentSnapshot.getType() == DocumentChange.Type.ADDED) {
                      if ((chatMessages.getReceiver_id().equals(chatData.get("op_id")) || chatMessages.getUser_id().equals(chatData.get("op_id")))
                              && (chatMessages.getUser_id().equals(chatData.get("user_id")) || chatMessages.getReceiver_id().equals(chatData.get("user_id")))) {
                        chatDataList.add(chatMessages);
                        messagesAdapter.notifyDataSetChanged();
                        messagesListView.post(new Runnable() {
                          @Override
                          public void run() {
                            // Call smooth scroll
                            messagesListView.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
                          }
                        });
                        messagesAdapter.notifyDataSetChanged();

                      }

                    }

                  }


                }

              }
            });

          }
        }
      });


    }
  }
}
