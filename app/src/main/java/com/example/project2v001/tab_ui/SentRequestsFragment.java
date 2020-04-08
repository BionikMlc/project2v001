package com.example.project2v001.tab_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.post_module.Post;
import com.example.project2v001.tab_ui.adapters.SentReqAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SentRequestsFragment extends Fragment {
  private static final String TAG = "query err";
  private RecyclerView postListView;
  private List<Post> postsList;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;
  private SentReqAdapter sentReqAdapter;
  private DocumentSnapshot lastVisible;
  private TextView sendReq;
  private boolean isFirstDataLoad = true;

  public SentRequestsFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_sent_requests, container, false);
    auth = FirebaseAuth.getInstance();
    postListView = view.findViewById(R.id.post_requests_list_view);

    postsList = new ArrayList<>();
    sentReqAdapter = new SentReqAdapter(postsList);
    postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    postListView.setAdapter(sentReqAdapter);
    firebaseFirestore = FirebaseFirestore.getInstance();

    if (auth.getCurrentUser() != null) {
      postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);

          if (isReachedBottom) {
//            loadPosts();
          }
        }
      });
      final String user_id = auth.getUid();
      Query firstQuery = firebaseFirestore.collection("Posts");

      firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
          if (e == null) {
            if (!documentSnapshots.isEmpty()) {
              if (isFirstDataLoad) {
                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                postsList.clear();
              }

              for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                if (doc.getType() == DocumentChange.Type.ADDED) {

                  String postId = doc.getDocument().getId();
                  Post post = doc.getDocument().toObject(Post.class).withId(postId);
                 List<String> requests = (List<String>) doc.getDocument().get("requests");
                  if (requests.contains(user_id)) {
                    postsList.add(post);
                  }
                  sentReqAdapter.notifyDataSetChanged();
                }
              }

              isFirstDataLoad = false;

            }
          }
        }

      });
    }

    // Inflate the layout for this fragment
    return view;
  }

  private void loadPosts() {
    if (auth.getCurrentUser() != null) {
      final String user_id = auth.getUid();
      Query nextQuery = firebaseFirestore.collection("Posts")
              .whereEqualTo("user_id", user_id)
              .orderBy("timestamp", Query.Direction.DESCENDING)
              .startAfter(lastVisible)
              .limit(8);

      nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
          if (e == null) {
            if (!documentSnapshots.isEmpty()) {
              lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
//              postsList.clear();
              for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                if (doc.getType() == DocumentChange.Type.ADDED) {

                  String blogPostId = doc.getDocument().getId();
                  Post post = doc.getDocument().toObject(Post.class).withId(blogPostId);
                  List<String> requests = (List<String>) doc.getDocument().get("requests");
                  if (requests.contains(user_id)) {
                    postsList.add(post);
                  }

                  sentReqAdapter.notifyDataSetChanged();
                }

              }
            }
          }
        }
      });
    }
  }
}