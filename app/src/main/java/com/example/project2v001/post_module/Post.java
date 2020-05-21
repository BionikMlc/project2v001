package com.example.project2v001.post_module;


import java.util.Date;
import java.util.List;

public class Post extends PostId {
  //private thump img_url
  private String user_id, desc, img;
  private String post_type;
  private Date timestamp;
  private List<String> requests;
  private String reserved_for;

  public Post() {
  } // essential for firebase to function properly.

  public Post(String user_id, String desc, Date timestamp, String post_type, String img, String reserved_for, List<String> requests) {
    this.user_id = user_id;
    this.desc = desc;
    this.img = img;
    this.post_type = post_type;
    this.timestamp = timestamp;
    this.requests = requests;
    this.reserved_for = reserved_for;

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

  public String getPost_type() {
    return this.post_type;
  }

  public void setPost_type(String post_type) {
    this.post_type = post_type;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public List<String> getRequests() {
    return requests;
  }

  public String getReserved_for() {
    return reserved_for;
  }

public void setReserved_for(String reserved_for){this.reserved_for = reserved_for;}
}
