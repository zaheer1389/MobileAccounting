package com.adslinfotech.mobileaccounting.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import com.adslinfotech.mobileaccounting.R;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class ActivityEula {
  private static final String ASSET_EULA = "EULA";
  private static final String PREFERENCES_EULA = "eula";
  private static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";

  interface OnEulaAgreedTo {
    void onEulaAgreedTo();
  }

  static boolean show(final Activity activity) {
    final SharedPreferences preferences = activity.getSharedPreferences(PREFERENCES_EULA, 0);
    if (preferences.getBoolean(PREFERENCE_EULA_ACCEPTED, false)) {
      return false;
    }
    Builder builder = new Builder(activity);
    builder.setTitle(R.string.eula_title);
    builder.setCancelable(true);
    builder.setPositiveButton(R.string.eula_accept, new OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        ActivityEula.accept(preferences);
        if (activity instanceof OnEulaAgreedTo) {
          ((OnEulaAgreedTo) activity).onEulaAgreedTo();
        }
      }
    });
    builder.setNegativeButton(R.string.eula_refuse, new OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        ActivityEula.refuse(activity);
      }
    });
    builder.setOnCancelListener(new OnCancelListener() {
      public void onCancel(DialogInterface dialog) {
        ActivityEula.refuse(activity);
      }
    });
    builder.setMessage(readEula(activity));
    builder.create().show();
    return true;
  }

  private static void accept(SharedPreferences preferences) {
    preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, true).apply();
  }

  private static void refuse(Activity activity) {
    activity.finish();
  }

  private static CharSequence readEula(Activity activity) {
    StringBuilder stringBuilder = new StringBuilder();
    Throwable th;
    BufferedReader bufferedReader = null;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(activity.getAssets().open(ASSET_EULA)));
      try {

        while (true) {
          String line = in.readLine();
          if (line == null) {
            break;
          }
          stringBuilder.append(line).append('\n');
        }
        closeStream(in);
        bufferedReader = in;
      } catch (IOException e) {
        bufferedReader = in;
        try {
          stringBuilder = new StringBuilder();
          closeStream(bufferedReader);
          return stringBuilder;
        } catch (Throwable th2) {
          th = th2;
          closeStream(bufferedReader);
          throw th2;
        }
      } catch (Throwable th3) {
        th = th3;
        bufferedReader = in;
        closeStream(bufferedReader);
        throw th3;
      }
    } catch (IOException e2) {
      stringBuilder = new StringBuilder();
      closeStream(bufferedReader);
      return stringBuilder;
    }
    return stringBuilder;
  }

  private static void closeStream(Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (IOException e) {
      }
    }
  }
}
