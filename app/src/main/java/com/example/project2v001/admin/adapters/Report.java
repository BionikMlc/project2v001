package com.example.project2v001.admin.adapters;


public class Report {
  private String img;
  private String name;
  private String desc;
  private String reportDesc;
  private String timestamp;
  private String userImg;
  private String postID;



  public Report(String img, String name, String desc, String reportDesc, String timestamp, String userImg, String postID) {
    this.img = img;
    this.name = name;
    this.desc = desc;
    this.reportDesc = reportDesc;
    this.timestamp = timestamp;
    this.userImg = userImg;
    this.postID = postID;
  }

  public Report() {
  }

  public String getDesc() {
    return desc;
  }

  public String getReportDesc() {
    return reportDesc;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getUserImg() {
    return userImg;
  }


  public String getImg() {
    return img;
  }

  public String getName() {
    return name;
  }

  public String getPostID() {
    return postID;
  }

}
