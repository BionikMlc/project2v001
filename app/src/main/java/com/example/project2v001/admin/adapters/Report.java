package com.example.project2v001.admin.adapters;


public class Report extends ReportId {
  private String img;
  private String name;
  private String desc;
  private String reportDesc;
  private String timestamp;
  private String userImg;
  private String op_id;
  private String postID;


  public Report(String img, String name, String desc, String reportDesc, String timestamp, String userImg, String postID, String op_id) {
    this.img = img;
    this.name = name;
    this.desc = desc;
    this.reportDesc = reportDesc;
    this.timestamp = timestamp;
    this.userImg = userImg;
    this.postID = postID;
    this.op_id = op_id;
  }

  public Report() {
  }

  public String getOp_id() {
    return op_id;
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
