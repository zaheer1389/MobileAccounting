package com.adslinfotech.mobileaccounting.database;

import android.database.Cursor;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingApp;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.database.Query.ACCOUNT;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.text.NumberFormat;

public class FetchCursor {
    public static final Cursor getCursor(String query) {
        return SimpleAccountingApp.getDBHandler().getReadableDatabase().rawQuery(query, null);
    }

    public static final Account getAccount(Cursor cursor, boolean isBalance) {
        Account account = new Account();
        account.setName(cursor.getString(cursor.getColumnIndex(ACCOUNT.NAME)));
        account.setAccountId(cursor.getInt(cursor.getColumnIndex("AID")));
        account.setEmail(cursor.getString(cursor.getColumnIndex("PersonEmail")));
        account.setMobile(cursor.getString(cursor.getColumnIndex("PersonMobile")));
        account.setRemark(cursor.getString(cursor.getColumnIndex("Remark")));
        account.setImage(cursor.getBlob(cursor.getColumnIndex("Image")));
        if (isBalance) {
            double bal;
            NumberFormat newFormat = AppUtils.getCurrencyFormatter();
            try {
                bal = Double.parseDouble(cursor.getString(cursor.getColumnIndex("bal")));
            } catch (Exception e) {
                bal = 0.0d;
            }
            if (bal > 0.0d) {
                account.setBalance(newFormat.format(bal) + "/-Cr");
            } else if (0.0d > bal) {
                account.setBalance(newFormat.format(-1.0d * bal) + "/-Db");
            } else {
                account.setBalance("0.00");
            }
        }
        return account;
    }
}
