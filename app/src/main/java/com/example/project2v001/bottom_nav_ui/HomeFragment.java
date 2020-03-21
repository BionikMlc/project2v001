package com.example.project2v001.bottom_nav_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.blog_post_module.Post;
import com.example.project2v001.blog_post_module.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView postListView;
    private List<Post> postsList;
    private FirebaseAuth auth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        postListView = view.findViewById(R.id.posts_list_view);
        postsList = new ArrayList<>();
        final PostAdapter postAdapter = new PostAdapter(postsList);
        postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postListView.setAdapter(postAdapter);
        if (auth.getCurrentUser() != null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                private static final String TAG = "query change event";

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Post postItem = doc.getDocument().toObject(Post.class);
                                Log.d(TAG, "onEvent: " + postItem);
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
}
