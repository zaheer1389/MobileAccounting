package com.adslinfotech.mobileaccounting.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ImportExcel
{
  public List<String> read(String paramString1, String paramString2)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    File file = new File(paramString2);
    if (file.exists()) {}
    for (;;)
    {
      try
      {
        Sheet sheet = Workbook.getWorkbook(file).getSheet(0);
        int i = 0;
        if (i < sheet.getRows())
        {
          if (sheet.getCell(0, i).getContents().equalsIgnoreCase(paramString1))
          {
            int j = 0;
            if (j < sheet.getColumns())
            {
              localArrayList.add(sheet.getCell(j, i).getContents());
              j += 1;
              continue;
            }
          }
          i += 1;
        }
        else
        {
          localArrayList.add("File not found..!");
        }
      }
      catch (BiffException e)
      {
        e.printStackTrace();
        if (localArrayList.size() == 0) {
          localArrayList.add("Data not found..!");
        }
        return localArrayList;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}

