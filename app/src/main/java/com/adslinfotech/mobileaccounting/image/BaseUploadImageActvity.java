package com.adslinfotech.mobileaccounting.image;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditAccount;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditProfile;
import com.adslinfotech.mobileaccounting.activities.edit.ActivityEditTransaction;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddAccount;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCredit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddCreditDebit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityAddDebit;
import com.adslinfotech.mobileaccounting.activities.entry.ActivityRegistration;
import com.adslinfotech.mobileaccounting.ui.PickerDateActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import java.io.File;
import java.util.List;

public abstract class BaseUploadImageActvity extends PickerDateActivity {
  private static final int CROP_FROM_CAMERA = 26;
  private static final CharSequence ERROR_CROPPING_IMAGE = "Error cropping image";
  public static final int IMAGE_GALLERY = 153;
  private static final int INSUFFICEINT_SPACE = 12;
  private static final int NO_SD_CARD = 13;
  private static final int PICK_FROM_CAMERA = 1;
  private static final int REQ_CODE_GALLERY = 156;
  private static final int SUFFICE_SPACE = 11;
  private static final String TAG = "BaseAddUser";
  protected Bitmap CURRENT_BITMAP;
  protected int IMAGE_URI_TYPE;
  protected Dialog dialogUploadPhoto;
  private Uri mImageCaptureUri = null;
  private Activity mRegisteredActivity;

  protected void alertBoxForUploadImageOp() {
    Log.e("upload image", "alert uplod image called");
    this.dialogUploadPhoto = new Dialog(this, R.style.WindowTitleBackground);
    this.dialogUploadPhoto.requestWindowFeature(1);
    this.dialogUploadPhoto.setContentView(R.layout.dialog_upload_image);
    this.dialogUploadPhoto.setCancelable(false);
    TextView mTvGallary = (TextView) this.dialogUploadPhoto.findViewById(R.id.txt_openGallery);
    TextView mTvRemove = (TextView) this.dialogUploadPhoto.findViewById(R.id.txt_remove_image);
    TextView mTvZoom = (TextView) this.dialogUploadPhoto.findViewById(R.id.txt_zoom_image);
    Button mButtonCancel = (Button) this.dialogUploadPhoto.findViewById(R.id.btn_CancelDialogUpload);
    ((TextView) this.dialogUploadPhoto.findViewById(R.id.txt_openCamera)).setOnClickListener(this);
    mTvGallary.setOnClickListener(this);
    mTvRemove.setOnClickListener(this);
    mTvZoom.setOnClickListener(this);
    mButtonCancel.setOnClickListener(this);
    this.dialogUploadPhoto.show();
    Log.e("upload image", "alert uplod image return");
  }

  protected void openCamera(Activity context) {
    Log.e("upload image", "openCamera called");
    this.mRegisteredActivity = this;
    int storage = hasStorage();
    if (13 == storage) {
      Toast.makeText(this, getResources().getString(R.string.no_sd_card), Toast.LENGTH_LONG).show();
    } else if (12 == storage) {
      Toast.makeText(this, getResources().getString(R.string.insuffice_space), Toast.LENGTH_LONG).show();
    } else {
      launchCamera();
    }
    Log.e("upload image", "openCamera return");
  }

  private int hasStorage() {
    String state = Environment.getExternalStorageState();
    Log.e(TAG, "storage state is " + state);
    if (!"mounted".equals(state) || "mounted_ro".equals(state)) {
      return 13;
    }
    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    return ((double) stat.getAvailableBlocks()) * ((double) stat.getBlockSize()) > 1024.0d ? 11 : 12;
  }

  private void launchCamera() {
    Log.e("upload image", "launchCamera called");
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    this.mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    intent.putExtra("output", this.mImageCaptureUri);
    intent.setFlags(131072);
    try {
      intent.putExtra("return-data", true);
      startActivityForResult(intent, 1);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
    }
  }

  protected void openGallery(Activity context) {
    Log.e("upload image", "openGallery called");
    if (context != null) {
      Log.e("upload image", "openGallery contex is null");
      this.mRegisteredActivity = this;
    }
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction("android.intent.action.GET_CONTENT");
    intent.setFlags(131072);
    startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQ_CODE_GALLERY);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.e(TAG, "onActivityResult called");
    if (requestCode == REQ_CODE_GALLERY && resultCode == -1 && data != null) {
      Log.e(TAG, "REQ_CODE_GALLERY");
      this.mImageCaptureUri = data.getData();
      doCrop();
    } else if (requestCode == 1) {
      doCrop();
    } else if (requestCode == 26 && resultCode == -1) {
      Bundle extras = null;
      if (data != null) {
        extras = data.getExtras();
      }
      if (extras != null) {
        Bitmap photo = (Bitmap) extras.getParcelable(AppConstants.DATA);
        File f = new File(this.mImageCaptureUri.getPath());
        if (f.exists()) {
          f.delete();
        }
        if (photo != null) {
          this.CURRENT_BITMAP = photo;
          this.IMAGE_URI_TYPE = IMAGE_GALLERY;
          if (this.mRegisteredActivity instanceof ActivityRegistration) {
            ((ActivityRegistration) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityEditProfile) {
            ((ActivityEditProfile) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityEditAccount) {
            ((ActivityEditAccount) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityAddAccount) {
            ((ActivityAddAccount) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityAddCredit) {
            ((ActivityAddCredit) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityAddDebit) {
            ((ActivityAddDebit) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityEditTransaction) {
            ((ActivityEditTransaction) this.mRegisteredActivity).setImage(photo);
          } else if (this.mRegisteredActivity instanceof ActivityAddCreditDebit) {
            ((ActivityAddCreditDebit) this.mRegisteredActivity).setImage(photo);
          }
        }
      } else {
        Toast.makeText(this, ERROR_CROPPING_IMAGE, Toast.LENGTH_SHORT).show();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void doCrop() {
    try {
      Intent intent = new Intent("com.android.camera.action.CROP");
      intent.setType("image/*");
      List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
      if (list.size() == 0) {
        Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        return;
      }
      intent.setData(this.mImageCaptureUri);
      intent.putExtra("outputX", 650);
      intent.putExtra("outputY", 650);
      intent.putExtra("aspectX", 1);
      intent.putExtra("aspectY", 1);
      intent.putExtra("scale", true);
      intent.putExtra("return-data", true);
      Intent i = new Intent(intent);
      ResolveInfo res = (ResolveInfo) list.get(0);
      i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
      startActivityForResult(i, 26);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
