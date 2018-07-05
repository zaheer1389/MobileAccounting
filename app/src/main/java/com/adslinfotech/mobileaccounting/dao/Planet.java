package com.adslinfotech.mobileaccounting.dao;

public class Planet {
  private boolean checked = false;
  private String name = "";
  private String path = "";

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isChecked() {
    return this.checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public String toString() {
    return this.name;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
