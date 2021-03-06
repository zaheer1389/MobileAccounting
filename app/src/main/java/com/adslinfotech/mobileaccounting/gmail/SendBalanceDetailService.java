package com.adslinfotech.mobileaccounting.gmail;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.dao.UserDao;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;

import com.itextpdf.text.html.HtmlTags;

public class SendBalanceDetailService extends IntentService {
  public SendBalanceDetailService() {
    super(SendBalanceDetailService.class.getName());
  }

  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      UserDao user = SessionManager.getAdminDao();
      String sender = intent.getStringExtra("email");
      Account dao = (Account) intent.getExtras().getSerializable(AppConstants.ACCOUNT_SELECTED);
      String pass = intent.getExtras().getString(AppConstants.PASSWORD);
      String body = "Your transaction with MR. " + user.getName() + " \n   = " + dao.getRemark() + "\nVia: Simple Accounting Android App \n For Download this App. Please Visit \nhttp://bit.ly/1LGUjOE";
      String email = dao.getEmail();
      GMailSender gm = new GMailSender(sender, pass);
      try {
        //Log.d(GCMConstants.EXTRA_SENDER, " " + user.getEmail());
        Log.d("sender password", " " + pass);
        Log.d("receiver", " " + email);
        Log.d(HtmlTags.BODY, body);
        gm.sendMail("Simple Accounting", body, sender, email);
      } catch (Exception e) {
        Log.e("while sending", e.getMessage(), e);
      }
    }
  }
}
