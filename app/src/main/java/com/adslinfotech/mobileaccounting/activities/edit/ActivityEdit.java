package com.adslinfotech.mobileaccounting.activities.edit;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.image.PhotoPicker;
import com.adslinfotech.mobileaccounting.ui.SessionManager;

public abstract class ActivityEdit extends PhotoPicker {
  private Dialog dialogConfirmPass;
  private int index;
  private EditText mEtPass;

  public abstract void passwordConfirmed(int i);

  public void checkPasswordRequired(int i) {
    this.index = i;
    if (SessionManager.isPasswordRequired()) {
      showConfirmPassDialog();
    } else {
      passwordConfirmed(i);
    }
  }

  public void showConfirmPassDialog() {
    this.dialogConfirmPass = new Dialog(this, R.style.WindowTitleBackground);
    this.dialogConfirmPass.requestWindowFeature(1);
    this.dialogConfirmPass.setContentView(R.layout.dialog_with_edittext);
    this.dialogConfirmPass.setCancelable(false);
    this.mEtPass = (EditText) this.dialogConfirmPass.findViewById(R.id.et_dialong_name);
    Button mButtonOk = (Button) this.dialogConfirmPass.findViewById(R.id.btn_dialog_OK);
    ((Button) this.dialogConfirmPass.findViewById(R.id.btn_dialog_Cancel)).setOnClickListener(this);
    mButtonOk.setOnClickListener(this);
    this.dialogConfirmPass.show();
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_dialog_Cancel:
        this.dialogConfirmPass.dismiss();
        return;
      case R.id.btn_dialog_OK:
        confirmPassword();
        return;
      default:
        super.onClick(v);
        return;
    }
  }

  private void confirmPassword() {
    if (this.mEtPass.getText().toString().equalsIgnoreCase(SessionManager.getPassword())) {
      this.dialogConfirmPass.dismiss();
      passwordConfirmed(this.index);
      return;
    }
    this.mEtPass.setError(getResources().getString(R.string.txt_Incorrect_pass));
  }
}
