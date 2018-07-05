package com.adslinfotech.mobileaccounting.activities.invoice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes.Builder;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.Transaction;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.gmail.SendBalanceHelper;
import com.adslinfotech.mobileaccounting.ui.ActivityPreferences;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.DateFormat;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class InvoiceActivity extends SimpleAccountingActivity {
  private static final String EXTRA_ACCOUNT = "EXTRA_ACCOUNT";
  private static final String EXTRA_TRANS = "EXTRA_TRANS";
  private static final int REQUEST_READ_PHONE_STORAGE = 21;
  private boolean isShareBalanceCalled;
  private Account mAccount;
  private String mAmount;
  private SendBalanceHelper mBalanceHelper;
  private String mPath;
  private Transaction mTransaction;
  private WebView webView;

  public static void newInstance(Activity context, Transaction transaction, Account account) {
    Intent intent = new Intent(context, InvoiceActivity.class);
    intent.putExtra(EXTRA_TRANS, transaction);
    intent.putExtra(EXTRA_ACCOUNT, account);
    context.startActivityForResult(intent, AppConstants.ACTIVITY_FINISH);
  }

  protected void onCreate(Bundle savedInstanceState) {
    String type;
    String date;
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_webview);
    this.webView = (WebView) findViewById(R.id.webview);
    UserDao user = new FetchData().getProfileDetail();
    String header = "<b>" + user.getName() + "<br>\n" + user.getEmail() + "<br>\n" + user.getMobile() + "</b>";
    if (!TextUtils.isEmpty(user.getCity())) {
      header = header + "<br>\n" + user.getCity();
    }
    this.mAccount = (Account) getIntent().getSerializableExtra(EXTRA_ACCOUNT);
    this.mTransaction = (Transaction) getIntent().getSerializableExtra(EXTRA_TRANS);
    if (this.mTransaction.getDr_cr() == 1) {
      type = "<b>CREDIT";
      this.mAmount = this.mTransaction.getCraditAmount() + "/-Cr";
    } else {
      type = "<b>DEBIT";
      this.mAmount = this.mTransaction.getDebitAmount() + "/-Db";
    }
    type = type + "</b>";
    try {
      date = AppUtils.getDateFormat().format(new SimpleDateFormat(DateFormat.DB_DATE).parse(this.mTransaction.getDate()));
    } catch (Exception e) {
      date = this.mTransaction.getDate();
    }
    String str = "<!doctype html>\n<html>\n<head>\n    <meta charset=\"utf-8\">\n    <title>A simple, clean, and responsive HTML invoice template</title>\n    \n    <style>\n    .invoice-box{\n        max-width:800px;\n        margin:auto;\n        padding:30px;\n        border:1px solid #eee;\n        box-shadow:0 0 10px rgba(0, 0, 0, .15);\n        font-size:16px;\n        line-height:24px;\n        font-family:'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;    }\n    \n    .invoice-box table{\n        width:100%;\n        line-height:inherit;\n        text-align:left;\n    }\n    \n    .invoice-box table td{\n        padding:5px;\n        vertical-align:top;\n    }\n    \n    .invoice-box table tr td:nth-child(2){\n        text-align:right;\n    }\n    \n    .invoice-box table tr.top table td{\n        padding-bottom:20px;\n    }\n    \n    .invoice-box table tr.top table td.title{\n        font-size:45px;\n        line-height:45px;\n    }\n    \n    .invoice-box table tr.information table td{\n        padding-bottom:40px;\n    }\n    \n    .invoice-box table tr.heading td{\n        background:#eee;\n        border-bottom:1px solid #ddd;\n        font-weight:bold;\n    }\n    \n    .invoice-box table tr.details td{\n        padding-bottom:20px;\n    }\n    \n    .invoice-box table tr.item td{\n        border-bottom:1px solid #eee;\n    }\n    \n    .invoice-box table tr.item.last td{\n        border-bottom:none;\n    }\n    \n    .invoice-box table tr.total td:nth-child(2){\n        border-top:2px solid #eee;\n        font-weight:bold;\n    }\n    \n    @media only screen and (max-width: 600px) {\n        .invoice-box table tr.top table td{\n            width:100%;\n            display:block;\n            text-align:center;\n        }\n        \n        .invoice-box table tr.information table td{\n            width:100%;\n            display:block;\n            text-align:center;\n        }\n    }\n    </style>\n</head>\n\n<body>\n    <div class=\"invoice-box\">\n        <table cellpadding=\"0\" cellspacing=\"0\">\n            <tr class=\"total\">\n                <td colspan=\"2\">\n                    <table>\n                        <tr>\n                            <td style='text-align:center;vertical-align:middle'>\n                                " + header + "                            </td>\n                            \n                        </tr>\n            <tr class=\"total\">                            <td align=\"center\"valign=\"center\">                                " + type + "                            </td>            </tr>                    </table>\n                </td>\n            </tr>            <tr >\n                <td>\n                    " + getString(R.string.txt_Date) + "\n                </td>\n                \n                <td>\n                    <b>" + date + "</b>\n                </td>\n            </tr>\n            <tr >\n                <td>\n                    " + getString(R.string.txt_Amount) + "\n                </td>\n                \n                <td>\n                    <b>" + this.mAmount + "</b>\n                </td>\n            </tr>\n            <tr >\n                <td>\n                    " + getString(R.string.txt_Being) + "\n                </td>\n                \n                <td>\n                    <b>" + this.mAccount.getName() + "</b>\n                </td>\n            </tr>\n            <tr >\n                <td>\n                    " + getString(R.string.txt_Narration) + "\n                </td>\n                \n                <td>\n                    <b>" + this.mTransaction.getNarration() + "</b>\n                </td>\n            </tr>\n                    <table>\n            <tr class=\"total\">\n                <td></td>\n\n                \n\n                <td>\n\n                   " + getString(R.string.txt_AuthorizedBy) + "\n                </td>\n            </tr>\n                    </table>\n        </table>\n    </div>\n</body>\n</html>";
    this.webView.getSettings().setJavaScriptEnabled(true);
    this.webView.loadDataWithBaseURL(null, str, "text/HTML", "UTF-8", null);
    saveHtmlFile(str);
  }

  private void saveHtmlFile(String html) {
    String PATH = Environment.getExternalStorageDirectory() + AppConstants.FOLDER + "/pdf";
    File directory = new File(PATH);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    this.mPath = PATH + File.separator + "invoice.html";
    File file = new File(this.mPath);
    try {
      file.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      out.write(html.getBytes());
      out.close();
      Log.e("saveHtmlFile", "File Save : " + file.getPath());
    } catch (FileNotFoundException e2) {
      e2.printStackTrace();
    } catch (IOException e3) {
      e3.printStackTrace();
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_invoice, menu);
    return super.onCreateOptionsMenu(menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_capture:
        screenShot();
        return true;
      case R.id.menu_print:
        createWebPrintJob();
        return true;
      case R.id.menu_share:
        share();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void share() {
    String balance = new FetchData().getTotalBalance(this.mAccount.getAccountId());
    if (SimpleAccountingApp.getPreference().getString(ActivityPreferences.PREF_LANGUAGE, "es").equals("hi")) {
      if (this.mTransaction.getDr_cr() == 1) {
        this.mAccount.setBalance(this.mAmount + " जमा");
      } else {
        this.mAccount.setBalance(this.mAmount + " नामे");
      }
      String[] bal = balance.split("/");
      String str1 = bal[0];
      balance = bal[1].contains("Db") ? str1 + "/- नामे" : str1 + "/- जमा";
    } else if (this.mTransaction.getDr_cr() == 1) {
      this.mAccount.setBalance(this.mAmount + " Cr");
    } else {
      this.mAccount.setBalance(this.mAmount + " Db");
    }
    sendMail(balance);
  }

  private void sendMail(String balance) {
    this.isShareBalanceCalled = true;
    this.mBalanceHelper = new SendBalanceHelper(this, this.mAccount, balance);
    this.mBalanceHelper.share("Sent Receipt to " + this.mAccount.getName());
  }

  @TargetApi(19)
  private void createWebPrintJob() {
    PrintJob printJob = ((PrintManager) getSystemService(Context.PRINT_SERVICE)).print(getString(R.string.app_name) + " Document", this.webView.createPrintDocumentAdapter(), new Builder().build());
  }

  public void screenShot() {
    createImage(getBitmapOFRootView(this.webView));
  }

  public Bitmap getBitmapOFRootView(View v) {
    View rootview = v.getRootView();
    rootview.setDrawingCacheEnabled(true);
    return rootview.getDrawingCache();
  }

  public void createImage(Bitmap bmp) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bmp.compress(CompressFormat.JPEG, 40, bytes);
    File file = new File(Environment.getExternalStorageDirectory() + "/pnrscreen.jpg");
    try {
      file.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      file.createNewFile();
      FileOutputStream outputStream = new FileOutputStream(file);
      outputStream.write(bytes.toByteArray());
      outputStream.close();
      shareScreen(Uri.fromFile(file));
    } catch (Exception e2) {
      e2.printStackTrace();
    }
  }

  private void shareScreen(Uri screenshotUri) {
    Intent sharingIntent = new Intent("android.intent.action.SEND");
    try {
      getContentResolver().openInputStream(screenshotUri);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    sharingIntent.setType("image/jpeg");
    sharingIntent.putExtra("android.intent.extra.STREAM", screenshotUri);
    startActivity(Intent.createChooser(sharingIntent, "Share image using"));
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (this.isShareBalanceCalled && requestCode == 21 && grantResults[0] == 0) {
      this.mBalanceHelper.sendBySMS();
    }
    this.isShareBalanceCalled = false;
  }
}
