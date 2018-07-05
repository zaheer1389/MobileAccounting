package com.adslinfotech.mobileaccounting.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.adslinfotech.mobileaccounting.database.Query.ACCOUNT;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DataBaseAdapter {
  public static ArrayList<String> getColumns(int i) {
    ArrayList<String> mClm = new ArrayList();
    String[] clm = null;
    switch (i) {
      case 0:
        clm = new String[]{"UserID", "UserName", "Name", "Email", "Mobile", "Password", "Image"};
        break;
      case 1:
        clm = new String[]{"AID", "UserID", ACCOUNT.NAME, "PersonEmail", "PersonMobile", "Remark", "Image", "Type", "TypeName"};
        break;
      case 2:
        clm = new String[]{"TID", "AID", "UserID", "Credit_Amount", "Debit_Amount", "dr_cr", "Remark", "Narration", "Date"};
        break;
      case 3:
        clm = new String[]{"RID", "AID", "UserId", "ReminderDate", "ReminderDiscription", "BeforeDays", "Remark", "ReminderMode"};
        break;
      case 4:
        clm = new String[]{"AID", ACCOUNT.NAME, "Description"};
        break;
      case 5:
        clm = new String[]{"TypeId", "TypeName"};
        break;
    }
    mClm.addAll(Arrays.asList(clm));
    return mClm;
  }

  public static boolean insertData(SQLiteDatabase db, Cursor cursor, String table, int tableIndex) {
    Exception e;
    boolean isInserted = false;
    SimpleDateFormat format = null;
    SimpleDateFormat format1 = null;
    deleteData(db, table);
    cursor.moveToPosition(-1);
    boolean isFirst = true;
    int[] arr = null;
    ContentValues values = new ContentValues();
    while (cursor.moveToNext()) {

      try {

        SimpleDateFormat format2;
        switch (tableIndex) {
          case 0:
            if (isFirst) {
              arr = new int[]{cursor.getColumnIndex("UserID"), cursor.getColumnIndex("UserName"), cursor.getColumnIndex("Name"), cursor.getColumnIndex("Email"), cursor.getColumnIndex("Mobile"), cursor.getColumnIndex("Password"), cursor.getColumnIndex("Image"), cursor.getColumnIndex("BackupDate")};
            }
            values.put("UserID", Integer.valueOf(cursor.getInt(arr[0])));
            values.put("UserName", cursor.getString(arr[1]));
            values.put("Name", cursor.getString(arr[2]));
            values.put("Email", cursor.getString(arr[3]));
            values.put("Mobile", cursor.getString(arr[4]));
            values.put("Password", cursor.getString(arr[5]));
            values.put("Image", cursor.getBlob(arr[6]));
            if (db.insertWithOnConflict(table, null, values, 5) != -1) {
              isInserted = true;
            }
            return isInserted;
          case 1:
            if (isFirst) {
              isFirst = false;
              arr = new int[]{cursor.getColumnIndex("AID"), cursor.getColumnIndex("UserID"), cursor.getColumnIndex(ACCOUNT.NAME), cursor.getColumnIndex("PersonEmail"), cursor.getColumnIndex("PersonMobile"), cursor.getColumnIndex("Remark"), cursor.getColumnIndex("Image"), cursor.getColumnIndex("Type"), cursor.getColumnIndex("TypeName")};
            }
            values.put("AID", Integer.valueOf(cursor.getInt(arr[0])));
            values.put("UserID", Integer.valueOf(cursor.getInt(arr[1])));
            values.put(ACCOUNT.NAME, cursor.getString(arr[2]));
            values.put("PersonEmail", cursor.getString(arr[3]));
            values.put("PersonMobile", cursor.getString(arr[4]));
            values.put("Remark", cursor.getString(arr[5]));
            values.put("Image", cursor.getBlob(arr[6]));
            values.put("Type", Integer.valueOf(cursor.getInt(arr[7])));
            values.put("TypeName", cursor.getString(arr[8]));
            break;
          case 2:
            if (isFirst) {
              SimpleDateFormat format12 = null;
              isFirst = false;
              format2 = new SimpleDateFormat("MM/dd/yyyy");
              try {
                format12 = new SimpleDateFormat(DateFormat.DB_DATE);
              } catch (Exception e2) {
                e = e2;
                format = format2;
                e.printStackTrace();
              }
              try {
                arr = new int[]{cursor.getColumnIndex("Date"), cursor.getColumnIndex("TID"), cursor.getColumnIndex("AID"), cursor.getColumnIndex("UserID"), cursor.getColumnIndex("Credit_Amount"), cursor.getColumnIndex("Debit_Amount"), cursor.getColumnIndex("dr_cr"), cursor.getColumnIndex("Remark"), cursor.getColumnIndex("Narration"), cursor.getColumnIndex("EntryDate"), cursor.getColumnIndex("LongDate")};
                format1 = format12;
                format = format2;
              } catch (Exception e3) {
                e = e3;
                format1 = format12;
                format = format2;
                e.printStackTrace();
              }
            }
            String date = cursor.getString(arr[0]);
            values.put("TID", Integer.valueOf(cursor.getInt(arr[1])));
            values.put("AID", Integer.valueOf(cursor.getInt(arr[2])));
            values.put("UserID", Integer.valueOf(cursor.getInt(arr[3])));
            values.put("Credit_Amount", Double.valueOf(cursor.getDouble(arr[4])));
            values.put("Debit_Amount", Double.valueOf(cursor.getDouble(arr[5])));
            values.put("dr_cr", Integer.valueOf(cursor.getInt(arr[6])));
            values.put("Remark", cursor.getString(arr[7]));
            values.put("Narration", cursor.getString(arr[8]));
            if (arr[9] != -1) {
              try {
                values.put("EntryDate", cursor.getString(arr[9]));
                values.put("Date", cursor.getString(arr[0]));
                values.put("LongDate", cursor.getString(arr[10]));
              } catch (Exception e4) {
                e4.printStackTrace();
              }
            } else {
              try {
                Date d = format.parse(date);
                values.put("EntryDate", format1.format(d));
                values.put("Date", date);
                values.put("LongDate", Long.valueOf(d.getTime()));
              } catch (Exception e42) {
                e42.printStackTrace();
                values.put("EntryDate", format1.format(new Date()));
              }
            }
            try {
              values.put("Image", cursor.getBlob(cursor.getColumnIndex("Image")));
              break;
            } catch (Exception e5) {
              break;
            }
          case 3:
            if (isFirst) {
              isFirst = false;
              format2 = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
              arr = new int[]{cursor.getColumnIndex("RID"), cursor.getColumnIndex("AID"), cursor.getColumnIndex("UserID"), cursor.getColumnIndex("ReminderDate"), cursor.getColumnIndex("ReminderDiscription"), cursor.getColumnIndex("BeforeDays"), cursor.getColumnIndex("Remark"), cursor.getColumnIndex("ReminderMode"), cursor.getColumnIndex("EntryDate")};
              format = format2;
            }
            values.put("RID", Integer.valueOf(cursor.getInt(arr[0])));
            values.put("AID", Integer.valueOf(cursor.getInt(arr[1])));
            values.put("UserID", Integer.valueOf(cursor.getInt(arr[2])));
            if (arr[8] != -1) {
              values.put("EntryDate", format.format(new Date(cursor.getLong(arr[3]))));
            } else {
              values.put("EntryDate", cursor.getString(arr[8]));
            }
            values.put("ReminderDate", Long.valueOf(cursor.getLong(arr[3])));
            values.put("ReminderDiscription", cursor.getString(arr[4]));
            values.put("BeforeDays", cursor.getString(arr[5]));
            values.put("Remark", cursor.getString(arr[6]));
            values.put("ReminderMode", Integer.valueOf(cursor.getInt(arr[7])));
            break;
          case 4:
            values.put("AID", Integer.valueOf(cursor.getInt(0)));
            values.put(ACCOUNT.NAME, cursor.getString(1));
            values.put("Description", cursor.getString(2));
            break;
          case 5:
            values.put("TypeId", Integer.valueOf(cursor.getInt(0)));
            values.put("TypeName", cursor.getString(1));
            break;
        }
      } catch (Exception e6) {
        e6.printStackTrace();
      }
    }
    if (db.insert(table, null, values) != -1) {
      isInserted = true;
    }

    return isInserted;

  }

  public static void updateData(SQLiteDatabase db, Cursor cursor, String table, int tableIndex) {
    SimpleDateFormat format;
    Exception e;
    SimpleDateFormat format2 = null;
    SimpleDateFormat format1 = null;
    cursor.moveToPosition(-1);
    boolean isFirst = true;
    int[] arr = null;
    String clause = "";
    ContentValues values = new ContentValues();
    while (cursor.moveToNext()) {

      switch (tableIndex) {
        case 1:
          if (isFirst) {
            isFirst = false;
            format = new SimpleDateFormat("MM/dd/yyyy");
            try {
              SimpleDateFormat format12 = new SimpleDateFormat(DateFormat.DB_DATE);
              try {
                arr = new int[]{cursor.getColumnIndex("Date"), cursor.getColumnIndex("TID"), cursor.getColumnIndex("LongDate")};
                format1 = format12;
                format2 = format;
              } catch (Exception e2) {
                e = e2;
                format1 = format12;
                format2 = format;
                e.printStackTrace();
              }
            } catch (Exception e3) {
              e = e3;
              format2 = format;
              e.printStackTrace();
            }
          }
          clause = "TID = " + cursor.getInt(arr[1]);
          try {
            values.put("EntryDate", format1.format(format2.parse(cursor.getString(arr[0]))));
            break;
          } catch (Exception e4) {
            try {
              values.put("EntryDate", format1.format(new Date(cursor.getLong(2))));
              break;
            } catch (Exception e5) {
              values.put("EntryDate", format1.format(new Date()));
              break;
            }
          }
        case 2:
          if (isFirst) {
            isFirst = false;
            format = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
            arr = new int[]{cursor.getColumnIndex("RID"), cursor.getColumnIndex("ReminderDate")};
            format2 = format;
          }
          try {
            clause = "RID = " + cursor.getInt(arr[0]);
            values.put("EntryDate", format2.format(new Date(cursor.getLong(arr[1]))));
            break;
          } catch (Exception e6) {
            e = e6;
            e.printStackTrace();
          }
      }
    }
    db.update(table, values, clause, null);
    return;

  }

  private static void deleteData(SQLiteDatabase db, String table) {
    try {
      Log.d("DBAdapter", "delete table = " + table);
      db.delete(table, null, null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("delete table = " + table, "data not deleted exception");
    }
  }
}
