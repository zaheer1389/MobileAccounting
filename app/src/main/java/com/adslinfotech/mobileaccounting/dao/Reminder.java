package com.adslinfotech.mobileaccounting.dao;

import java.io.Serializable;
import java.util.Date;

public class Reminder implements Serializable {
  private int mAccType;
  private Date mAlarmDate;
  private int mBeforeDay;
  private String mDate;
  private String mDescription;
  private int mId;
  private String mRemark;

  public int getId() {
    return this.mId;
  }

  public void setId(int mId) {
    this.mId = mId;
  }

  public String getDate() {
    return this.mDate;
  }

  public void setDate(String mDate) {
    this.mDate = mDate;
  }

  public String getDescription() {
    return this.mDescription;
  }

  public void setDescription(String mDescription) {
    this.mDescription = mDescription;
  }

  public int getBeforeDay() {
    return this.mBeforeDay;
  }

  public void setBeforeDay(int mBeforeDay) {
    this.mBeforeDay = mBeforeDay;
  }

  public int getRmdType() {
    return this.mAccType;
  }

  public void setRmdType(int mAccType) {
    this.mAccType = mAccType;
  }

  public String getRemark() {
    return this.mRemark;
  }

  public void setRemark(String mRemark) {
    this.mRemark = mRemark;
  }

  public Date getAlarmDate() {
    return this.mAlarmDate;
  }

  public void setAlarmDate(Date mAlarmDate) {
    this.mAlarmDate = mAlarmDate;
  }
}
