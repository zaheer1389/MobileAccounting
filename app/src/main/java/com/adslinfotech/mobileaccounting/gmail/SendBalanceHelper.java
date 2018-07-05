package com.adslinfotech.mobileaccounting.gmail;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Telephony.Sms;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import java.util.ArrayList;
import java.util.List;

import static com.adslinfotech.mobileaccounting.GsmRegisterActivity.mEmail;


public class SendBalanceHelper implements OnItemClickListener {
  private static String SMS_PACKAGE;
  private ShareIntentAdapter mAdapter;
  private String mBalance;
  private SimpleAccountingActivity mContext;
  private Dialog mDialog;
  private GridView mGrid;
  private LayoutInflater mInflater;
  private int mMaxColumns;
  private Account mSelectedAcc;
  private List<Transaction> mTransections;
  private List<ResolveInfo> plainTextActivities;
  private boolean status = false;

  public class ShareIntentAdapter extends BaseAdapter {
    public int getCount() {
      return SendBalanceHelper.this.plainTextActivities != null ? SendBalanceHelper.this.plainTextActivities.size() : 0;
    }

    public ResolveInfo getItem(int position) {
      return (ResolveInfo) SendBalanceHelper.this.plainTextActivities.get(position);
    }

    public long getItemId(int position) {
      return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      View view;
      if (convertView == null) {
        view = SendBalanceHelper.this.mInflater.inflate(R.layout.griditem_share_us, parent, false);
      } else {
        view = convertView;
      }
      bindView(view, (ResolveInfo) SendBalanceHelper.this.plainTextActivities.get(position));
      return view;
    }

    private final void bindView(View view, ResolveInfo info) {
      ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
      ((TextView) view.findViewById(android.R.id.text1)).setText(info.activityInfo.applicationInfo.loadLabel(SendBalanceHelper.this.mContext.getPackageManager()).toString());
      icon.setImageDrawable(info.activityInfo.applicationInfo.loadIcon(SendBalanceHelper.this.mContext.getPackageManager()));
    }
  }

  public SendBalanceHelper(SimpleAccountingActivity context, List<Transaction> mTransections2) {
    this.mContext = context;
    this.mTransections = mTransections2;
  }

  public SendBalanceHelper(SimpleAccountingActivity context, Account selectedAcc, String balance) {
    this.mContext = context;
    this.mSelectedAcc = selectedAcc;
    this.mBalance = balance;
  }

  @SuppressLint({"NewApi"})
  public void share(String title) {
    SMS_PACKAGE = Sms.getDefaultSmsPackage(this.mContext);
    this.mInflater = LayoutInflater.from(this.mContext);
    Intent sendIntent = new Intent("android.intent.action.SEND");
    sendIntent.setType("text/plain");
    List<ResolveInfo> htmlActivities = this.mContext.getPackageManager().queryIntentActivities(sendIntent, 0);
    if (htmlActivities.size() > 0) {
      Builder builder;
      this.plainTextActivities = new ArrayList();
      sendIntent.setType("text/plain");
      for (ResolveInfo resolveInfo : htmlActivities) {
        String packege = resolveInfo.activityInfo.packageName;
        if (packege.contains("android.gm") || packege.contains("android.mms") || packege.contains("android.sms") || packege.contains(SMS_PACKAGE)) {
          this.plainTextActivities.add(resolveInfo);
        }
      }
      this.mAdapter = new ShareIntentAdapter();
      View chooserView = this.mInflater.inflate(R.layout.dialog_share_us_chooser, null);
      this.mGrid = (GridView) chooserView.findViewById(R.id.resolver_grid);
      this.mGrid.setAdapter(this.mAdapter);
      this.mGrid.setOnItemClickListener(this);
      this.mMaxColumns = 2;
      this.mGrid.setNumColumns(Math.min(this.plainTextActivities.size(), this.mMaxColumns));
      if (VERSION.SDK_INT >= 11) {
        builder = new Builder(this.mContext, 5);
      } else {
        builder = new Builder(this.mContext);
      }
      builder.setTitle(title);
      builder.setView(chooserView);
      this.mDialog = builder.create();
      this.mDialog.show();
      return;
    }
    Toast.makeText(this.mContext, "No social apps installed to share ChurchLink!", Toast.LENGTH_LONG).show();
  }

