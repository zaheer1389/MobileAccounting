package com.adslinfotech.mobileaccounting.image;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.ui.PickerDateActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.Permissions;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class PhotoPicker extends PickerDateActivity {
  private int REQUEST_CAMERA = 0;
  private int SELECT_FILE = 1;
  protected Dialog dialogUploadPhoto;
  private boolean isCameraTask;

  protected abstract void setImage(Bitmap bitmap);

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case Permissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE /*123*/:
        if (grantResults.length > 0 && grantResults[0] == 0) {
          if (this.isCameraTask) {
            cameraIntent();
            return;
          } else {
            galleryIntent();
            return;
          }
        }
        return;
      default:
        return;
    }
  }

  protected void alertBoxForUploadImageOp(boolean isEditable) {
    Log.d("upload image", "alert upload image called");
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
    Log.d("upload image", "alert uplod image return");
  }

  public void onClick(View v) {
    boolean result;
    switch (v.getId()) {
      case R.id.btn_CancelDialogUpload:
        this.dialogUploadPhoto.dismiss();
        return;
      case R.id.img_profile:
        alertBoxForUploadImageOp(true);
        return;
      case R.id.txt_openCamera:
        this.isCameraTask = true;
        result = Permissions.checkPermission(this);
        this.dialogUploadPhoto.dismiss();
        if (result) {
          cameraIntent();
          return;
        }
        return;
      case R.id.txt_openGallery:
        this.isCameraTask = false;
        result = Permissions.checkPermission(this);
        this.dialogUploadPhoto.dismiss();
        if (result) {
          galleryIntent();
          return;
        }
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  private void galleryIntent() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction("android.intent.action.GET_CONTENT");
    startActivityForResult(Intent.createChooser(intent, "Select File"), this.SELECT_FILE);
  }

  private void cameraIntent() {
    startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), this.REQUEST_CAMERA);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != -1) {
      return;
    }
    if (requestCode == this.SELECT_FILE) {
      onSelectFromGalleryResult(data);
    } else if (requestCode == this.REQUEST_CAMERA) {
      onCaptureImageResult(data);
    }
  }

  private void onCaptureImageResult(Intent data) {
    Bitmap thumbnail = (Bitmap) data.getExtras().get(AppConstants.DATA);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    thumbnail.compress(CompressFormat.JPEG, 90, bytes);
    File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
    try {
      destination.createNewFile();
      FileOutputStream fo = new FileOutputStream(destination);
      fo.write(bytes.toByteArray());
      fo.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    setImage(thumbnail);
  }

  private void onSelectFromGalleryResult(Intent data) {
    Bitmap bm = null;
    if (data != null) {
      try {
        bm = Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    setImage(bm);
  }
}
