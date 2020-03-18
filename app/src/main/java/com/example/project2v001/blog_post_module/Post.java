package com.example.project2v001.blog_post_module;

//import java.sql.Timestamp;

public class Post {
    //private thump img_url
    private     String user_id,desc;
    //private    URL img_url;
    private     int postType;
//    private     Timestamp  timestamp;
    public Post(){}
    public Post(String user_id, String desc, int postType) {
        this.user_id = user_id;
        this.desc = desc;
//        this.img_url = img_url;
        this.postType = postType;
//        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public URL getImg_url() {
//        return img_url;
//    }
//
//    public void setImg_url(URL img_url) {
//        this.img_url = img_url;
//    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

//    public Timestamp getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Timestamp timestamp) {
//        this.timestamp = timestamp;
//    }



}
