package com.adslinfotech.mobileaccounting.dao;

import java.io.Serializable;

public class NoteDao implements Serializable {
  private int Aid;
  private String Descr;
  private String Pname;

  public String getHeading() {
    return this.Pname;
  }

  public void setHeading(String pname) {
    this.Pname = pname;
  }

  public String getDescr() {
    return this.Descr;
  }

  public void setDescr(String descr) {
    this.Descr = descr;
  }

  public int getAccount_Id() {
    return this.Aid;
  }

  public void setAccount_Id(int aid) {
    this.Aid = aid;
  }
}
