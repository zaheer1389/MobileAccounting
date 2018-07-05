package com.adslinfotech.mobileaccounting.files;

import android.os.Environment;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppConstants.FILE_EXTENSION;
import com.itextpdf.text.xml.xmp.PdfSchema;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivityFileManager {
  private String extention;
  private String extention1 = null;
  private String mPath;
  private ArrayList<HashMap<String, String>> songsList = new ArrayList();

  class FileExtensionFilter implements FilenameFilter {
    FileExtensionFilter() {
    }

    public boolean accept(File dir, String name) {
      if (ActivityFileManager.this.extention1 != null) {
        return name.endsWith(ActivityFileManager.this.extention) || name.endsWith(ActivityFileManager.this.extention1);
      } else {
        return name.endsWith(ActivityFileManager.this.extention);
      }
    }
  }

  public ActivityFileManager(String ex) {
    this.extention = ex;
    if (ex.equalsIgnoreCase(PdfSchema.DEFAULT_XPATH_ID)) {
      this.mPath = Environment.getExternalStorageDirectory().getPath() + AppConstants.FOLDER + "/pdf";
      this.extention = ".pdf";
    } else if (ex.equalsIgnoreCase("xls")) {
      this.mPath = Environment.getExternalStorageDirectory().getPath() + AppConstants.FOLDER + "/excel";
      this.extention = ".xls";
    } else {
      this.mPath = Environment.getExternalStorageDirectory().getPath() + AppConstants.FOLDER;
      this.extention = FILE_EXTENSION.BACKUP;
      this.extention1 = ".dp";
    }
  }

  public ArrayList<HashMap<String, String>> getPlayList() {
    File home = new File(this.mPath);
    if (home.exists() && (!(home == null && home.length() == 0) && home.listFiles(new FileExtensionFilter()).length > 0)) {
      for (File file : home.listFiles(new FileExtensionFilter())) {
        HashMap<String, String> song = new HashMap();
        String name = file.getName();
        name = name.substring(0, name.lastIndexOf("."));
        if (name.length() > 45) {
          song.put("songTitle", name.substring(0, 45));
        } else {
          song.put("songTitle", name);
        }
        song.put("songPath", file.getPath());
        this.songsList.add(song);
      }
    }
    return this.songsList;
  }
}
