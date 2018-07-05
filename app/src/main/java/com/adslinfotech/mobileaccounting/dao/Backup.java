package com.adslinfotech.mobileaccounting.dao;

import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_NAME_START;
import java.io.Serializable;

public class Backup implements Serializable, Comparable<Backup> {
  private String Date;
  private String Name;
  private String Path;

  public String getName() {
    return this.Name;
  }

  public void setName(String name) {
    this.Name = name;
  }

  public String getDate() {
    return this.Date;
  }

  public void setDate(String date) {
    this.Date = date;
  }

  public int compareTo(Backup another) {
    String second = another.getDate();
    if (!this.Name.startsWith(FILE_NAME_START.SA) || !this.Name.contains(FILE_EXTENSION.BACKUP)) {
      return 1;
    }
    if (second.startsWith(FILE_NAME_START.SA) && second.contains(FILE_EXTENSION.BACKUP)) {
      return this.Date.compareTo(second) * -1;
    }
    return -1;
  }

  public String getPath() {
    return this.Path;
  }

  public void setPath(String path) {
    this.Path = path;
  }

  public String toString() {
    return this.Name + this.Date;
  }
}
