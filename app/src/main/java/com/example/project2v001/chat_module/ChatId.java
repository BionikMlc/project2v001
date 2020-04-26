package com.example.project2v001.chat_module;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.Exclude;

public class ChatId {


    public String chatId;

    @Exclude
    public <T extends ChatId> T withId(@NotNull final String id)
    {
      this.chatId = id;
      return (T) this;
    }
  }

