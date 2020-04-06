package com.example.project2v001.post_module;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2v001.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;
    private Context context;
    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    /******************************
     Adapter Methods
     *****************************/

//onCreateViewHolder : inflates the layout with layout of single item that we created.
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    //onBindViewHolder : inflates the layout with layout of single item that we created.
    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        String descText = postList.get(position).getDesc();
        holder.setPostDesc(descText);

        //gets user name and sets it to the user name textView
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.setPostUserName(task.getResult().get("name").toString());
                holder.setUserImg(task.getResult().get("img").toString());
            }
        });

        long timeInMS = postList.get(position).getTimestamp().getTime();
        String time = DateFormat.format("yyyy/MM/dd HH:mm", new Date(timeInMS)).toString();
        holder.setPostDate(time);

        String postImg = postList.get(position).getImg();
        holder.setPostImg(postImg);

        //Request Feature
        final String user_id = FirebaseAuth.getInstance().getUid();
        final String postId = postList.get(position).postId;

//        firebaseFirestore.collection("Posts/"+postId+"/Requests").document(user_id).addSnapshotListener( new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if(e == null){
//                    if(!documentSnapshot.exists())
//                    {
//
//                    } else {
//
//                    }
//                } else
//                    {
//                        //handle errors
//                    }
//            }
//        });

        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_id = FirebaseAuth.getInstance().getUid();
                final String postId = postList.get(position).postId;

                if(!holder.request.getText().toString().equals("Request"))
                {
                    holder.request.setText("Request");
                    holder.request.setTextColor(holder.request.getResources().getColor(R.color.common_google_signin_btn_text_light_default));
                }
                else {
                    holder.request.setText("Requested");
                    holder.request.setTextColor(holder.request.getResources().getColor(R.color.colorAccent));
                }


                firebaseFirestore.collection("Posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> req = new HashMap<>();
                        List<String> userIds = new ArrayList<>();
                        userIds = (List<String>) task.getResult().get("requests");

                        if(!userIds.contains(user_id))
                        {
                            userIds.add(user_id);

                        }
                        else {
                            userIds.remove(user_id);

                        }

                        req.put("requests",userIds);
                        firebaseFirestore.collection("Posts").document(postId).set(req, SetOptions.merge());
                    }
                });




            }
        });
    }

    //getItemCount : gets the size of data we retrieved.
    @Override
    public int getItemCount() {
        return postList.size();
    }

    /******************************
     End Adapter Methods
     *****************************/

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView postDescView;
        private TextView postUserNameView;
        private TextView postDateView;
        private ImageView postImgView;
        private CircleImageView userImgView;
        private TextView request;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            request =  mView.findViewById(R.id.post_request);
        }

        public void setPostDesc(String descText) {
            postDescView = mView.findViewById(R.id.post_desc);
            postDescView.setText(descText);
        }

        public void setPostUserName(String userName) {
            postUserNameView = mView.findViewById(R.id.user_name);
            postUserNameView.setText(userName);
        }

        public void setUserImg(String imgUri) {

            userImgView = mView.findViewById(R.id.user_img);
            Glide.with(context)
                    .load(imgUri)
                    .placeholder(R.drawable.rectangle_1)
                    .into(userImgView);
        }

        public void setPostImg(String imgUri) {
            postImgView = mView.findViewById(R.id.post_img);
            Glide.with(context)
                    .load(imgUri)
                    .placeholder(R.drawable.default_profile)
                    .into(postImgView);
        }

        public void setPostDate(String date) {
            postDateView = mView.findViewById(R.id.post_date);
            postDateView.setText(date);
        }
    }


}
