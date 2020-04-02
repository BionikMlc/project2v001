package com.example.project2v001.tab_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.project2v001.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {



    public MyPostsFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);



        // Inflate the layout for this fragment
        return view;
    }

}
