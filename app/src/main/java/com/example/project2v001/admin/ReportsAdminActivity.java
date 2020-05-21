package com.example.project2v001.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.example.project2v001.admin.adapters.Report;
import com.example.project2v001.admin.adapters.ReportAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportsAdminActivity extends AppCompatActivity {
  private RecyclerView reportsListView;
  private ReportAdapter reportAdapter;
  private List<Report> reportList;
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

    if(auth.getUid() != null)
    {
      firebaseFirestore.collection("Reports").addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
          if (e == null) {
            if (!documentSnapshots.isEmpty()) {
              if (isFirstDataLoad) {
                reportList.clear();
              }

              for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                if (doc.getType() == DocumentChange.Type.ADDED){

                  String postId = doc.getDocument().getId();
                  Report report = doc.getDocument().toObject(Report.class);

                  if (isFirstDataLoad) {
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





  }
}
