package com.adslinfotech.mobileaccounting.drive;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dropbox.DownloadFileAdapter;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;

public class DriveDownload
  extends BaseDriveClass
  implements OnItemClickListener
{
  private ArrayList<Metadata> files;
  private ListView lvDriveDownloadFilesList;
  
  public void onConnected(Bundle paramBundle)
  {
    super.onConnected(paramBundle);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.dropboxdownload);
    this.lvDriveDownloadFilesList = ((ListView)findViewById(R.id.lvDropboxDownloadFilesList));
    this.lvDriveDownloadFilesList.setAdapter(new DownloadFileAdapter(this, files));
    this.lvDriveDownloadFilesList.setOnItemClickListener(this);
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {}
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/drive/DriveDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */