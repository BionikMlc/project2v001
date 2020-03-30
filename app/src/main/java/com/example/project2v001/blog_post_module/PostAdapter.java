package com.example.project2v001.blog_post_module;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_post_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    //onBindViewHolder : inflates the layout with layout of single item that we created.
    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, int position) {

        String descText = postList.get(position).getDesc();
        holder.setPostDesc(descText);

        //gets user name and sets it to the user name textView
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
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
//            String user_id = FirebaseAuth.getInstance().getUid();
            userImgView = mView.findViewById(R.id.user_img);
            Glide.with(context)
                    .load(imgUri)
                    .placeholder(R.drawable.default_profile)
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
