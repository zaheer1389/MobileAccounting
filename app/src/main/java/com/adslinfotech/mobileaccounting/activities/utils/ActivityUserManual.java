package com.adslinfotech.mobileaccounting.activities.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.utils.AppConstants.EXTRA;
import com.adslinfotech.mobileaccounting.utils.AppConstants.HTML;

public class ActivityUserManual extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView view = new WebView(getApplicationContext());
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        String url = getIntent().getStringExtra(EXTRA.HTML_FILE);
        view.loadUrl(url);
        view.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        setContentView((View) view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (url.equals(HTML.USER_MANUAL)) {
            getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[8]);
        } else if (url.equals(HTML.FAQ)) {
            getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[11]);
        } else {
            getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_menu_items_profile)[15]);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != Menus.HOME) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return false;
    }
}
