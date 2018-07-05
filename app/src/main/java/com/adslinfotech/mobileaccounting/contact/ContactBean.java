package com.adslinfotech.mobileaccounting.contact;

import java.io.Serializable;

public class ContactBean implements Serializable {
  private String email;
  private String name;
  private String phoneNo;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNo() {
    return this.phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
