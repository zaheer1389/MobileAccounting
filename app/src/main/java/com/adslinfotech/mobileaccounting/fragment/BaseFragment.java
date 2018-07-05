package com.adslinfotech.mobileaccounting.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import com.adslinfotech.mobileaccounting.utils.AppUtils;
import java.text.NumberFormat;

public abstract class BaseFragment extends Fragment {
  protected NumberFormat newFormat = AppUtils.getCurrencyFormatter();

  public abstract void onClick(View view);
}
