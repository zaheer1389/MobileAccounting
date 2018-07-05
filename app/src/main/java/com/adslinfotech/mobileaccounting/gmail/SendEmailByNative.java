package com.adslinfotech.mobileaccounting.gmail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.adslinfotech.mobileaccounting.utils.AppConstants;

public class SendEmailByNative {
  String body;
  String email;
  String subject;
  Uri uri;

  public SendEmailByNative(Context context, String email, String subject, String body, Uri uri) {
    this.email = email;
    this.subject = subject;
    this.body = body;
    this.uri = uri;
  }

  public SendEmailByNative(Context context, String email, String subject, String body) {
    this.email = email;
    this.subject = subject;
    this.body = body;
  }

  public Intent sendEmailIntent() {
    this.body += "\n\n" + AppConstants.PROMOTIONAL_APP_LINK_FULL;
    Intent emailIntent = new Intent("android.intent.action.SEND");
    emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
    emailIntent.setType("plain/text");
    emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{this.email});
    emailIntent.putExtra("android.intent.extra.SUBJECT", this.subject);
    emailIntent.putExtra("android.intent.extra.TEXT", this.body);
    emailIntent.putExtra("android.intent.extra.STREAM", this.uri);
    return emailIntent;
  }

  public Intent sendMailIntent() {
    Intent emailIntent = new Intent("android.intent.action.SEND");
    emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
    emailIntent.setType("plain/text");
    emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{"shreshthatechno@gmail.com"});
    emailIntent.putExtra("android.intent.extra.SUBJECT", this.subject);
    emailIntent.putExtra("android.intent.extra.TEXT", this.body);
    return emailIntent;
  }
}
