package com.adslinfotech.mobileaccounting.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adslinfotech.mobileaccounting.R;

public class ActivityUserManual
  extends AppCompatActivity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    WebView webView = new WebView(getApplicationContext());
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    webView.loadUrl("file:///android_asset/User_Manual.htm");
    webView.setWebViewClient(new WebViewClient()
    {
      public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        paramAnonymousWebView.loadUrl(paramAnonymousString);
        return false;
      }
    });
    setContentView(webView);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[8]);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finish();
      return false;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/ui/ActivityUserManual.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */