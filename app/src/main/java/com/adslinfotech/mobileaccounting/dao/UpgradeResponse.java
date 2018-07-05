package com.adslinfotech.mobileaccounting.dao;

import com.google.gson.annotations.SerializedName;

public class UpgradeResponse {
  @SerializedName("message")
  private String message;
  @SerializedName("status")
  private boolean status;

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isStatus() {
    return this.status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }
}
