package com.adslinfotech.mobileaccounting.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import com.adslinfotech.mobileaccounting.ui.SessionManager;

public class UserEmailFetcher {
  public static String getEmail(Context context) {
    Account account = getAccount(AccountManager.get(context));
    if (account == null) {
      return SessionManager.getEmail();
    }
    return account.name;
  }

  private static Account getAccount(AccountManager accountManager) {
    Account[] accounts = accountManager.getAccountsByType("com.google");
    if (accounts.length > 0) {
      return accounts[0];
    }
    return null;
  }
}
