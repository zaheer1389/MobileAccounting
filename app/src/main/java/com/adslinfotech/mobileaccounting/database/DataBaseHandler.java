package com.adslinfotech.mobileaccounting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class DataBaseHandler extends SQLiteOpenHelper {
  private static String TAG = "DataBaseHandler";
  public static String name = "accounting.db";
  private static int version = 2;

  public DataBaseHandler(Context context) {
    super(context, name, null, version);
  }

  public void onCreate(SQLiteDatabase db) {
    try {
      db.execSQL("create table Login(UserID  INTEGER PRIMARY KEY autoincrement, UserName String, Name String, Email String, Mobile String, Password String, Image BLOB, BackupDate Date)");
      db.execSQL("create table Account(AID Integer PRIMARY KEY autoincrement, UserID Integer, PersonName String not null unique, PersonEmail String, PersonMobile String, Remark String, Image BLOB, Type Integer, TypeName String)");
      db.execSQL("create table Transection(TID Integer PRIMARY KEY autoincrement, AID Integer, UserID Integer, Credit_Amount Integer, Debit_Amount Integer, dr_cr INTEGER, Remark String, Narration String, Image BLOB, EntryDate Date, Date String, LongDate unixtime)");
      db.execSQL("create table GeneralReminder(RID Integer PRIMARY KEY autoincrement, AID Integer, UserId Integer, EntryDate datetime, ReminderDate unixtime, ReminderDiscription String, BeforeDays String, Remark String, ReminderMode Integer)");
      db.execSQL("create table AccountDetails(AID Integer PRIMARY KEY autoincrement, PersonName String not null unique, Description String )");
      db.execSQL("create table AccountType(TypeId Integer PRIMARY KEY autoincrement, TypeName String unique)");
      db.execSQL("create table CountTable(ID Integer PRIMARY KEY autoincrement, BackupCount Integer, ExportCount Integer, RememberCount Integer)");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (1, 'Individual');");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (2, 'Income');");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (3, 'Expense');");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (4, 'General');");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (5, 'Friend');");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (6, 'Business');");
      db.execSQL("insert into AccountType(TypeId, TypeName) Values (7, 'Relative');");
      db.execSQL("insert into CountTable(ID, BackupCount, ExportCount, RememberCount) Values (1, 0, 0, 0);");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void reCreate(SQLiteDatabase db) {
    try {
      db.execSQL("create table Login(UserID  INTEGER PRIMARY KEY autoincrement, UserName String, Name String, Email String, Mobile String, Password String, Image BLOB, BackupDate Date)");
      db.execSQL("create table Account(AID Integer PRIMARY KEY autoincrement, UserID Integer, PersonName String not null unique, PersonEmail String, PersonMobile String, Remark String, Image BLOB, Type Integer, TypeName String)");
      db.execSQL("create table Transection(TID Integer PRIMARY KEY autoincrement, AID Integer, UserID Integer, Credit_Amount Integer, Debit_Amount Integer, dr_cr INTEGER, Remark String, Narration String, Image BLOB, EntryDate Date, Date String, LongDate unixtime)");
      db.execSQL("create table GeneralReminder(RID Integer PRIMARY KEY autoincrement, AID Integer, UserId Integer, EntryDate datetime, ReminderDate unixtime, ReminderDiscription String, BeforeDays String, Remark String, ReminderMode Integer)");
      db.execSQL("create table AccountDetails(AID Integer PRIMARY KEY autoincrement, PersonName String not null unique, Description String )");
      db.execSQL("create table AccountType(TypeId Integer PRIMARY KEY autoincrement, TypeName String unique)");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.e("DataBaseHandler", "onUpgrade called:- oldVersion: " + oldVersion + " newVersion: " + newVersion);
    try {
      db.execSQL("ALTER TABLE Login RENAME TO temp_Login");
      db.execSQL("ALTER TABLE Transection RENAME TO temp_Transection");
      db.execSQL("ALTER TABLE Account RENAME TO temp_Account");
      db.execSQL("ALTER TABLE GeneralReminder RENAME TO temp_GeneralReminder");
      db.execSQL("ALTER TABLE AccountType RENAME TO temp_AccountType");
      db.execSQL("ALTER TABLE AccountDetails RENAME TO temp_AccountDetails");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      db.execSQL("DROP TABLE IF EXISTS Login");
      db.execSQL("DROP TABLE IF EXISTS Transection");
      db.execSQL("DROP TABLE IF EXISTS Account");
      db.execSQL("DROP TABLE IF EXISTS GeneralReminder");
      db.execSQL("DROP TABLE IF EXISTS AccountType");
      db.execSQL("DROP TABLE IF EXISTS AccountDetails");
    } catch (Exception e2) {
      e2.printStackTrace();
    }
    reCreate(db);
    try {
      db.execSQL("insert into Login (UserID, UserName, Name, Email, Mobile, Password, Image) select UserID, UserName, Name, Email, Mobile, Password, Image from temp_Login");
    } catch (Exception e22) {
      e22.printStackTrace();
    }
    try {
      db.execSQL("insert into Transection (UserID, AID, TID, Credit_Amount, Debit_Amount, dr_cr, Remark, Narration, Image, Date) select UserID, AID, TID, Credit_Amount, Debit_Amount, dr_cr, Remark, Narration, Image, Date from temp_Transection");
    } catch (Exception e222) {
      e222.printStackTrace();
    }
    try {
      db.execSQL("insert into Account (UserID, AID, PersonName, PersonEmail, PersonMobile, Remark, Image, Type, TypeName) select UserID, AID, PersonName, PersonEmail, PersonMobile, Remark, Image, Type, TypeName from temp_Account");
    } catch (Exception e2222) {
      e2222.printStackTrace();
    }
    try {
      db.execSQL("insert into GeneralReminder (RID, AID, UserId, ReminderDate, ReminderDiscription, BeforeDays, Remark, ReminderMode) select RID, AID, UserId, ReminderDate, ReminderDiscription, BeforeDays, Remark, ReminderMode from temp_GeneralReminder");
    } catch (Exception e22222) {
      e22222.printStackTrace();
    }
    try {
      db.execSQL("insert into AccountType (TypeId, TypeName) select TypeId, TypeName from temp_AccountType");
    } catch (Exception e222222) {
      e222222.printStackTrace();
    }
    try {
      db.execSQL("insert into AccountDetails (AID, PersonName, Description) select AID, PersonName, Description from temp_AccountDetails");
    } catch (Exception e2222222) {
      e2222222.printStackTrace();
    }
    try {
      updateDb(db);
    } catch (Exception e22222222) {
      e22222222.printStackTrace();
    }
    try {
      db.execSQL("DROP TABLE IF EXISTS temp_Login");
      db.execSQL("DROP TABLE IF EXISTS temp_Account");
      db.execSQL("DROP TABLE IF EXISTS temp_Transection");
      db.execSQL("DROP TABLE IF EXISTS temp_GeneralReminder");
      db.execSQL("DROP TABLE IF EXISTS temp_AccountType");
      db.execSQL("DROP TABLE IF EXISTS temp_AccountDetails");
    } catch (Exception e222222222) {
      e222222222.printStackTrace();
    }
  }

  public void updateDb(SQLiteDatabase db) {
    String strDate = "";
    Reminder reminder;
    SimpleDateFormat format = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat oldFormat = new SimpleDateFormat("MM/dd/yyyy");
    ArrayList<Transaction> transactions = new ArrayList();
    Cursor c = db.rawQuery("SELECT TID, Date, LongDate FROM temp_Transection", null);
    if (c != null) {
      while (c.moveToNext()) {
        Transaction transaction = new Transaction();
        int tId = c.getInt(c.getColumnIndex("TID"));
        try {
          strDate = format.format(oldFormat.parse(c.getString(c.getColumnIndex("Date"))));
        } catch (Exception e) {
          try {
            strDate = format.format(new Date(c.getLong(c.getColumnIndex("LongDate"))));
          } catch (SQLiteException e2) {
            e2.printStackTrace();
          }
        }
        transaction.setTransactionId(tId);
        transaction.setDate(strDate);
        transactions.add(transaction);
      }
    }
    c.close();
    Iterator it = transactions.iterator();
    Transaction transaction = null;
    while (it.hasNext()) {
      transaction = (Transaction) it.next();
      try {
        ContentValues values = new ContentValues();
        values.put("EntryDate", transaction.getDate());
        db.update("Transection", values, "TID=" + transaction.getTransactionId(), null);
      } catch (Exception e3) {
        e3.printStackTrace();
      }
    }
    format = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
    ArrayList<Reminder> Reminders = new ArrayList();
    c = db.rawQuery("SELECT RID, ReminderDate FROM GeneralReminder", null);
    if (c != null) {
      while (c.moveToNext()) {
        reminder = new Reminder();
        int tId = c.getInt(c.getColumnIndex("RID"));
        try {
          strDate = format.format(new Date(c.getLong(c.getColumnIndex("ReminderDate"))));
        } catch (Exception e4) {
          strDate = format.format(new Date());
        }
        try {
          reminder.setId(tId);
          reminder.setDate(strDate);
          Reminders.add(reminder);
        } catch (SQLiteException e22) {
          e22.printStackTrace();
        }
      }
    }
    c.close();
    it = Reminders.iterator();
    while (it.hasNext()) {
      reminder = (Reminder) it.next();
      try {
        ContentValues values = new ContentValues();
        values.put("EntryDate", reminder.getDate());
        db.update("GeneralReminder", values, "RID=" + reminder.getId(), null);
      } catch (Exception e32) {
        e32.printStackTrace();
      }
    }
  }
}
