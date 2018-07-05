package com.adslinfotech.mobileaccounting.alarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ShareActivity extends SimpleAccountingActivity implements OnItemClickListener {
  private static final String TAG = ShareActivity.class.getSimpleName();
  private String facebookBody;
  private Set<String> htmlActivitiesPackages;
  private CharSequence htmlbody;
  private ShareIntentAdapter mAdapter;
  private GridView mGrid;
  private LayoutInflater mInflater;
  private int mMaxColumns;
  private List<ResolveInfo> plainTextActivities;
  private String subject;
  private String textbody;
  private String twitterBody;

  public class ShareIntentAdapter extends BaseAdapter {
    public int getCount() {
      return ShareActivity.this.plainTextActivities != null ? ShareActivity.this.plainTextActivities.size() : 0;
    }

    public ResolveInfo getItem(int position) {
      return (ResolveInfo) ShareActivity.this.plainTextActivities.get(position);
    }

    public long getItemId(int position) {
      return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      View view;
      if (convertView == null) {
        view = ShareActivity.this.mInflater.inflate(R.layout.griditem_share_us, parent, false);
      } else {
        view = convertView;
      }
      bindView(view, (ResolveInfo) ShareActivity.this.plainTextActivities.get(position));
      return view;
    }

    private final void bindView(View view, ResolveInfo info) {
      ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
      ((TextView) view.findViewById(android.R.id.text1)).setText(info.activityInfo.applicationInfo.loadLabel(ShareActivity.this.getPackageManager()).toString());
      icon.setImageDrawable(info.activityInfo.applicationInfo.loadIcon(ShareActivity.this.getPackageManager()));
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.dialog_share_us_chooser);
    setTitle("Share as app");
    init();
    this.mInflater = LayoutInflater.from(getApplicationContext());
    share();
  }

  private void init() {
    this.subject = SessionManager.getName() + "Suggest you Simple Accounting App for Andoroid Mobile";
    this.textbody = "This app works amazing and i am very much satisfied with this app. It helps me to manage all my accounting on finger tips.\nI would like to suggest you to please download or install this app\nhttp://bit.ly/1LGUjOE";
    this.htmlbody = getResources().getString(R.string.ShartAppHtml);
    this.twitterBody = "http://bit.ly/1LGUjOE";
    this.facebookBody = "http://bit.ly/1LGUjOE";
  }

  @SuppressLint({"NewApi"})
  public void share() {
    Intent sendIntent = new Intent("android.intent.action.SEND");
    sendIntent.setType("text/plain");
    this.plainTextActivities = getPackageManager().queryIntentActivities(sendIntent, 0);
    if (this.plainTextActivities.size() > 0) {
      this.htmlActivitiesPackages = new HashSet();
      sendIntent.setType("text/html");
      for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(sendIntent, 0)) {
        this.htmlActivitiesPackages.add(resolveInfo.activityInfo.packageName);
      }
      this.mAdapter = new ShareIntentAdapter();
      this.mGrid = (GridView) findViewById(R.id.resolver_grid);
      this.mGrid.setAdapter(this.mAdapter);
      this.mGrid.setOnItemClickListener(this);
      this.mMaxColumns = 2;
      this.mGrid.setNumColumns(Math.min(this.plainTextActivities.size(), this.mMaxColumns));
      return;
    }
    Toast.makeText(getApplicationContext(), "No social apps installed to share ChurchLink!", Toast.LENGTH_LONG).show();
  }

  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    ResolveInfo info = this.mAdapter.getItem(position);
    Intent intent = new Intent("android.intent.action.SEND");
    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
    intent.setType("text/plain");
    intent.putExtra("android.intent.extra.TITLE", this.subject);
    intent.putExtra("android.intent.extra.SUBJECT", this.subject);
    if (info.activityInfo.packageName.contains("facebook")) {
      intent.putExtra("android.intent.extra.TEXT", this.facebookBody);
      intent.putExtra("android.intent.extra.STREAM", R.drawable.accounting_icon);
    } else if (info.activityInfo.packageName.contains("twitter")) {
      intent.putExtra("android.intent.extra.TEXT", this.twitterBody);
    } else if (this.htmlActivitiesPackages.contains(info.activityInfo.packageName)) {
      intent.putExtra("android.intent.extra.TEXT", this.htmlbody);
    } else {
      intent.putExtra("android.intent.extra.TEXT", this.textbody);
    }
    Log.d(TAG, info.activityInfo.packageName);
    startActivity(intent);
  }
}
