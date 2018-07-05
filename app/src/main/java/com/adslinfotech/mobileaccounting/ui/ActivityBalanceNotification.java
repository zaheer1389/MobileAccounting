package com.adslinfotech.mobileaccounting.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.database.FetchData;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityBalanceNotification extends Activity {
  private Button btn;
  protected DecimalFormat df = new DecimalFormat("#.##", this.otherSymbols);
  private ImageView image;
  public NumberFormat newFormat = new DecimalFormat(this.newPattern);
  String newPattern = this.pattern.replace("¤", "").trim();
  NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
  DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(new Locale("en"));
  String pattern = ((DecimalFormat) this.nf).toPattern();
  private TextView txtMessage;
  private TextView txtTitle;

  protected void onCreate(Bundle savedInstanceState) {
    String balance;
    super.onCreate(savedInstanceState);
    getWindow().requestFeature(1);
    setContentView(R.layout.dialog_positive_alerts);
    String mRsSymbol = SessionManager.getCurrency(getApplicationContext());
    ArrayList<Double> transection = new FetchData().getTransactionTotal(0);
    double credit = ((Double) transection.get(0)).doubleValue();
    double debit = ((Double) transection.get(1)).doubleValue();
    if (debit > credit) {
      balance = mRsSymbol + this.newFormat.format(Double.valueOf(this.df.format(debit - credit)).doubleValue()) + "/-Db";
    } else if (debit == credit) {
      balance = mRsSymbol + "0.00";
    } else {
      balance = mRsSymbol + this.newFormat.format(Double.valueOf(this.df.format(credit - debit)).doubleValue()) + "/-Cr";
    }
    String bal = getIntent().getExtras().getString("BALANCE") + "\nOverAll Balance:" + balance;
    this.txtTitle = (TextView) findViewById(R.id.title_alert);
    this.txtMessage = (TextView) findViewById(R.id.txt_positive_alert);
    this.image = (ImageView) findViewById(R.id.alert_icon);
    this.image.setImageResource(R.drawable.accounting_icon);
    this.txtTitle.setText(getResources().getString(R.string.app_name));
    this.txtMessage.setText(bal);
    this.txtMessage.setTextSize(15.0f);
    this.btn = (Button) findViewById(R.id.btn_ok_alert_positive);
    this.btn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent(ActivityBalanceNotification.this.getApplicationContext(), ActivityLogin.class);
        intent.addFlags(4194304);
        intent.addFlags(131072);
        intent.addFlags(16);
        ActivityBalanceNotification.this.startActivity(intent);
        ActivityBalanceNotification.this.finish();
      }
    });
  }
}
