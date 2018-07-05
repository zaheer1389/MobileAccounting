package com.adslinfotech.mobileaccounting.dao;

public class Account extends SearchDao {
  private int mAccountId;
  private int mAccountType;
  private String mBalance;
  private byte[] mImage;
  private String mName;
  private String mPEmail;
  private String mPMobile;
  private String mRemark;
  private String mTypeName;
  private int mUserId;

  public int getUserId() {
    return this.mUserId;
  }

  public void setUserId(int mUId) {
    this.mUserId = mUId;
  }

  public String getName() {
    return this.mName;
  }

  public void setName(String mPName) {
    this.mName = mPName;
  }

  public String getEmail() {
    return this.mPEmail;
  }

  public void setEmail(String mPEmail) {
    this.mPEmail = mPEmail;
  }

  public String getMobile() {
    return this.mPMobile;
  }

  public void setMobile(String mPMobile) {
    this.mPMobile = mPMobile;
  }

  public String getRemark() {
    return this.mRemark;
  }

  public void setRemark(String mRemark) {
    this.mRemark = mRemark;
  }

  public int getAccountId() {
    return this.mAccountId;
  }

  public void setAccountId(int mAccountId) {
    this.mAccountId = mAccountId;
  }

  public byte[] getImage() {
    return this.mImage;
  }

  public void setImage(byte[] mImage) {
    this.mImage = mImage;
  }

  public int getCategoryId() {
    return this.mAccountType;
  }

  public void setCategoryId(int mAccountType) {
    this.mAccountType = mAccountType;
  }

  public String getCategory() {
    return this.mTypeName;
  }

  public void setCategory(String mTypeName) {
    this.mTypeName = mTypeName;
  }

  public String getBalance() {
    return this.mBalance;
  }

  public void setBalance(String mBal) {
    this.mBalance = mBal;
  }

  public String toString() {
    return this.mName;
  }
}
