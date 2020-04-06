package com.example.project2v001.post_module;


import java.util.Date;

public class Post extends PostId {
    //private thump img_url
    private String user_id,desc,img;
    private int postType;
    private Date timestamp;
//    private List<String> requests;
    public Post() {} // essential for firebase to function properly.
    public Post(String user_id, String desc, Date timestamp, int postType, String img) {
        this.user_id = user_id;
        this.desc = desc;
        this.img = img;
        this.postType = postType;
        this.timestamp = timestamp;
//        this.requests = new ArrayList<>();
//        this.requests.add(requests);

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
//    public void setRequests(String requests) {
//        this.requests.add(requests);
//    }
//
//    public List<String> getRequests() {
//        return requests;
//    }
}
