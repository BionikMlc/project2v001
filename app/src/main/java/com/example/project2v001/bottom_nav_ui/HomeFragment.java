package com.example.project2v001.bottom_nav_ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.PostActivity;
import com.example.project2v001.R;
import com.example.project2v001.post_module.Post;
import com.example.project2v001.post_module.adapters.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
public class HomeFragment extends Fragment {
    private static final String TAG = "query err";
    private RecyclerView postListView;
    private List<Post> postsList;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    private PostAdapter postAdapter;

    private DocumentSnapshot lastVisible;
    private FloatingActionButton addPostBtn;
    private boolean isFirstDataLoad = true;

    public HomeFragment() {
    } // requires empty constructor


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        postListView = view.findViewById(R.id.posts_list_view);
        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(postsList);
        postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postListView.setAdapter(postAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        addPostBtn = view.findViewById(R.id.add_post_float_btn);

        if (auth.getCurrentUser() != null) {

            postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean isReachedBottom =  !recyclerView.canScrollVertically(1);

                    if(isReachedBottom){
                        loadPosts();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts")
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
                                String resString = doc.getDocument().get("reserved_for").toString();
                                if (doc.getType() == DocumentChange.Type.ADDED && resString.isEmpty()){

                                    String postId = doc.getDocument().getId();
                                    Post post = doc.getDocument().toObject(Post.class).withId(postId);

                                    if (isFirstDataLoad) {
                                        postsList.add(post);
                                    } else {
                                        postsList.add(0, post);
                                    }
                                    postAdapter.notifyDataSetChanged();
                                }
                            }

                            isFirstDataLoad = false;
                        }
                    }
                }

            });
        }
        addPostBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startActivity(new Intent(getActivity(), PostActivity.class));
        }
      });

        // Inflate the layout for this fragment
        return view;
    }

    ///////////////////////////////////////////////////////
    private void loadPosts() {
        if (auth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(8);

            nextQuery.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e == null) {
                        if (!documentSnapshots.isEmpty()) {
                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                                 postsList.clear();
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String blogPostId = doc.getDocument().getId();
                                    Post blogPost = doc.getDocument().toObject(Post.class).withId(blogPostId);
                                    postsList.add(blogPost);
                                    postAdapter.notifyDataSetChanged();

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