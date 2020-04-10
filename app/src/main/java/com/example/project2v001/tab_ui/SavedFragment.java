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
import com.example.project2v001.tab_ui.adapters.SavedPostAdapter;
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


public class SavedFragment extends Fragment {
  private static final String TAG = "query err";
  private RecyclerView postListView;
  private List<Post> postsList;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;
  private SavedPostAdapter savedPostAdapter;
  private DocumentSnapshot lastVisible;
  private TextView sendReq;
  private boolean isFirstDataLoad = true;

  public SavedFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_saved, container, false);
    auth = FirebaseAuth.getInstance();
    postListView = view.findViewById(R.id.saved_posts_list_view);

    postsList = new ArrayList<>();
    savedPostAdapter = new SavedPostAdapter(postsList);
    postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    postListView.setAdapter(savedPostAdapter);
    firebaseFirestore = FirebaseFirestore.getInstance();

    if (auth.getCurrentUser() != null) {
      postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);
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
                  List<String> saved = (List<String>) doc.getDocument().get("saved");
                  Post post = doc.getDocument().toObject(Post.class).withId(postId);

                  if (saved.contains(user_id))
                    postsList.add(post);

                  savedPostAdapter.notifyDataSetChanged();
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

}

