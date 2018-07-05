package com.adslinfotech.mobileaccounting.activities;

import android.os.Environment;

import com.adslinfotech.mobileaccounting.database.DataBaseHandler;
import com.adslinfotech.mobileaccounting.drive.BaseDriveClass;

import java.io.File;

public class DriveSynchActivity extends BaseDriveClass {
  public static final String DATABASE_NAME = DataBaseHandler.name;
  private static final File DATA_DIRECTORY_DATABASE = new File(Environment.getDataDirectory() + "/data/" + "com.adslinfotech.mobileaccounting" + "/databases/" + DATABASE_NAME);
  private static final String TAG = "DriveSynchActivity";


}