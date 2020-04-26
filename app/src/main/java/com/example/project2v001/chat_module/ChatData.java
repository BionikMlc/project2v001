package com.example.project2v001.chat_module;

import java.util.Date;

public class ChatData extends ChatId{
  private String op_id;
  private String user_id;
  private String receiver_id;
  private String message;
  private Date timestamp;

  public ChatData() { }

  public ChatData(String op_id, String user_id, String receiver_id, Date timestamp, String message) {
    this.op_id = op_id;
    this.user_id = user_id;
    this.receiver_id = receiver_id;
    this.message = message;
    this.timestamp = timestamp;
  }

  public String getOp_id() {
    return op_id;
  }

  public String getUser_id() {
    return user_id;
  }

  public String getMessage() {
    return message;
  }

  public Date getTimestamp() {
    return timestamp;
  }
  public String getReceiver_id() {return receiver_id;}

}
