package com.example.project2v001.bottom_nav_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.blog_post_module.Post;
import com.example.project2v001.blog_post_module.PostAdapter;
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

        if (auth.getCurrentUser() != null) {

            postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        loadPosts();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).startAfter().limit(6);
            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null && !queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Post postItem = doc.getDocument().toObject(Post.class);
                                postsList.add(postItem);
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.d(TAG, "post query change exception: " + e.getMessage());


                    }

                }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void loadPosts() {
        if (auth.getCurrentUser() != null) {

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(6);
            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null && !queryDocumentSnapshots.isEmpty()) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Post postItem = doc.getDocument().toObject(Post.class);
                                    postsList.add(postItem);
                                    postAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d(TAG, "post query change exception: " + e.getMessage());
                        }
                    }

                }
            });
        }
    }
}