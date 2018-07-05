package com.adslinfotech.mobileaccounting.gmail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ShareHelper implements OnItemClickListener {
  private static final String TAG = ShareHelper.class.getSimpleName();
  private String facebookBody;
  private Set<String> htmlActivitiesPackages;
  private CharSequence htmlbody;
  private ShareIntentAdapter mAdapter;
  private Activity mContext;
  private Dialog mDialog;
  private GridView mGrid;
  private LayoutInflater mInflater;
  private int mMaxColumns;
  private List<ResolveInfo> plainTextActivities;
  private String subject;
  private String textbody;
  private String twitterBody;

  public class ShareIntentAdapter extends BaseAdapter {
    public int getCount() {
      return ShareHelper.this.plainTextActivities != null ? ShareHelper.this.plainTextActivities.size() : 0;
    }

    public ResolveInfo getItem(int position) {
      return (ResolveInfo) ShareHelper.this.plainTextActivities.get(position);
    }

    public long getItemId(int position) {
      return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      View view;
      if (convertView == null) {
        view = ShareHelper.this.mInflater.inflate(R.layout.griditem_share_us, parent, false);
      } else {
        view = convertView;
      }
      bindView(view, (ResolveInfo) ShareHelper.this.plainTextActivities.get(position));
      return view;
    }

    private final void bindView(View view, ResolveInfo info) {
      ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
      ((TextView) view.findViewById(android.R.id.text1)).setText(info.activityInfo.applicationInfo.loadLabel(ShareHelper.this.mContext.getPackageManager()).toString());
      icon.setImageDrawable(info.activityInfo.applicationInfo.loadIcon(ShareHelper.this.mContext.getPackageManager()));
    }
  }

  public ShareHelper(Activity context, String subject, String textbody) {
    this.mContext = context;
    this.subject = subject;
    this.textbody = textbody;
    this.htmlbody = textbody;
    this.twitterBody = textbody;
    this.facebookBody = textbody;
  }

  public ShareHelper(Activity context, String subject, String textbody, CharSequence htmlbody, String twitterBody, String facebookBody) {
    this.mContext = context;
    this.subject = subject;
    this.textbody = textbody;
    this.htmlbody = htmlbody;
    this.twitterBody = twitterBody;
    this.facebookBody = facebookBody;
  }

  @SuppressLint({"NewApi"})
  public void share() {
    this.mInflater = LayoutInflater.from(this.mContext);
    Intent sendIntent = new Intent("android.intent.action.SEND");
    sendIntent.setType("text/plain");
    this.plainTextActivities = this.mContext.getPackageManager().queryIntentActivities(sendIntent, 0);
    if (this.plainTextActivities.size() > 0) {
      Builder builder;
      this.htmlActivitiesPackages = new HashSet();
      sendIntent.setType("text/plain");
      for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentActivities(sendIntent, 0)) {
        this.htmlActivitiesPackages.add(resolveInfo.activityInfo.packageName);
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
      builder.setTitle("Share as app");
      builder.setView(chooserView);
      this.mDialog = builder.create();
      this.mDialog.show();
      return;
    }
    Toast.makeText(this.mContext, "No social apps installed to share ChurchLink!", Toast.LENGTH_LONG).show();
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
    this.mContext.startActivity(intent);
    this.mDialog.dismiss();
  }
}
