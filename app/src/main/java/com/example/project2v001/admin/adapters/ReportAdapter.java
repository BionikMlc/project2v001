package com.example.project2v001.admin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
  private List<Report> userList;
  private Context context;
  private FirebaseFirestore firebaseFirestore;
  private FirebaseAuth auth;


  public ReportAdapter(List<Report> userList) {
    this.userList = userList;
    setHasStableIds(true);
  }

  @NonNull
  @Override
  public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_report_post, parent, false);
    context = parent.getContext();

    return new ViewHolder(view);

  }

  @Override
  public void onBindViewHolder(@NonNull final ReportAdapter.ViewHolder holder, final int position) {
//   holder.setIsRecyclable(false);

    firebaseFirestore =FirebaseFirestore.getInstance();
    auth = FirebaseAuth.getInstance();

    holder.hidePostReportButton();
    holder.deleteUserButton.setVisibility(View.VISIBLE);
    holder.discardReportButton.setVisibility(View.VISIBLE);
    holder.deletePostButton.setVisibility(View.VISIBLE);
    holder.setUsername(userList.get(position).getName());
    holder.setUserImg(userList.get(position).getUserImg());
    holder.setPostImg(userList.get(position).getImg());
    holder.setReportDescEditTextView(userList.get(position).getReportDesc());
    holder.setPostDateView(userList.get(position).getTimestamp());
    holder.setPostDescView(userList.get(position).getDesc());

    holder.discardReportButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Discard Report");
        dialogBuilder.setMessage("Are you sure you want to discard this report ?");
        dialogBuilder.setPositiveButton("discard", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            firebaseFirestore.collection("Reports").document(userList.get(position).getPostID()).delete();
//        holder.container.setVisibility(View.GONE);
            holder.container.removeAllViews();
          }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });
        dialogBuilder.show();

      }
    });

    holder.deletePostButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
      dialogBuilder.setTitle("Delete Post");
      dialogBuilder.setMessage("Are you sure you want to delete this Post  ?");
      dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      firebaseFirestore.collection("Reports").document(userList.get(position).getPostID()).delete();
      firebaseFirestore.collection("Posts").document(userList.get(position).getPostID()).delete();
      holder.container.removeAllViews();
    }
  });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
  });
        dialogBuilder.show();
      }
    });

    holder.deleteUserButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Delete User");
        dialogBuilder.setMessage("Are you sure you want to delete this user ?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      firebaseFirestore.collection("Reports").document(userList.get(position).getPostID()).delete();
      holder.deleteUserData(userList.get(position).getOp_id());
//        holder.deleteUser(userList.get(position).getOp_id());
    }
  });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
  });
        dialogBuilder.show();


      }
    });




  }

//  MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
//        dialogBuilder.setTitle("Delete User");
//        dialogBuilder.setMessage("Are you sure you want to delete this user ?");
//        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//      holder.deleteUserData(userList.get(position).getUid());
//    }
//  });
//        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//
//    }
//  });
//        dialogBuilder.show();
//

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

    @Override
  public int getItemCount() {
    return userList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private ConstraintLayout container;
    private TextView postDescView;
    private TextView usernameTextView;
    private TextView postDateView;
    private ImageView postImgView;
    private CircleImageView userImgView;
    private TextView reportDescEditTextView;
    private Button postReportButton;
    private TextView noEditTextView;
    private TextView discardReportButton;
    private TextView deleteUserButton;
    private TextView deletePostButton;
    private FirebaseFunctions firebaseFunctions;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;

      postDescView = mView.findViewById(R.id.post_desc);
      usernameTextView = mView.findViewById(R.id.user_name);
      postDateView = mView.findViewById(R.id.post_date);
      postImgView = mView.findViewById(R.id.post_img);
      userImgView = mView.findViewById(R.id.user_img);
      noEditTextView = mView.findViewById(R.id.not_edit_textView);
      reportDescEditTextView = mView.findViewById(R.id.report_desc);
      postReportButton = mView.findViewById(R.id.report_post_btn);
      container = mView.findViewById(R.id.report_container);

      discardReportButton = mView.findViewById(R.id.post_request);
      deletePostButton = mView.findViewById(R.id.post_report2);
      deleteUserButton = mView.findViewById(R.id.unsave_post);


      firebaseFunctions = FirebaseFunctions.getInstance();
    }


//    public void setContainer(ScrollView container) {
//      this.container = container;
//    }

    public void setPostDescView(String postDesc) {
      this.postDescView.setText(postDesc);
    }

    public void setUsername(String username) {
      this.usernameTextView.setText(username);
    }

//    public void setPostUserNameView(TextView postUserNameView) {
//      this.postUserNameView = postUserNameView;
//    }


    public void setPostDateView(String postDate) {
      this.postDateView.setText(postDate);
    }

//    public void setPostImgView(ImageView postImgView) {
//      Glide.with(context)
//              .load(postImgView)
//              .placeholder(R.drawable.rectangle_1)
//              .into(this.postImgView);
//    }

    public void setUserImg(String userImgView) {
      Glide.with(context)
              .load(userImgView)
              .placeholder(R.drawable.default_profile)
              .into(this.userImgView);
    }
    public void setPostImg(String imgUri) {
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.rectangle_1)
              .into(this.postImgView);
    }

    public void setReportDescEditTextView(String reportDesc) {
      this.noEditTextView.setText(reportDesc);
      this.reportDescEditTextView.setVisibility(View.INVISIBLE);
    }

    public void hidePostReportButton() {
      postReportButton.setVisibility(View.INVISIBLE);
    }






//    public void setDiscardReportButton(TextView discardReportButton) {
//      this.discardReportButton = discardReportButton;
//    }
//
//    public void setDeleteUserButton(TextView deleteUserButton) {
//      this.deleteUserButton = deleteUserButton;
//    }
//
//    public void setDeletePostButton(TextView deletePostButton) {
//      this.deletePostButton = deletePostButton;
//    }


    private Task<String> deleteUser(String uid) {
      // Create the arguments to the callable function.
      Map<String, String> data = new HashMap<>();
      data.put("uid", uid);
      return firebaseFunctions
              .getHttpsCallable("deleteUser")
              .call(data)
              .continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                  // This continuation runs on either success or failure, but if the task
                  // has failed then getResult() will throw an Exception which will be
                  // propagated down.
                  String result = (String) task.getResult().getData();
                  return result;
                }
              });
    }

    private void deleteUserData(final String uid) {
      container.removeAllViews();//hides removed item
      FirebaseFirestore.getInstance().collection("Posts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
          for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            if (documentSnapshot.exists()) {
              if (documentSnapshot.get("user_id").equals(auth.getUid())) {
                FirebaseFirestore.getInstance().collection("Posts").document(documentSnapshot.getId()).delete();
              }
            }
          }
          FirebaseFirestore.getInstance().collection("Chats").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
              {
                if (documentSnapshot.get("op_id").equals(uid)|| documentSnapshot.get("receiver_id").equals(uid))
                {
                  FirebaseFirestore.getInstance().collection("Chats").document(documentSnapshot.getId()).delete();
                }
              }
              FirebaseFirestore.getInstance().collection("Users").document(uid).delete();
            }
          });

        }
      });
      deleteUser(uid);
    }
  }
}