  private void showPositiveAlert(String title, String alert) {
    final Dialog dialogPositiveAlert = new Dialog(this.mContext, R.style.WindowTitleBackground);
    dialogPositiveAlert.requestWindowFeature(1);
    dialogPositiveAlert.setContentView(R.layout.dialog_positive_alerts);
    dialogPositiveAlert.setCancelable(false);
    TextView mAlertText = (TextView) dialogPositiveAlert.findViewById(R.id.txt_positive_alert);
    ((TextView) dialogPositiveAlert.findViewById(R.id.title_alert)).setText(title);
    mAlertText.setText(alert);
    ((Button) dialogPositiveAlert.findViewById(R.id.btn_ok_alert_positive)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        dialogPositiveAlert.dismiss();
      }
    });
    dialogPositiveAlert.show();
  }

  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    String app = this.mAdapter.getItem(position).activityInfo.packageName;
    if (app.contains("gm")) {
      if (!isNetworkAvailable()) {
        showPositiveAlert("No Network", "Please check your network connection and try again.");
      } else if (this.status) {
        for (Transaction transaction : this.mTransections) {
          try {
            String mEmail = transaction.getNarration();
            Log.e("EmailID", mEmail);
            if (mEmail.equalsIgnoreCase("") || mEmail.equalsIgnoreCase(null)) {
              Toast.makeText(this.mContext, "Email Id is not Available for This Account", Toast.LENGTH_LONG).show();
            } else {
              String [] TO = new String[]{mEmail};
              String [] CC = new String[]{""};
              Intent emailIntent = new Intent("android.intent.action.SEND");
              emailIntent.setData(Uri.parse("mailto:"));
              emailIntent.setType("text/plain");
              emailIntent.putExtra("android.intent.extra.EMAIL", TO);
              emailIntent.putExtra("android.intent.extra.CC", CC);
              emailIntent.putExtra("android.intent.extra.SUBJECT", "Simple Accounting App");
              emailIntent.putExtra("android.intent.extra.TEXT", "Your Transaction with MR." + SessionManager.getName() + "=" + transaction.getBalance() + "\nVia:Simple Accounting Android App \ndownload this app \nhttp://bit.ly/1LGUjOE");
              try {
                this.mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                Log.i("SendBalanceHelper", "Finished sending email...");
              } catch (ActivityNotFoundException e) {
                Toast.makeText(this.mContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
              }
            }
          } catch (Exception e2) {
            e2.printStackTrace();
          }
        }
      } else {
        try {
          mEmail = this.mSelectedAcc.getEmail();
          if (mEmail.equalsIgnoreCase("") || mEmail.equalsIgnoreCase(null)) {
            Toast.makeText(this.mContext, "Email Id is not Available for This Account", Toast.LENGTH_LONG).show();
          } else {
            String [] TO = new String[]{mEmail};
            String [] CC = new String[]{""};
            Intent emailIntent = new Intent("android.intent.action.SEND");
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra("android.intent.extra.EMAIL", TO);
            emailIntent.putExtra("android.intent.extra.CC", CC);
            emailIntent.putExtra("android.intent.extra.SUBJECT", "Simple Accounting App");
            emailIntent.putExtra("android.intent.extra.TEXT", "Your Transaction with MR." + SessionManager.getName() + "=" + this.mSelectedAcc.getBalance() + "\nTotal Balance as on Date" + this.mBalance);
            try {
              this.mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
              Log.e("", "Finished sending email...");
            } catch (Exception e3) {
              Toast.makeText(this.mContext, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
          }
        } catch (Exception e22) {
          e22.printStackTrace();
        }
      }
    } else if (app.contains("mms") || app.contains("sms") || app.contains(SMS_PACKAGE)) {
      if (isSendSMSPermissionGranted()) {
        sendBySMS();
      } else {
        Toast.makeText(this.mContext, R.string.permission_sms, Toast.LENGTH_LONG).show();
      }
    }
    this.mDialog.dismiss();
  }

  public void sendBySMS() {
    String address;
    String msgContent;
    if (this.status) {
      for (Transaction transaction : this.mTransections) {
        try {
          address = transaction.getRemark();
          if (address.equalsIgnoreCase("") || address.equalsIgnoreCase(null)) {
            Toast.makeText(this.mContext, "Mobile Number not Availabe for This Account", Toast.LENGTH_SHORT).show();
          } else {
            SmsManager sms = SmsManager.getDefault();
            if (SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_LANGUAGE, "es").equals("hi")) {
              String balance;
              String[] bal = transaction.getBalance().split("/");
              String str1 = bal[0];
              if (bal[1].contains("Db")) {
                balance = str1 + "/- नामे";
              } else {
                balance = str1 + "/- जमा";
              }
              msgContent = SessionManager.getName() + " के साथ आपका लेनदेन = " + balance;
            } else {
              msgContent = "Your transaction with " + SessionManager.getName() + "= " + transaction.getBalance();
            }
            Log.e("msgContent", "" + msgContent);
            Log.e("address", "" + address);
            sms.sendMultipartTextMessage(address, null, sms.divideMessage(msgContent), null, null);
            Toast.makeText(this.mContext, "Message Send Successfully", Toast.LENGTH_SHORT).show();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return;
    }
    try {
      address = this.mSelectedAcc.getMobile();
      if (address.equalsIgnoreCase("") || address.equalsIgnoreCase(null)) {
        Toast.makeText(this.mContext, "Mobile Number not Availabe for This Account", Toast.LENGTH_SHORT).show();
        return;
      }
      SmsManager sms = SmsManager.getDefault();
      if (SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_LANGUAGE, "es").equals("hi")) {
        msgContent = SessionManager.getName() + " के साथ आपका लेनदेन = " + this.mSelectedAcc.getBalance() + "\nकुल बैलेंस आज की तारीख तक" + this.mBalance;
      } else {
        msgContent = "Your transaction with " + SessionManager.getName() + "= " + this.mSelectedAcc.getBalance() + "\nTotal Balance as on Date" + this.mBalance;
      }
      Log.e("msgContent", "" + msgContent);
      Log.e("address", "" + address);
      sms.sendMultipartTextMessage(address, null, sms.divideMessage(msgContent), null, null);
      Toast.makeText(this.mContext, "Message Send Successfully", Toast.LENGTH_SHORT).show();
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }

  private boolean isSendSMSPermissionGranted() {
    if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.SEND_SMS") == 0) {
      return true;
    }
    ActivityCompat.requestPermissions(this.mContext, new String[]{"android.permission.SEND_SMS"}, 283);
    return false;
  }

  public boolean isNetworkAvailable() {
    return ((ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
  }
}
