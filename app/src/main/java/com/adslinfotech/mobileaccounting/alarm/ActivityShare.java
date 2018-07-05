package com.adslinfotech.mobileaccounting.alarm;

import android.app.Activity;
import android.os.Bundle;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.gmail.ShareHelper;
import com.adslinfotech.mobileaccounting.ui.SessionManager;

public class ActivityShare extends Activity {
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new ShareHelper(this, SessionManager.getName() + "Suggest you Simple Accounting App for Andoroid Mobile", "This app works amazing and i am very much satisfied with this app. It helps me to manage all my accounting on finger tips.\nI would like to suggest you to please download or install this app please visit\n http://bit.ly/1LGUjOE", getResources().getString(R.string.ShartAppHtml), "http://bit.ly/1LGUjOE", "http://bit.ly/1LGUjOE").share();
  }
}
