package com.adslinfotech.mobileaccounting.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEdit;
import com.adslinfotech.mobileaccounting.database.DatabaseExportImport;
import com.adslinfotech.mobileaccounting.ui.ActivityLogin;

import java.io.File;

public class BrowesDbActivity extends ActivityEdit {
  private String mPath;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_browes_db);
    Intent intent = getIntent();
    String lastPathSegment = intent.getData().getLastPathSegment();
    this.mPath = intent.getData().getPath();
    ((TextView) findViewById(R.id.tv_browsed_file_name)).setText(lastPathSegment);
    ((TextView) findViewById(R.id.tv_browsed_file_detail)).setText("File Size : " + new File(this.mPath).getTotalSpace() + "\npath : " + this.mPath);
    ((Button) findViewById(R.id.btn_browsed_file_import)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        BrowesDbActivity.this.showConfirmPassDialog();
      }
    });
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  public void passwordConfirmed(int i) {
    DatabaseExportImport.importDb(this, new File(this.mPath));
  }

  public void onBackPressed() {
    openMainScreen();
    super.onBackPressed();
  }

  private void openMainScreen() {
    Intent intent = new Intent(this, ActivityLogin.class);
    intent.addFlags(4194304);
    intent.addFlags(131072);
    intent.addFlags(16);
    startActivity(intent);
    finish();
  }

  protected void setImage(Bitmap thumbnail) {
  }
}
