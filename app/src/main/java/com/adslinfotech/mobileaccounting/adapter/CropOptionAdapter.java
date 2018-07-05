package com.adslinfotech.mobileaccounting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.image.CropOption;
import java.util.ArrayList;

public class CropOptionAdapter extends ArrayAdapter<CropOption> {
  private LayoutInflater mInflater;
  private ArrayList<CropOption> mOptions;

  public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
    super(context, R.layout.crop_selector, options);
    this.mOptions = options;
    this.mInflater = LayoutInflater.from(context);
  }

  public View getView(int position, View convertView, ViewGroup group) {
    if (convertView == null) {
      convertView = this.mInflater.inflate(R.layout.crop_selector, null);
    }
    CropOption item = (CropOption) this.mOptions.get(position);
    if (item == null) {
      return null;
    }
    ((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(item.icon);
    ((TextView) convertView.findViewById(R.id.tv_name)).setText(item.title);
    return convertView;
  }
}
