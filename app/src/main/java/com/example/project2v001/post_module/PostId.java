package com.example.project2v001.post_module;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.Exclude;

public class PostId {
    public String postId;

    @Exclude
    public <T extends PostId> T withId(@NotNull final String id)
    {
            this.postId = id;
            return (T) this;
    }
}
