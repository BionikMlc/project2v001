package com.example.project2v001.chat_module;

import java.util.Date;
import java.util.List;

public class ChatData {
  private String op_id;
  private String user_id;
  private List<String> messages;
  private Date timestamp;

  public ChatData() { }

  public ChatData(String op_id, String user_id,Date timestamp, List<String> messages) {
    this.op_id = op_id;
    this.user_id = user_id;
    this.messages = messages;
    this.timestamp = timestamp;
  }

  public String getOp_id() {
    return op_id;
  }

  public String getUser_id() {
    return user_id;
  }

  public List<String> getMessages() {
    return messages;
  }

  public Date getTimestamp() {
    return timestamp;
  }

}
