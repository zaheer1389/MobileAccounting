package com.adslinfotech.mobileaccounting.desktop;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.utils.AppConstants;

public class WebServerActivity extends Activity {
  private MyHttpServer server;
  private int type;

  class C03961 implements OnClickListener {
    C03961() {
    }

    public void onClick(View v) {
      try {
        WebServerActivity.this.server.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
      WebServerActivity.this.finish();
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webserver);
    this.type = getIntent().getIntExtra("TYPE", 0);
    if (this.type == 1) {
      this.server = new MyHttpServer((Context) this, (Account) getIntent().getExtras().getSerializable(AppConstants.ACCOUNT_DAO));
      this.server.start();
    } else if (this.type == 2) {
      this.server = new MyHttpServer((Context) this, getIntent().getStringExtra(AppConstants.ACCOUNT_SELECTED));
      this.server.start();
    } else if (this.type == 3 || this.type == 4) {
      this.server = new MyHttpServer((Context) this, this.type);
      this.server.start();
    }
    String strIPAddress = this.server.getIPAddress();
    Toast.makeText(getApplicationContext(), new StringBuilder(String.valueOf(strIPAddress.contains("7777") ? "Visit, " : "")).append(strIPAddress).toString(), Toast.LENGTH_LONG).show();
    ((TextView) findViewById(R.id.txtServerIP)).setText(this.server.getIPAddress());
    ((Button) findViewById(R.id.buttonStop)).setOnClickListener(new C03961());
  }

  public void onBackPressed() {
    super.onBackPressed();
    try {
      this.server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
    finish();
  }
}
