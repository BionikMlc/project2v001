package com.example.project2v001.blog_post_module;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList)
    {
        this.postList = postList;
    }

/******************************
        Adapter Methods
 *****************************/

//onCreateViewHolder : inflates the layout with layout of single item that we created.
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_post_item,parent,false);
        return new ViewHolder(view);
    }
//onBindViewHolder : inflates the layout with layout of single item that we created.
    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, int position) {
        String descText = postList.get(position).getDesc();

//        String userName = postList.get(position).getName();
        holder.setPostDesc(descText);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.setPostUserName(task.getResult().get("name").toString());

            }
        });

        long timeInMS = postList.get(position).getTimestamp().getTime();
        String time = DateFormat.format("yyyy-MM-dd HH:mm",new Date(timeInMS)).toString();
        holder.setPostDate(time);

    }
//getItemCount : gets the size of data we retrieved.
    @Override
    public int getItemCount() {
        return postList.size();
    }

/******************************
     End Adapter Methods
 *****************************/

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView postDescView;
        private TextView postUserNameView;
        private TextView postDateView;
        private ImageView postImgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setPostDesc(String descText)
        {
            postDescView = mView.findViewById(R.id.post_desc);
            postDescView.setText(descText);
        }
        public void setPostUserName(String userName)
        {
            postUserNameView = mView.findViewById(R.id.user_name);
            postUserNameView.setText(userName);
        }
//        public void setPostImg(String img)
//        {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//            postImgView = mView.findViewById(R.id.post_img);
//            storageReference.child("profile_images").child(userId + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    Glide.with()
//                            .load(task.getResult().toString())
//                            .placeholder(R.drawable.default_profile)
//                            .into(profileImage);
//}

        public void setPostDate(String date)
        {
            postDateView = mView.findViewById(R.id.post_date);
            postDateView.setText(date);
        }
    }


}
