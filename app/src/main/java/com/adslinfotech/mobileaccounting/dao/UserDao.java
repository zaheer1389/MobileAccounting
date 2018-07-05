package com.adslinfotech.mobileaccounting.dao;

public class UserDao {
  private String mCity;
  private String mDeviceEmail;
  private String mEmail;
  private byte[] mImage;
  private String mMobile;
  private String mName;
  private String mPassword;
  private String mRemark;
  private int mUserID;
  private String mUserName;

  public String getName() {
    return this.mName;
  }

  public void setName(String mName) {
    this.mName = mName;
  }

  public String getEmail() {
    return this.mEmail;
  }

  public void setEmail(String mEmail) {
    this.mEmail = mEmail;
  }

  public String getMobile() {
    return this.mMobile;
  }

  public void setMobile(String mMobile) {
    this.mMobile = mMobile;
  }

  public String getPassword() {
    return this.mPassword;
  }

  public void setPassword(String mPassword) {
    this.mPassword = mPassword;
  }

  public String getUserName() {
    return this.mUserName;
  }

  public void setUserName(String mUserName) {
    this.mUserName = mUserName;
  }

  public int getUserID() {
    return this.mUserID;
  }

  public void setUserID(int mUserID) {
    this.mUserID = mUserID;
  }

  public byte[] getImage() {
    return this.mImage;
  }

  public void setImage(byte[] b) {
    this.mImage = b;
  }

  public String getDeviceEmail() {
    return this.mDeviceEmail;
  }

  public void setDeviceEmail(String mDeviceEmail) {
    this.mDeviceEmail = mDeviceEmail;
  }

  public String getRemark() {
    return this.mRemark;
  }

  public void setRemark(String mRemark) {
    this.mRemark = mRemark;
  }

  public String getCity() {
    return this.mCity;
  }

  public void setCity(String mCity) {
    this.mCity = mCity;
  }
}
