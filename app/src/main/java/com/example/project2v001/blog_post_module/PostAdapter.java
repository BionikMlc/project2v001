package com.example.project2v001.blog_post_module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2v001.R;

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
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        String descText = postList.get(position).getDesc();
        holder.setPostDesc(descText);
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setPostDesc(String descText)
        {
            postDescView = mView.findViewById(R.id.post_desc);
            postDescView.setText(descText);
        }
    }


}
