package com.example.project2v001.bottom_nav_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.chat_module.ChatAdapter;
import com.example.project2v001.chat_module.ChatData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagingFragment extends Fragment {

  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;
  private List<ChatData> chatList;
  private RecyclerView chatListView;
  private ChatAdapter chatAdapter;


  public MessagingFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_messaging, container, false);

    auth = FirebaseAuth.getInstance();
    chatListView = view.findViewById(R.id.chatListView);

    chatList = new ArrayList<>();
    chatAdapter = new ChatAdapter(chatList);
    chatListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    chatListView.setAdapter(chatAdapter);
    firebaseFirestore = FirebaseFirestore.getInstance();
//
    if (auth.getCurrentUser() != null) {
      chatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);

//          if (isReachedBottom) {
////                        loadPosts();
//          }
        }
      });

      final String user_id = auth.getUid();
      Query firstQuery = firebaseFirestore.collection("Chats");
//                     .whereEqualTo("op_id",user_id)
//              .orderBy("timestamp", Query.Direction.DESCENDING);
      firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
          if (e == null) {
            if (!documentSnapshots.isEmpty()) {
              chatList.clear();
            }
            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

              if (doc.getType() == DocumentChange.Type.ADDED) {

//                String postId = doc.getDocument().getId();
                final ChatData chatData = doc.getDocument().toObject(ChatData.class);

                  chatList.add(chatData);


                chatAdapter.notifyDataSetChanged();
              }
            }
          }
        }
      });
    }
    return view;
  }
}