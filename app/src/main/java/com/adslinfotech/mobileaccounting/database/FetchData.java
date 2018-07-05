package com.adslinfotech.mobileaccounting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Balance;
import com.adslinfotech.mobileaccounting.dao.Category;
import com.adslinfotech.mobileaccounting.dao.NoteDao;
import com.adslinfotech.mobileaccounting.dao.Reminder;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.database.Query.ACCOUNT;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class FetchData {
  private static String TAG = "FetchData";
  private DecimalFormat df = new DecimalFormat("#.##");
  private DataBaseHandler handler = SimpleAccountingApp.getDBHandler();
  private NumberFormat newFormat = new DecimalFormat(this.newPattern);
  private String newPattern = this.pattern.replace("¤", "").trim();
  private NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
  private String pattern = ((DecimalFormat) this.nf).toPattern();

  public FetchData() {
    this.df.setMinimumFractionDigits(2);
    this.df.setMaximumFractionDigits(2);
  }

  public int insertLoginDetail(UserDao user) {
    try {
      Log.d("insertLoginDetail", "insert login detail");
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("UserName", user.getUserName());
      values.put("Name", user.getName());
      values.put("Email", user.getEmail());
      values.put("Mobile", user.getMobile());
      values.put("Password", user.getPassword());
      values.put("Image", user.getImage());
      return (int) db.insert("Login", null, values);
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }

  public boolean countTotalAccount() {
    Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT AID FROM Account", null);
    if (c.moveToFirst()) {
      int id = c.getCount();
      c.close();
      if (id > 20) {
        return false;
      }
      return true;
    }
    c.close();
    return true;
  }

  public int insertAccountDetail(Account account) {
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("UserID", Integer.valueOf(account.getUserId()));
      values.put(ACCOUNT.NAME, account.getName());
      values.put("PersonEmail", account.getEmail());
      values.put("PersonMobile", account.getMobile());
      values.put("Remark", account.getRemark());
      values.put("Image", account.getImage());
      values.put("Type", Integer.valueOf(account.getCategoryId()));
      values.put("TypeName", account.getCategory());
      return (int) db.insert("Account", null, values);
    } catch (SQLiteException e) {
      return -1;
    } catch (Exception e2) {
      e2.printStackTrace();
      Log.d("insert acc detail", "data not inserted");
      return 27;
    }
  }

  public void insertTransactionDetail(Transaction transaction) {
    SimpleDateFormat format1 = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("AID", Integer.valueOf(transaction.getAccountId()));
      values.put("UserID", Integer.valueOf(transaction.getUserId()));
      values.put("Credit_Amount", Double.valueOf(transaction.getCraditAmount()));
      values.put("Debit_Amount", Double.valueOf(transaction.getDebitAmount()));
      values.put("dr_cr", Integer.valueOf(transaction.getDr_cr()));
      values.put("Remark", transaction.getRemark());
      values.put("Narration", transaction.getNarration());
      values.put("EntryDate", transaction.getDate());
      try {
        Date date = format1.parse(transaction.getDate());
        values.put("Date", format2.format(date));
        values.put("LongDate", Long.valueOf(date.getTime()));
      } catch (Exception e) {
      }
      values.put("Image", transaction.getImage());
      db.insert("Transection", null, values);
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }

  public int insertReminderDetail(Reminder dao) {
    SimpleDateFormat format = new SimpleDateFormat(DateFormat.DB_DATE_TIME);
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("EntryDate", dao.getDate());
      values.put("ReminderDiscription", dao.getDescription());
      values.put("BeforeDays", Integer.valueOf(dao.getBeforeDay()));
      values.put("ReminderMode", Integer.valueOf(dao.getRmdType()));
      values.put("Remark", dao.getRemark());
      try {
        values.put("ReminderDate", Long.valueOf(format.parse(dao.getDate()).getTime()));
      } catch (Exception e) {
      }
      return (int) db.insert("GeneralReminder", null, values);
    } catch (Exception e2) {
      e2.printStackTrace();
      return 0;
    }
  }

  public void insertCategory(String category) {
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("TypeName", category);
      db.insert("AccountType", null, values);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void updateAutoBackupDate(long mDate) {
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("BackupDate", Long.valueOf(mDate));
      db.update("Login", values, "UserID = 1", null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean isDBExists() {
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT count(UserID), (SELECT count(AID) FROM Account), (SELECT count(TID) FROM Transection) FROM Login", null);
      if (c.moveToFirst()) {
        boolean s;
        if (c.getInt(0) > 0 || c.getInt(1) > 0 || c.getInt(2) > 0) {
          s = true;
        } else {
          s = false;
        }
        c.close();
        return s;
      }
      c.close();
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean isUserExists() {
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT count(Name) FROM Login", null);
      if (c.moveToFirst()) {
        boolean s;
        if (c.getInt(0) > 0) {
          s = true;
        } else {
          s = false;
        }
        c.close();
        return s;
      }
      c.close();
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void insertNote(NoteDao account) {
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(ACCOUNT.NAME, account.getHeading());
      values.put("Description", account.getDescr());
      db.insert("AccountDetails", null, values);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<Reminder> getReminder(String strDate) {
    String query;
    ArrayList<Reminder> results = new ArrayList();
    SQLiteDatabase db = this.handler.getReadableDatabase();
    if (strDate != null) {
      query = "select *, strftime('%m', EntryDate) as month, strftime('%d', EntryDate) - strftime('%d', '" + strDate + "') as diff_month, julianday(strftime('%Y-%m-%d', EntryDate)) - julianday('" + strDate + "') as diff from GeneralReminder where (ReminderMode = 0 AND diff between 0 and BeforeDays) or (ReminderMode = 1 AND diff_month between 0 and BeforeDays) or (ReminderMode = 2 AND month = strftime('%m', '" + strDate + "') AND diff_month between 0 and BeforeDays)";
    } else {
      query = "SELECT * FROM GeneralReminder";
    }
    Cursor c = db.rawQuery(query, null);
    Log.d("getReminder", "cursor size = " + c.getCount());
    while (c.moveToNext()) {
      Reminder reminder = new Reminder();
      int rId = c.getInt(c.getColumnIndex("RID"));
      int mode = c.getInt(c.getColumnIndex("ReminderMode"));
      String date = c.getString(c.getColumnIndex("EntryDate"));
      String desc = c.getString(c.getColumnIndex("ReminderDiscription"));
      String bdays = c.getString(c.getColumnIndex("BeforeDays"));
      reminder.setId(rId);
      reminder.setRmdType(mode);
      reminder.setDate(date);
      reminder.setDescription(desc);
      reminder.setRemark(c.getString(c.getColumnIndex("Remark")));
      try {
        reminder.setBeforeDay(Integer.parseInt(bdays));
      } catch (Exception e) {
        reminder.setBeforeDay(0);
        e.printStackTrace();
      }
      try {
        results.add(reminder);
      } catch (SQLiteException e2) {
        Log.e(getClass().getSimpleName(), "Could not create or Open the database");
      }
    }
    c.close();
    return results;
  }

  public ArrayList<Long> countAccountByBal() {
    ArrayList<Long> results = new ArrayList();
    long crAccounts = 0;
    long drAccounts = 0;
    long balAccounts = 0;
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT AID, (select (Sum(Transection.Credit_Amount) - Sum(Transection.Debit_Amount)) FROM Transection where Transection.AID = Account.AID) AS bal FROM Account", null);
      while (c.moveToNext()) {
        double bal;
        try {
          bal = Double.parseDouble(c.getString(c.getColumnIndex("bal")));
        } catch (Exception e) {
          bal = 0.0d;
        }
        if (bal > 0.0d) {
          crAccounts++;
        } else if (0.0d > bal) {
          drAccounts++;
        } else {
          balAccounts++;
        }
      }
      c.close();
    } catch (SQLiteException e2) {
      e2.printStackTrace();
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    results.add(Long.valueOf(crAccounts));
    results.add(Long.valueOf(drAccounts));
    results.add(Long.valueOf(balAccounts));
    results.add(Long.valueOf((crAccounts + drAccounts) + balAccounts));
    return results;
  }

  public ArrayList<String> getAllAccountsNames() {
    ArrayList<String> results = new ArrayList();
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT * FROM Account ORDER BY PersonName", null);
      while (c.moveToNext()) {
        results.add(c.getString(c.getColumnIndex(ACCOUNT.NAME)));
      }
      c.close();
    } catch (SQLiteException e) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return results;
  }

  public Account getAccount(String name) {
    Account account = new Account();
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT * FROM Account WHERE PersonName LIKE ?", new String[]{name});
      while (c.moveToNext()) {
        name = c.getString(c.getColumnIndex(ACCOUNT.NAME));
        int id = c.getInt(c.getColumnIndex("AID"));
        String pemail = c.getString(c.getColumnIndex("PersonEmail"));
        String pmobile = c.getString(c.getColumnIndex("PersonMobile"));
        String remark = c.getString(c.getColumnIndex("Remark"));
        byte[] image = c.getBlob(c.getColumnIndex("Image"));
        try {
          String type = c.getString(c.getColumnIndex("TypeName"));
          int typeId = c.getInt(c.getColumnIndex("Type"));
          if (type == null || type.trim().equalsIgnoreCase("")) {
            type = "Individual";
            typeId = 1;
          }
          account.setCategory(type);
          account.setCategoryId(typeId);
        } catch (Exception e) {
        }
        account.setName(name);
        account.setAccountId(id);
        account.setEmail(pemail);
        account.setMobile(pmobile);
        account.setRemark(remark);
        account.setImage(image);
      }
      c.close();
    } catch (SQLiteException e2) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return account;
  }

  public ArrayList<Account> getAllAccounts(boolean isImage, boolean isBalance) {
    String query;
    ArrayList<Account> results = new ArrayList();
    SQLiteDatabase db = this.handler.getReadableDatabase();
    if (isBalance) {
      query = "SELECT *, (select (Sum(Transection.Credit_Amount) - Sum(Transection.Debit_Amount)) FROM Transection where Transection.AID = Account.AID) AS bal FROM Account ORDER BY PersonName COLLATE NOCASE";
    } else {
      query = "SELECT * FROM Account ORDER BY PersonName COLLATE NOCASE";
    }
    Cursor c = db.rawQuery(query, null);
    int[] arr = null;
    while (c.moveToNext()) {
      if (c.isFirst()) {
        arr = new int[]{c.getColumnIndex("AID"), c.getColumnIndex(ACCOUNT.NAME), c.getColumnIndex("PersonEmail"), c.getColumnIndex("PersonMobile"), c.getColumnIndex("Remark"), c.getColumnIndex("TypeName"), c.getColumnIndex("Type"), c.getColumnIndex("Image"), c.getColumnIndex("bal")};
      }
      Account account = new Account();
      try {
        String type = c.getString(arr[5]);
        int typeId = c.getInt(arr[6]);
        if (type == null || type.trim().equalsIgnoreCase("")) {
          type = "Individual";
          typeId = 1;
        }
        account.setCategory(type);
        account.setCategoryId(typeId);
      } catch (Exception e) {
      }
      try {
        account.setAccountId(c.getInt(arr[0]));
        account.setName(c.getString(arr[1]));
        account.setEmail(c.getString(arr[2]));
        account.setMobile(c.getString(arr[3]));
        account.setRemark(c.getString(arr[4]));
        if (isImage) {
          account.setImage(c.getBlob(arr[7]));
        }
        if (isBalance) {
          double bal;
          try {
            bal = Double.parseDouble(c.getString(arr[8]));
          } catch (Exception e2) {
            bal = 0.0d;
          }
          if (bal > 0.0d) {
            account.setBalance(this.newFormat.format(bal) + "/-Cr");
          } else if (0.0d > bal) {
            account.setBalance(this.newFormat.format(-1.0d * bal) + "/-Db");
          } else {
            account.setBalance("0.00");
          }
        }
        results.add(account);
      } catch (SQLiteException e3) {
        e3.printStackTrace();
        Log.e(getClass().getSimpleName(), "Could not create or Open the database");
      }
    }
    c.close();
    return results;
  }

  public ArrayList<Category> getAllCategory() {
    ArrayList<Category> results = new ArrayList();
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT * FROM AccountType ORDER BY TypeName COLLATE NOCASE", null);
      if (c.moveToFirst()) {
        do {
          Category account = new Category();
          String pname = c.getString(c.getColumnIndex("TypeName"));
          int aId = c.getInt(c.getColumnIndex("TypeId"));
          account.setName(pname);
          account.setId(aId);
          results.add(account);
        } while (c.moveToNext());
      }
      c.close();
    } catch (SQLiteException e) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return results;
  }

  public ArrayList<NoteDao> getAllNotes() {
    ArrayList<NoteDao> results = new ArrayList();
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT * FROM AccountDetails", null);
      if (c.moveToFirst()) {
        do {
          NoteDao accountdetals = new NoteDao();
          int aId = c.getInt(c.getColumnIndex("AID"));
          String pname = c.getString(c.getColumnIndex(ACCOUNT.NAME));
          String desc = c.getString(c.getColumnIndex("Description"));
          accountdetals.setAccount_Id(aId);
          accountdetals.setHeading(pname);
          accountdetals.setDescr(desc);
          results.add(accountdetals);
        } while (c.moveToNext());
      }
      c.close();
    } catch (SQLiteException e) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return results;
  }

  /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
  public ArrayList<Transaction> getAllTransactions(Context context, String query, int aId, boolean checkOrder) {
    Cursor c;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat sdf1 = AppUtils.getDateFormat();
    ArrayList<Transaction> transactions = new ArrayList();
    double totalCredit = 0.0d;
    double totalDebit = 0.0d;
    NumberFormat format = AppUtils.getCurrencyFormatter();
    String mRsSymbol = SessionManager.getCurrency(context);
    SQLiteDatabase db = this.handler.getReadableDatabase();
    if (query != null) {
      c = db.rawQuery(query, new String[]{String.valueOf(aId)});
    } else if (aId == 0) {
      c = db.rawQuery("SELECT * FROM Transection order by AID, EntryDate", null);
    } else {
      c = db.rawQuery("SELECT * FROM Transection where AID = ? order by EntryDate", new String[]{String.valueOf(aId)});
    }
    while (c.moveToNext()) {
      Transaction transaction = new Transaction();
      transaction.setDr_cr(c.getInt(c.getColumnIndex("dr_cr")));
      String date = c.getString(c.getColumnIndex("EntryDate"));
      try {
        transaction.setDate(sdf1.format(simpleDateFormat.parse(date)));
      } catch (Exception e) {
        transaction.setDate(date);
      }
      transaction.setNarration(c.getString(c.getColumnIndex("Narration")));
      transaction.setRemark(c.getString(c.getColumnIndex("Remark")));
      try {
        transaction.setCraditAmount(Double.parseDouble(c.getString(c.getColumnIndex("Credit_Amount"))));
        try {
          transaction.setDebitAmount(Double.parseDouble(c.getString(c.getColumnIndex("Debit_Amount"))));
        } catch (Exception e2) {
          transaction.setDebitAmount(0.0d);
        }
        transaction.setAId(c.getInt(c.getColumnIndex("AID")));
        transaction.setTransactionId(c.getInt(c.getColumnIndex("TID")));
        transaction.setImage(c.getBlob(c.getColumnIndex("Image")));
        if (transaction.getDr_cr() == 1) {
          totalCredit += transaction.getCraditAmount();
        } else {
          totalDebit += transaction.getDebitAmount();
        }
        if (totalCredit > totalDebit) {
          transaction.setBalance(mRsSymbol + format.format(totalCredit - totalDebit) + "/-Cr");
        } else if (totalDebit > totalCredit) {
          transaction.setBalance(mRsSymbol + format.format(totalDebit - totalCredit) + "/-Db");
        } else {
          transaction.setBalance(mRsSymbol + "0.00/-");
        }
        transactions.add(transaction);
      } catch (Exception e3) {
        Log.e(getClass().getSimpleName(), "Could not create or Open the database");
      }
    }
    c.close();
    if (checkOrder && !SessionManager.getListOrder()) {
      Collections.reverse(transactions);
    }
    Balance balance = getBalanceFormatted(context, totalCredit, totalDebit);
    ArrayList list = new ArrayList();
    list.add(transactions);
    list.add(balance);
    return list;
  }

  public boolean checkSignInRequest(String uname, String pass) {
    try {
      String str = "Login";
      Cursor cursor = this.handler.getReadableDatabase().query(str, new String[]{"UserID"}, "username=?  COLLATE NOCASE AND password=?  COLLATE NOCASE", new String[]{uname, pass}, null, null, null);
      int count = cursor.getCount();
      cursor.close();
      if (count > 0) {
        return true;
      }
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public int changepassword(int id, String pass) {
    try {
      SQLiteDatabase db = this.handler.getReadableDatabase();
      ContentValues updateCountry = new ContentValues();
      updateCountry.put("Password", pass);
      int updateStatus = db.update("Login", updateCountry, "UserID=" + String.valueOf(id), null);
      Log.d("cursor", String.valueOf(updateStatus));
      if (updateStatus != 0) {
        return 1;
      }
    } catch (Exception e) {
    }
    return 0;
  }

  public void settleAccount(int id) {
    this.handler.getWritableDatabase().delete("Transection", "AID=" + id, null);
  }

  public ArrayList<Double> getTransactionTotal(int aId) {
    ArrayList<Double> amount = new ArrayList();
    double s1 = 0.0d;
    double s2 = 0.0d;
    SQLiteDatabase db = this.handler.getReadableDatabase();
    try {
      Cursor cursor;
      db.execSQL("DELETE FROM Transection WHERE AID NOT IN (SELECT AID FROM Account)");
      if (aId == 0) {
        cursor = db.rawQuery(" SELECT  sum(Transection.Debit_Amount) AS SumOfDRAmount, sum(Transection.Credit_Amount) AS SumOfCRAmount FROM Transection", null);
      } else {
        cursor = db.rawQuery(" SELECT  sum(Transection.Debit_Amount) AS SumOfDRAmount, sum(Transection.Credit_Amount) AS SumOfCRAmount FROM Transection WHERE (((Transection.AID)=?))", new String[]{String.valueOf(aId)});
      }
      Log.d(TAG, "getTransectionTotal cursor =" + cursor);
      if (cursor.moveToFirst()) {
        try {
          s1 = Double.parseDouble(cursor.getString(1));
        } catch (Exception e) {
        }
        try {
          s2 = Double.parseDouble(cursor.getString(0));
        } catch (Exception e2) {
        }
      }
      cursor.close();
    } catch (Exception e3) {
      e3.printStackTrace();
    }
    amount.add(Double.valueOf(s1));
    amount.add(Double.valueOf(s2));
    return amount;
  }

  public ArrayList<ArrayList<Transaction>> getAccountGlance(boolean isSortByName) {
    String query = "SELECT Account.AID, Account.PersonName, (Sum(Transection.Credit_Amount) - Sum(Transection.Debit_Amount)) AS balance\nFROM Account INNER JOIN [Transection] ON Account.AID = Transection.AID\nGROUP BY Account.AID ORDER BY Account.PersonName COLLATE NOCASE";
    ArrayList<Transaction> listCr = new ArrayList();
    ArrayList<Transaction> listDr = new ArrayList();
    double totalCr = 0.0d;
    double totalDr = 0.0d;
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery(query, null);
      if (c.moveToFirst()) {
        do {
          Transaction account = new Transaction();
          String pname = c.getString(c.getColumnIndex(ACCOUNT.NAME));
          String strBalance = c.getString(c.getColumnIndex("balance"));
          account.setAccName(pname);
          double bal = Double.parseDouble(strBalance);
          if (bal > 0.0d) {
            account.setCraditAmount(bal);
            totalCr += bal;
            listCr.add(account);
          } else if (0.0d > bal) {
            bal *= -1.0d;
            totalDr += bal;
            account.setDebitAmount(bal);
            listDr.add(account);
          }
        } while (c.moveToNext());
      }
      c.close();
    } catch (SQLiteException e) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    ArrayList<Transaction> total = new ArrayList();
    Transaction dao = new Transaction();
    dao.setCraditAmount(totalCr);
    dao.setDebitAmount(totalDr);
    total.add(dao);
    if (!isSortByName) {
      Collections.sort(listCr);
      Collections.sort(listDr);
    }
    ArrayList<ArrayList<Transaction>> result = new ArrayList();
    result.add(listCr);
    result.add(listDr);
    result.add(total);
    return result;
  }

  public Transaction getTodayTransactions(String fromDate, String toDate) {
    String query = "SELECT sum(Transection.Debit_Amount) AS SumOfDRAmount, sum(Transection.Credit_Amount) AS SumOfCRAmount, Transection.EntryDate FROM Transection\nWHERE (AID != 0) AND Transection.EntryDate BETWEEN Date('" + fromDate + "') AND Date('" + toDate + "') GROUP BY Transection.EntryDate";
    Transaction data = new Transaction();
    try {
      Cursor cursor = this.handler.getReadableDatabase().rawQuery(query, null);
      Log.d("getDayTransaction " + cursor.getCount(), "" + query);
      if (cursor.moveToFirst()) {
        int clmDate = cursor.getColumnIndex("EntryDate");
        int damt = cursor.getColumnIndex("SumOfDRAmount");
        int camt = cursor.getColumnIndex("SumOfCRAmount");
        String debit = cursor.getString(damt);
        try {
          data.setCraditAmount(Double.parseDouble(cursor.getString(camt)));
        } catch (Exception e) {
          data.setCraditAmount(0.0d);
        }
        try {
          data.setDebitAmount(Double.parseDouble(debit));
        } catch (Exception e2) {
          data.setDebitAmount(0.0d);
        }
        if (data.getCraditAmount() > data.getDebitAmount()) {
          data.setBalance((data.getCraditAmount() - data.getDebitAmount()) + "/-Cr");
        } else if (data.getDebitAmount() > data.getCraditAmount()) {
          data.setBalance((data.getDebitAmount() - data.getCraditAmount()) + "/-Dr");
        } else {
          data.setBalance("0.00");
        }
        data.setDate(cursor.getString(clmDate));
        cursor.close();
      }
    } catch (Exception e3) {
      e3.printStackTrace();
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return data;
  }

  public ArrayList<Account> getAccountById(String type) {
    ArrayList<Account> accounts = new ArrayList();
    try {
      Cursor c;
      SQLiteDatabase db = this.handler.getReadableDatabase();
      if (type == null) {
        c = db.rawQuery("SELECT PersonName, TypeName, Type, AID, PersonEmail, PersonMobile FROM Account", null);
      } else {
        c = db.rawQuery("SELECT PersonName, TypeName, Type, AID, PersonEmail, PersonMobile FROM Account WHERE TypeName=? ", new String[]{type});
      }
      if (c != null) {
        c.moveToFirst();
        do {
          Account data = new Account();
          data.setName(c.getString(0));
          String t = c.getString(1);
          if (t == null || t.trim().equalsIgnoreCase("")) {
            t = "Individual";
          }
          data.setCategory(t);
          data.setCategoryId(c.getInt(2));
          data.setAccountId(c.getInt(3));
          data.setEmail(c.getString(4));
          data.setMobile(c.getString(5));
          accounts.add(data);
        } while (c.moveToNext());
        c.close();
      }
    } catch (Exception e) {
    }
    return accounts;
  }

  public String getTotalBalance(int aId) {
    String balance = "0.00";
    Cursor cur = this.handler.getReadableDatabase().rawQuery("SELECT  AID, sum(Transection.Debit_Amount) AS SumOfDRAmount, sum(Transection.Credit_Amount) AS SumOfCRAmount FROM Transection  WHERE (((Transection.AID)=?)) ", new String[]{String.valueOf(aId)});
    if (cur != null) {
      cur.moveToFirst();
      int sno = cur.getColumnIndex("AID");
      int damt = cur.getColumnIndex("SumOfDRAmount");
      int camt = cur.getColumnIndex("SumOfCRAmount");
      if (cur.moveToFirst()) {
        do {
          int snos = cur.getInt(sno);
          Log.d("getTotalBalance = ", "id = " + snos);
          if (snos != 0) {
            double dr;
            double cr;
            String dates = cur.getString(damt);
            String camts = cur.getString(camt);
            try {
              dr = Double.parseDouble(dates);
            } catch (Exception e) {
              e.printStackTrace();
              dr = 0.0d;
            }
            try {
              cr = Double.parseDouble(camts);
            } catch (Exception e2) {
              e2.printStackTrace();
              cr = 0.0d;
            }
            if (cr > dr) {
              try {
                balance = "" + this.newFormat.format(Double.valueOf(cr - dr).doubleValue()) + "/-Cr";
              } catch (Exception e22) {
                e22.printStackTrace();
                System.out.println("Data not insert");
              }
            } else if (dr > cr) {
              balance = "" + this.newFormat.format(Double.valueOf(dr - cr).doubleValue()) + "/-Db";
            } else {
              balance = "0.00";
            }
          }
        } while (cur.moveToNext());
      }
      cur.close();
    }
    return balance;
  }

  private Balance getBalanceFormatted(Context context, double cr, double dr) {
    Balance dao = new Balance();
    NumberFormat format = AppUtils.getCurrencyFormatter();
    String mRsSymbol = SessionManager.getCurrency(context);
    if (cr > dr) {
      dao.setBalance(mRsSymbol + format.format(cr - dr) + "/-" + context.getResources().getString(R.string.txt_Credit));
    } else if (dr > cr) {
      dao.setBalance(mRsSymbol + format.format(dr - cr) + "/-" + context.getResources().getString(R.string.txt_Debit));
    } else {
      dao.setBalance(mRsSymbol + "0.00/-");
    }
    dao.setCredit(mRsSymbol + format.format(cr) + "/-");
    dao.setDebit(mRsSymbol + format.format(dr) + "/-");
    return dao;
  }

  public Balance getAccountBalance(int aId, Context context, String tillDate) {
    Balance dao = new Balance();
    try {
      String select;
      SQLiteDatabase db = this.handler.getReadableDatabase();
      if (tillDate != null) {
        select = "SELECT  AID, sum(Debit_Amount) AS SumOfDRAmount, sum(Credit_Amount) AS SumOfCRAmount FROM Transection  WHERE AID=? AND EntryDate < '" + tillDate + "'";
      } else {
        select = "SELECT  AID, sum(Debit_Amount) AS SumOfDRAmount, sum(Credit_Amount) AS SumOfCRAmount FROM Transection  WHERE AID=? ";
      }
      Cursor cur = db.rawQuery(select, new String[]{String.valueOf(aId)});
      int damt = cur.getColumnIndex("SumOfDRAmount");
      int camt = cur.getColumnIndex("SumOfCRAmount");
      if (cur.moveToFirst()) {
        double dr;
        double cr;
        String dates = cur.getString(damt);
        String camts = cur.getString(camt);
        try {
          dr = Double.parseDouble(dates);
        } catch (Exception e) {
          e.printStackTrace();
          dr = 0.0d;
        }
        try {
          cr = Double.parseDouble(camts);
        } catch (Exception e2) {
          e2.printStackTrace();
          cr = 0.0d;
        }
        if (context != null) {
          dao = getBalanceFormatted(context, cr, dr);
        } else {
          dao.setCredit("" + cr);
          dao.setDebit("" + dr);
        }
      }
      cur.close();
    } catch (Exception e22) {
      e22.printStackTrace();
      System.out.println("Data not insert");
    }
    return dao;
  }

  public ArrayList<Transaction> getCategoryBalance(String category) {
    String query;
    if (category == null) {
      query = "SELECT Account.AID, Account.PersonName, Account.PersonEmail, Account.PersonMobile, Sum(Transection.Credit_Amount) AS SumOfCRAmount, Sum(Transection.Debit_Amount) AS SumOfDRAmount, Account.TypeName\nFROM Account INNER JOIN [Transection] ON Account.AID = Transection.AID\nGROUP BY Account.AID, Account.TypeName\nORDER BY PersonName COLLATE NOCASE;";
    } else {
      query = "SELECT Account.AID, Account.PersonName, Account.PersonEmail, Account.PersonMobile, Sum(Transection.Credit_Amount) AS SumOfCRAmount, Sum(Transection.Debit_Amount) AS SumOfDRAmount, Account.TypeName\nFROM Account INNER JOIN [Transection] ON Account.AID = Transection.AID\nGROUP BY Account.AID, Account.TypeName\nHAVING (((Account.TypeName)=\"" + category + "\")) ORDER BY PersonName COLLATE NOCASE;";
    }
    ArrayList<Transaction> results = new ArrayList();
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery(query, null);
      if (c.moveToFirst()) {
        do {
          Transaction account = new Transaction();
          String pname = c.getString(c.getColumnIndex(ACCOUNT.NAME));
          int aId = c.getInt(c.getColumnIndex("AID"));
          String credit = c.getString(c.getColumnIndex("SumOfCRAmount"));
          String debit = c.getString(c.getColumnIndex("SumOfDRAmount"));
          String mobile = c.getString(c.getColumnIndex("PersonMobile"));
          String email = c.getString(c.getColumnIndex("PersonEmail"));
          try {
            String type = c.getString(c.getColumnIndex("TypeName"));
            if (type == null || type.trim().equalsIgnoreCase("")) {
              type = "Individual";
            }
            account.setType(type);
          } catch (Exception e) {
          }
          account.setAccName(pname);
          account.setRemark(mobile);
          account.setNarration(email);
          account.setAId(aId);
          account.setCraditAmount(Double.parseDouble(credit));
          account.setDebitAmount(Double.parseDouble(debit));
          results.add(account);
        } while (c.moveToNext());
      }
      c.close();
    } catch (SQLiteException e2) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return results;
  }

  public int updateNote(NoteDao account) {
    int updateStatus = 0;
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(ACCOUNT.NAME, account.getHeading());
      values.put("Description", account.getDescr());
      updateStatus = db.update("AccountDetails", values, "AID=" + account.getAccount_Id(), null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return updateStatus;
  }

  public int updateTransaction(Transaction transaction) {
    SimpleDateFormat format1 = new SimpleDateFormat(DateFormat.DB_DATE);
    SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");
    int updateStatus = 0;
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("Credit_Amount", Double.valueOf(transaction.getCraditAmount()));
      values.put("Debit_Amount", Double.valueOf(transaction.getDebitAmount()));
      values.put("dr_cr", Integer.valueOf(transaction.getDr_cr()));
      values.put("Remark", transaction.getRemark());
      values.put("Narration", transaction.getNarration());
      values.put("EntryDate", transaction.getDate());
      try {
        Date date = format1.parse(transaction.getDate());
        values.put("Date", format2.format(date));
        values.put("LongDate", Long.valueOf(date.getTime()));
      } catch (Exception e) {
      }
      values.put("AID", Integer.valueOf(transaction.getAccountId()));
      values.put("Image", transaction.getImage());
      updateStatus = db.update("Transection", values, "TID=" + transaction.getTransactionId(), null);
    } catch (Exception e2) {
      e2.printStackTrace();
    }
    return updateStatus;
  }

  public int updateAccount(Account account) {
    int updateStatus = 0;
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(ACCOUNT.NAME, account.getName());
      values.put("PersonEmail", account.getEmail());
      values.put("PersonMobile", account.getMobile());
      values.put("Remark", account.getRemark());
      values.put("Image", account.getImage());
      values.put("TypeName", account.getCategory());
      updateStatus = db.update("Account", values, "AId=" + account.getAccountId(), null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("update acc detail", "data not undated");
    }
    return updateStatus;
  }

  public int deleteReminder(int rId) {
    int delateStatus = 0;
    try {
      delateStatus = this.handler.getWritableDatabase().delete("GeneralReminder", "RID=" + rId, null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("delete acc detail", "data not deleted");
    }
    return delateStatus;
  }

  public int deleteAccount(int aId) {
    int delateStatus = 0;
    try {
      delateStatus = this.handler.getWritableDatabase().delete("Account", "AID=" + aId, null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("delete acc detail", "data not deleted");
    }
    return delateStatus;
  }

  public int deleteNote(int aId) {
    int delateStatus = 0;
    try {
      delateStatus = this.handler.getWritableDatabase().delete("AccountDetails", "AID=" + aId, null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("delete acc detail", "data not deleted");
    }
    return delateStatus;
  }

  public int deleteCategory(Category category) {
    int delateStatus = 0;
    try {
      if (getAccountById(category.getName()).size() == 0) {
        delateStatus = this.handler.getWritableDatabase().delete("AccountType", "TypeId=" + category.getId(), null);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return delateStatus;
  }

  public int deleteTransaction(int tId) {
    int delateStatus = 0;
    try {
      delateStatus = this.handler.getWritableDatabase().delete("Transection", "TID=" + tId, null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("delete tran detail", "data not deleted");
    }
    return delateStatus;
  }

  public int updateReminder(Reminder dao) {
    int updateStatus = 0;
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("ReminderMode", Integer.valueOf(dao.getRmdType()));
      values.put("EntryDate", dao.getDate());
      values.put("ReminderDiscription", dao.getDescription());
      values.put("BeforeDays", "" + dao.getBeforeDay());
      values.put("Remark", dao.getRemark());
      updateStatus = db.update("GeneralReminder", values, "RID=" + dao.getId(), null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("update acc detail", "data not undated");
    }
    return updateStatus;
  }

  public int updateProfile(UserDao user) {
    int updateStatus = 0;
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("UserName", user.getUserName());
      values.put("Password", user.getPassword());
      values.put("Name", user.getName());
      values.put("Email", user.getEmail());
      values.put("Mobile", user.getMobile());
      values.put("Image", user.getImage());
      updateStatus = db.update("Login", values, "UserID=" + user.getUserID(), null);
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("update acc detail", "data not undated");
    }
    return updateStatus;
  }

  public int updateCategory(Category category) {
    int updateStatus = 0;
    try {
      SQLiteDatabase db = this.handler.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put("TypeName", category.getName());
      updateStatus = db.update("AccountType", values, "TypeId=" + category.getId(), null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return updateStatus;
  }

  public UserDao getProfileDetail() {
    UserDao user = new UserDao();
    try {
      Cursor c = this.handler.getReadableDatabase().rawQuery("SELECT * FROM Login", null);
      if (c.moveToFirst()) {
        int userId = c.getInt(c.getColumnIndex("UserID"));
        String userName = c.getString(c.getColumnIndex("UserName"));
        String name = c.getString(c.getColumnIndex("Name"));
        String mobile = c.getString(c.getColumnIndex("Mobile"));
        String email = c.getString(c.getColumnIndex("Email"));
        String pass = c.getString(c.getColumnIndex("Password"));
        byte[] image = c.getBlob(c.getColumnIndex("Image"));
        user.setUserID(userId);
        user.setUserName(userName);
        user.setName(name);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(pass);
        user.setImage(image);
      }
      c.close();
    } catch (SQLiteException e) {
      Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }
    return user;
  }
}
