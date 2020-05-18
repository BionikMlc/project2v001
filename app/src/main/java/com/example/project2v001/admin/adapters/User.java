package com.example.project2v001.admin.adapters;

public class User {
  private String img;
  private String name;
  private String email;
  private String uid;

  public User() {
  }

  public User(String img, String name, String email, String uid) {
    this.img = img;
    this.name = name;
    this.email = email;
    this.uid = uid;
  }

  public String getImg() {
    return img;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getUid() {
    return uid;
  }


}
