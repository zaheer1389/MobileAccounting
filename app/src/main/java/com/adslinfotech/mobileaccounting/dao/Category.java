package com.adslinfotech.mobileaccounting.dao;

public class Category extends SearchDao {
  private int mId;
  private String mName;

  public int getId() {
    return this.mId;
  }

  public void setId(int mId) {
    this.mId = mId;
  }

  public String getName() {
    return this.mName;
  }

  public void setName(String mName) {
    this.mName = mName;
  }

  public String toString() {
    return this.mName;
  }
}
