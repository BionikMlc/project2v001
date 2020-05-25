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
import com.example.project2v001.bottom_nav_ui.adapters.NotificationAdapter;
import com.example.project2v001.post_module.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
  private static final String TAG = "query err";
  private RecyclerView postListView;
  private List<Post> postsList;
  private List<String> reqs;
  private FirebaseAuth auth;
  private FirebaseFirestore firebaseFirestore;
  private NotificationAdapter notificationAdapter;
  private boolean isFirstLoad = true;


  public NotificationFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_notification, container, false);
    auth = FirebaseAuth.getInstance();
    postListView = view.findViewById(R.id.notification_list_view);

    postsList = new ArrayList<>();
    notificationAdapter = new NotificationAdapter(postsList);
    postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    postListView.setAdapter(notificationAdapter);
    firebaseFirestore = FirebaseFirestore.getInstance();

    if (auth.getUid() != null) {
      postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          boolean isReachedBottom = !recyclerView.canScrollVertically(1);

          if (isReachedBottom) {
//                        loadPosts();
          }
        }
      });

      firebaseFirestore.collection("Notifs").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
          if (task.getResult().exists())
          {
            final String user_id = auth.getUid();
            Query firstQuery = firebaseFirestore.collection("Posts")
//                    .whereEqualTo("user_id",user_id)
                    .orderBy("timestamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e == null) {
                  if (!documentSnapshots.isEmpty()) {
                    if(isFirstLoad) {
                      postsList.clear();
                    }
                  }

                  for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                      String postId = doc.getDocument().getId();
                      final Post post = doc.getDocument().toObject(Post.class).withId(postId);
                      reqs = new ArrayList<>();
                      reqs = post.getRequests();
                      Map<String,Object> exist = new HashMap<>();
                      exist.put("exists",auth.getUid());
                      if (post.getReserved_for().equals(user_id)) {
                        postsList.add(post);
                        firebaseFirestore.collection("Notifs").document(auth.getUid()).set(exist);
                      }
                      else if (post.getUser_id().equals(user_id) && !reqs.isEmpty()) {
                        postsList.add(post);
                        firebaseFirestore.collection("Notifs").document(auth.getUid()).set(exist);
                      }
                      else if (reqs.contains(user_id) && !post.getReserved_for().equals(user_id)) {
                        postsList.add(post);
                        firebaseFirestore.collection("Notifs").document(auth.getUid()).set(exist);
                      }
                      notificationAdapter.notifyDataSetChanged();

                    }
                  }
                  isFirstLoad = false;
                }
              }
            });
          }

        }
      });


    }

    // Inflate the layout for this fragment
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    firebaseFirestore.collection("Notifs").document(auth.getUid()).delete();
  }
}