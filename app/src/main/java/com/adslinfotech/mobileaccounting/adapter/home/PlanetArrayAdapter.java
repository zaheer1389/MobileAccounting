package com.adslinfotech.mobileaccounting.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.Planet;
import java.util.List;

public class PlanetArrayAdapter extends ArrayAdapter<Planet> {
  private TextViewClickedLister callback;
  private LayoutInflater inflater;

  public interface TextViewClickedLister {
    void textViewClicked(int i);
  }

  public class PlanetViewHolder {
    private CheckBox checkBox;
    private TextView textView;

    public PlanetViewHolder(TextView textView, CheckBox checkBox) {
      this.checkBox = checkBox;
      this.textView = textView;
    }

    public CheckBox getCheckBox() {
      return this.checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
      this.checkBox = checkBox;
    }

    public TextView getTextView() {
      return this.textView;
    }

    public void setTextView(TextView textView) {
      this.textView = textView;
    }
  }

  public PlanetArrayAdapter(Context context, List<Planet> planetList) {
    super(context, R.layout.list_item_row_cb, R.id.tv_title, planetList);
    this.inflater = LayoutInflater.from(context);
    this.callback = (TextViewClickedLister) context;
  }

  public View getView(final int position, View convertView, ViewGroup parent) {
    TextView textView;
    CheckBox checkBox;
    Planet planet = (Planet) getItem(position);
    if (convertView == null) {
      convertView = this.inflater.inflate(R.layout.list_item_row_cb, null);
      textView = (TextView) convertView.findViewById(R.id.tv_title);
      checkBox = (CheckBox) convertView.findViewById(R.id.cb_select);
      convertView.setTag(new PlanetViewHolder(textView, checkBox));
      checkBox.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          CheckBox cb = (CheckBox) v;
          ((Planet) cb.getTag()).setChecked(cb.isChecked());
        }
      });
      textView.setOnClickListener(new OnClickListener() {
        public void onClick(View arg0) {
          PlanetArrayAdapter.this.callback.textViewClicked(position);
        }
      });
    } else {
      PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
      checkBox = viewHolder.getCheckBox();
      textView = viewHolder.getTextView();
    }
    checkBox.setTag(planet);
    checkBox.setChecked(planet.isChecked());
    textView.setText(planet.getName());
    return convertView;
  }
}
