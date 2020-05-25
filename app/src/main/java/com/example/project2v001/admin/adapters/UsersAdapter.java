package com.example.project2v001.admin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
  private static final String TAG = "emailTag";
  private List<User> userList;
  private Context context;


  public UsersAdapter(List<User> userList) {
    this.userList = userList;
  }

  @NonNull
  @Override
  public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
    context = parent.getContext();

    return new ViewHolder(view);

  }

  @Override
  public void onBindViewHolder(@NonNull final UsersAdapter.ViewHolder holder, final int position) {

    Log.i(TAG, "onBindViewHolder: "+userList.get(position).getName());
    holder.setUsername(userList.get(position).getName());
    holder.setUserEmail(userList.get(position).getEmail());
    holder.setUserImg(userList.get(position).getImg());

    holder.deleteUserImageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Delete User");
        dialogBuilder.setMessage("Are you sure you want to delete this user ?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
//            holder.container.removeAllViews();
            holder.container.setVisibility(View.GONE);
            userList.remove(position);
            holder.deleteUserData(userList.get(position).getUid());
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

  @Override
  public int getItemCount() {
    return userList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private ConstraintLayout container;
    private ImageView userImgView;
    private TextView emailTextView;
    private TextView usernameTextView;
    private ImageButton deleteUserImageButton;
    private FirebaseFunctions firebaseFunctions;
    private FirebaseAuth auth;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      mView = itemView;
      container = mView.findViewById(R.id.users_list_item_container);
      deleteUserImageButton = mView.findViewById(R.id.users_list_item_delete_button);
      firebaseFunctions = FirebaseFunctions.getInstance();
      auth = FirebaseAuth.getInstance();
    }


    public void setUserImg(String imgUri) {
      userImgView = mView.findViewById(R.id.users_list_item_user_img);
      Glide.with(context)
              .load(imgUri)
              .placeholder(R.drawable.rectangle_1)
              .into(userImgView);
    }

    public void setUsername(String username) {
      usernameTextView = mView.findViewById(R.id.users_list_item_user_name);
      usernameTextView.setText(username);
    }

    public void setUserEmail(String email) {
      emailTextView = mView.findViewById(R.id.users_list_item_user_email);
      emailTextView.setText(email);
    }

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
              if (documentSnapshot.get("user_id").equals(uid)) {
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
