package com.example.project2v001.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.admin.adapters.Report;
import com.example.project2v001.admin.adapters.ReportAdapter;
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

public class ReportsAdminActivity extends AppCompatActivity {
  private RecyclerView reportsListView;
  private ReportAdapter reportAdapter;
  private List<Report> reportList;
  private DocumentSnapshot lastVisible;
  private FirebaseFirestore firebaseFirestore;
  private FirebaseAuth auth;
  private boolean isFirstDataLoad = true;

//  postListView = view.findViewById(R.id.posts_list_view);
//  postsList = new ArrayList<>();
//  postAdapter = new PostAdapter(postsList);
//        postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        postListView.setAdapter(postAdapter);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reports_admin);

    //init firebase variables
    firebaseFirestore = FirebaseFirestore.getInstance();
    auth = FirebaseAuth.getInstance();

    //init UI
    reportList = new ArrayList<>();
    reportsListView = findViewById(R.id.report_list_view);
    reportsListView.setLayoutManager(new LinearLayoutManager(this));
    reportAdapter = new ReportAdapter(reportList);
    reportsListView.setAdapter(reportAdapter);

    reportsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        boolean isReachedBottom = !recyclerView.canScrollVertically(1);

//        if (isReachedBottom) {
//          loadPosts();
//        }
      }
    });
    String user_id = auth.getUid();
    Query firstQuery = firebaseFirestore.collection("Reports");

    firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e == null) {
          if (!documentSnapshots.isEmpty()) {
            if (isFirstDataLoad) {
              lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
              reportList.clear();
            }

            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

              if (doc.getType() == DocumentChange.Type.ADDED) {

                String postId = doc.getDocument().getId();
                Report report = doc.getDocument().toObject(Report.class).withId(postId);
                  if (doc.getDocument().exists()) {
                    reportList.add(report);
                  }
                  reportAdapter.notifyDataSetChanged();

              }
            }

            isFirstDataLoad = false;

          }
        }
      }

    });
  }

  private void loadPosts() {
    if (auth.getCurrentUser() != null) {
      String user_id = auth.getUid();
      Query nextQuery = firebaseFirestore.collection("Reports")
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
                  Report report = doc.getDocument().toObject(Report.class);
                  reportList.add(report);

                  reportAdapter.notifyDataSetChanged();
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

