package com.adslinfotech.mobileaccounting.dao;

import java.io.Serializable;

public class Transaction implements Serializable, Comparable<Transaction> {
  private int dr_cr;
  private int mAId;
  private String mAccName;
  private String mBalance;
  private boolean mChecked;
  private double mCreditAmount;
  private String mDate;
  private double mDebitAmount;
  private byte[] mImage;
  private String mNarration;
  private String mRemark;
  private int mTId;
  private String mType;
  private int mUserId;

  public int getAccountId() {
    return this.mAId;
  }

  public void setAId(int mAId) {
    this.mAId = mAId;
  }

  public int getDr_cr() {
    return this.dr_cr;
  }

  public void setDr_cr(int dr_cr) {
    this.dr_cr = dr_cr;
  }

  public String getRemark() {
    return this.mRemark;
  }

  public void setRemark(String mRemark) {
    this.mRemark = mRemark;
  }

  public String getNarration() {
    return this.mNarration;
  }

  public void setNarration(String mNarration) {
    this.mNarration = mNarration;
  }

  public String getDate() {
    return this.mDate;
  }

  public void setDate(String mDate) {
    this.mDate = mDate;
  }

  public int getUserId() {
    return this.mUserId;
  }

  public void setUserId(int mUserId) {
    this.mUserId = mUserId;
  }

  public double getCraditAmount() {
    return this.mCreditAmount;
  }

  public void setCraditAmount(double amt) {
    this.mCreditAmount = amt;
  }

  public double getDebitAmount() {
    return this.mDebitAmount;
  }

  public void setDebitAmount(double amt) {
    this.mDebitAmount = amt;
  }

  public int getTransactionId() {
    return this.mTId;
  }

  public void setTransactionId(int mTId) {
    this.mTId = mTId;
  }

  public String getType() {
    return this.mType;
  }

  public void setType(String mType) {
    this.mType = mType;
  }

  public byte[] getImage() {
    return this.mImage;
  }

  public void setImage(byte[] mImage) {
    this.mImage = mImage;
  }

  public boolean isChecked() {
    return this.mChecked;
  }

  public void setChecked(boolean mChecked) {
    this.mChecked = mChecked;
  }

  public String getAccName() {
    return this.mAccName;
  }

  public void setAccName(String mAccName) {
    this.mAccName = mAccName;
  }

  public String getBalance() {
    return this.mBalance;
  }

  public void setBalance(String mBalance) {
    this.mBalance = mBalance;
  }

  public String toString() {
    String str;
    if (this.dr_cr == 1) {
      str = "Credit" + this.mCreditAmount;
    } else {
      str = "Debit" + this.mDebitAmount;
    }
    return this.mAccName + this.mDate + str + this.mType + this.mNarration + this.mRemark;
  }

  public int compareTo(Transaction another) {
    if (this.mCreditAmount > this.mDebitAmount) {
      if (another.getCraditAmount() < this.mCreditAmount) {
        return -1;
      }
      if (another.getCraditAmount() > this.mCreditAmount) {
        return 1;
      }
    } else if (another.getDebitAmount() < this.mDebitAmount) {
      return -1;
    } else {
      if (another.getDebitAmount() > this.mDebitAmount) {
        return 1;
      }
    }
    return 0;
  }
}
