package com.adslinfotech.mobileaccounting.dropbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.adslinfotech.mobileaccounting.R;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import java.util.List;

public class DownloadFileAdapter extends BaseAdapter {
  private List<Metadata> files;
  private LayoutInflater lInflater;
  private View view;

  private class Holder {
    ImageView ivImageDownloadOrBrowableDir;
    ImageView ivImageFolderOrFile;
    TextView tvDownloadFileOrFolderName;

    private Holder() {
    }
  }

  public DownloadFileAdapter(Context context, List<Metadata> files) {
    this.files = files;
    this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public int getCount() {
    return this.files.size();
  }

  public Object getItem(int position) {
    return Integer.valueOf(position);
  }

  public long getItemId(int position) {
    return (long) position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder;
    this.view = convertView;
    if (this.view == null) {
      holder = new Holder();
      this.view = this.lInflater.inflate(R.layout.downloadfileinflater, null);
      holder.ivImageDownloadOrBrowableDir = (ImageView) this.view.findViewById(R.id.ivImageDownloadOrBrowableDir);
      holder.ivImageFolderOrFile = (ImageView) this.view.findViewById(R.id.ivImageFolderOrFile);
      holder.tvDownloadFileOrFolderName = (TextView) this.view.findViewById(R.id.tvDownloadFileFileName);
      this.view.setTag(holder);
    } else {
      holder = (Holder) this.view.getTag();
    }
    Metadata file = (Metadata) this.files.get(position);
    if (file instanceof FileMetadata) {
      holder.ivImageDownloadOrBrowableDir.setImageResource(R.drawable.downloadicon);
      holder.ivImageFolderOrFile.setImageResource(R.drawable.fileicon);
    } else {
      holder.ivImageDownloadOrBrowableDir.setImageResource(R.drawable.browsedirectoryicon);
      holder.ivImageFolderOrFile.setImageResource(R.drawable.dropboxdiricon);
    }
    holder.tvDownloadFileOrFolderName.setText(file.getPathDisplay());
    return this.view;
  }
}
