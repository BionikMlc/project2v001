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
import com.example.project2v001.tab_ui.adapters.MyPostAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {
  private static final String TAG = "query err";
  private RecyclerView postListView;
  private List<Post> postsList;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;
  private MyPostAdapter myPostAdapter;
  private DocumentSnapshot lastVisible;
  private TextView sendReq;
  private boolean isFirstDataLoad = true;

  public MyPostsFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_my_posts, container, false);
    auth = FirebaseAuth.getInstance();
    postListView = view.findViewById(R.id.my_posts_list_view);

    postsList = new ArrayList<>();
    myPostAdapter = new MyPostAdapter(postsList);
    postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    postListView.setAdapter(myPostAdapter);
    firebaseFirestore = FirebaseFirestore.getInstance();

    if (auth.getCurrentUser() != null) {
      postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);

          if (isReachedBottom) {
            loadPosts();
          }
        }
      });
      String user_id = auth.getUid();
      Query firstQuery = firebaseFirestore.collection("Posts")
              .whereEqualTo("user_id",user_id)
              .orderBy("timestamp", Query.Direction.DESCENDING)
              .limit(8);
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
                  if (post.getReserved_for().isEmpty()){
                    if (isFirstDataLoad) {
                      postsList.add(post);
                    } else {
                      postsList.add(0, post);
                    }
                    myPostAdapter.notifyDataSetChanged();
                  }

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
        String user_id = auth.getUid();
      Query nextQuery = firebaseFirestore.collection("Posts")
              .whereEqualTo("user_id",user_id)
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
                  Post blogPost = doc.getDocument().toObject(Post.class).withId(blogPostId);
                  postsList.add(blogPost);

                  myPostAdapter.notifyDataSetChanged();
                }

              }
            }
          }
        }
      });
    }
    /////////////////////////////
  }
}
