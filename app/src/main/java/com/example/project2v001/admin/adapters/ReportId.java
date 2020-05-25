package com.example.project2v001.admin.adapters;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.Exclude;

public class ReportId {
  public String reportId;

  @Exclude
  public <T extends ReportId> T withId(@NotNull final String id)
  {
    this.reportId = id;
    return (T) this;
  }
}