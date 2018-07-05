package com.adslinfotech.mobileaccounting.dao;

import java.io.Serializable;

public abstract class SearchDao implements Serializable {
  public abstract String getName();

  public abstract void setName(String str);
}
