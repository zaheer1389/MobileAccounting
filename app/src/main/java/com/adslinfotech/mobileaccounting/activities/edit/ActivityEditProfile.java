package com.adslinfotech.mobileaccounting.activities.edit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import br.liveo.utils.Menus;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.activities.home.HomeFragment;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.database.FetchData;
import com.adslinfotech.mobileaccounting.image.FullScreenImage;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.ByteArrayOutputStream;

public class ActivityEditProfile extends ActivityEdit implements OnClickListener {
  private String email;
  private AdView mAdView;
  private EditText mEtEmail;
  private EditText mEtMobile;
  private EditText mEtName;
  private FetchData mFetchData;
  private ImageView mImgProfile;
  private byte[] mNewEncodedImage;
  private Resources mResource;
  private String mobile;
  private String name;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_edit_profile);
    this.mResource = getResources();
    this.mFetchData = new FetchData();
    getSupportActionBar().setTitle(this.mResource.getStringArray(R.array.nav_menu_items_profile)[1]);
    getViews();
    setProfileData();
    hideKeyPad();
  }

  protected void populateSetDate(int id, int mYear2, int i, int mDay2) {
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != Menus.HOME) {
      return super.onOptionsItemSelected(item);
    }
    finish();
    return false;
  }

  private void getViews() {
    this.mEtName = (EditText) findViewById(R.id.et_name);
    this.mEtEmail = (EditText) findViewById(R.id.et_email);
    this.mEtMobile = (EditText) findViewById(R.id.et_mobile);
    this.mImgProfile = (ImageView) findViewById(R.id.img_profile);
    Button btCancel = (Button) findViewById(R.id.btn_cancel);
    ((Button) findViewById(R.id.btn_save)).setOnClickListener(this);
    btCancel.setOnClickListener(this);
    boolean isInternetPresent = AppUtils.isNetworkAvailable(getApplicationContext());
    if (!SessionManager.isProUser() && isInternetPresent) {
      this.mAdView = new AdView(this);
      this.mAdView = (AdView) findViewById(R.id.adView);
      this.mAdView.setVisibility(View.VISIBLE);
      this.mAdView.loadAd(new Builder().addTestDevice(AppConstants.TEST_DEVICE).build());
    }
  }

  public void onClick(View v) {
    SessionManager.incrementInteractionCount();
    switch (v.getId()) {
      case R.id.btn_cancel:
        finish();
        return;
      case R.id.btn_save:
        validateFields();
        return;
      case R.id.img_profile:
        alertBoxForUploadImageOp(true);
        return;
      case R.id.txt_remove_image:
        this.mNewEncodedImage = null;
        this.mImgProfile.setImageResource(R.drawable.add_profile_pic);
        this.dialogUploadPhoto.dismiss();
        return;
      case R.id.txt_zoom_image:
        Intent intent1 = new Intent(this, FullScreenImage.class);
        intent1.putExtra("image", this.mNewEncodedImage);
        startActivity(intent1);
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  public void setImage(Bitmap photo) {
    if (photo != null) {
      photo = Bitmap.createScaledBitmap(photo, 1000, 1000, true);
      this.mImgProfile.setImageBitmap(photo);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      photo.compress(CompressFormat.JPEG, 90, baos);
      this.mNewEncodedImage = baos.toByteArray();
    }
  }

  private void setProfileData() {
    UserDao user = this.mFetchData.getProfileDetail();
    Log.d("EditProfile", "user: " + user);
    this.mEtName.setText(user.getName());
    this.mEtEmail.setText(user.getEmail());
    this.mEtMobile.setText(user.getMobile());
    this.mNewEncodedImage = user.getImage();
    setImage(this.mNewEncodedImage);
  }

  private void setImage(byte[] byteImage) {
    try {
      if (AppUtils.setImage(this.mImgProfile, byteImage)) {
        this.mImgProfile.setImageResource(R.drawable.profile_icon);
      }
    } catch (Exception e) {
    }
  }

  private void validateFields() {
    this.name = this.mEtName.getText().toString().trim();
    this.email = this.mEtEmail.getText().toString().trim();
    this.mobile = this.mEtMobile.getText().toString().trim();
    if (TextUtils.isEmpty(this.name)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Entername), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(this.email)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Enteremail), Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(this.mobile)) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Entermobile), Toast.LENGTH_SHORT).show();
    } else {
      checkPasswordRequired(0);
    }
  }

  public void passwordConfirmed(int i) {
    updateProfile();
  }

  private void updateProfile() {
    HomeFragment.isProfileUpdate = true;
    UserDao loginUser = SessionManager.getAdminDao();
    UserDao newUser = new UserDao();
    newUser.setUserID(loginUser.getUserID());
    newUser.setUserName(loginUser.getUserName());
    newUser.setPassword(loginUser.getPassword());
    newUser.setName(this.name);
    newUser.setEmail(this.email);
    newUser.setMobile(this.mobile);
    newUser.setImage(this.mNewEncodedImage);
    Log.d("Edit profile insert", "image = " + this.mNewEncodedImage);
    int updateStatus = this.mFetchData.updateProfile(newUser);
    SessionManager.editProfile(this.name, this.email, this.mobile);
    if (updateStatus != 0) {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_Profileupdate), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getApplicationContext(), this.mResource.getString(R.string.txt_NotProfileupdate), Toast.LENGTH_SHORT).show();
    }
    finish();
  }

  public void onPause() {
    if (this.mAdView != null) {
      this.mAdView.pause();
    }
    super.onPause();
  }

  public void onResume() {
    super.onResume();
    if (this.mAdView != null) {
      this.mAdView.resume();
    }
  }

  public void onDestroy() {
    if (this.mAdView != null) {
      this.mAdView.destroy();
    }
    this.mAdView = null;
    super.onDestroy();
  }
}
