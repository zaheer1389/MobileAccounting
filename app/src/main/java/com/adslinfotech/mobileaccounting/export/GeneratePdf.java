package com.adslinfotech.mobileaccounting.export;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.dao.PdfDao;
import com.adslinfotech.mobileaccounting.ui.SessionManager;
import com.adslinfotech.mobileaccounting.utils.AppConstants;
import com.adslinfotech.mobileaccounting.utils.AppUtils;

import com.google.android.gms.drive.DriveFile;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.html.HtmlUtilities;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint({"Instantiatable"})
public class GeneratePdf {
  private static Font catFont = new Font(FontFamily.TIMES_ROMAN, 18.0f, 1);
  private static Format dateFormatSystem;
  private static List<String> mColumns;
  private static Context mContext;
  private static PdfDao mHeader;
  private static int mIndex;
  private static List<PdfDao> mValues;
  private static Font smallBold = new Font(FontFamily.TIMES_ROMAN, HtmlUtilities.DEFAULT_FONT_SIZE, 1);
  private static Font subFont = new Font(FontFamily.TIMES_ROMAN, 16.0f, 1);

  private class Task extends AsyncTask<String, Void, String> {
    private String filePath;

    private Task() {
    }

    protected String doInBackground(String... urls) {
      try {
        String PATH = Environment.getExternalStorageDirectory() + AppConstants.FOLDER + "/pdf";
        File directory = new File(PATH);
        if (!directory.exists()) {
          directory.mkdirs();
        }
        this.filePath = GeneratePdf.this.getPDFfileName(PATH);
        Log.e("PDF Task", "path: 16842794");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(this.filePath));
        document.open();
        GeneratePdf.addMetaData(document);
        GeneratePdf.addContent(document);
        document.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    protected void onPostExecute(String result) {
      GeneratePdf.this.openPDF(this.filePath);
    }
  }

  @SuppressLint({"Instantiatable"})
  public GeneratePdf(Context applicationContext, PdfDao header, ArrayList<String> columns, ArrayList<PdfDao> values, int index) {
    mContext = applicationContext;
    mHeader = header;
    mColumns = columns;
    mValues = values;
    mIndex = index;
    dateFormatSystem = AppUtils.getDateFormat();
  }

  public void pdf(Activity context) {
    if (isStoragePermissionGranted(context)) {
      checkSDCard();
    } else {
      Toast.makeText(context, R.string.permission_storage, Toast.LENGTH_LONG).show();
    }
  }

  public boolean isStoragePermissionGranted(Activity context) {
    if (ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
      return true;
    }
    ActivityCompat.requestPermissions(context, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
    return false;
  }

  private void checkSDCard() {
    String state = Environment.getExternalStorageState();
    Log.v("db export", "storage state is " + state);
    if (!"mounted".equals(state)) {
      Toast.makeText(mContext.getApplicationContext(), "Sorry! SD card not found.", Toast.LENGTH_LONG).show();
    } else if ("mounted_ro".equals(state)) {
      Toast.makeText(mContext.getApplicationContext(), "Please! Insert sd card ", Toast.LENGTH_LONG).show();
    } else {
      new Task().execute(new String[0]);
      Toast.makeText(mContext.getApplicationContext(), "Data exported in pdf file and saved on /SimpleAccounting/pdf folder Succesfully ", Toast.LENGTH_SHORT).show();
    }
  }

  public String getPDFfileName(String folder) {
    String time = AppUtils.getUniqueFileName();
    try {
      String filePath = AppUtils.getValidFileName(mHeader.getFirst());
      Log.e("PDF Task", "filePath: " + filePath);
      return folder + File.separator + filePath + time + ".pdf";
    } catch (Exception e) {
      return folder + File.separator + time + ".pdf";
    }
  }

  private void openPDF(String filePath) {
    File file = new File(filePath);
    Intent target = new Intent("android.intent.action.VIEW");
    target.setDataAndType(Uri.fromFile(file), "application/pdf");
    Intent intent = Intent.createChooser(target, "Open File");
    try {
      intent.addFlags(DriveFile.MODE_READ_ONLY);
      mContext.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static void addMetaData(Document document) {
    document.addTitle("Simple Accounting");
    document.addSubject("Ledger");
    document.addKeywords("Java, PDF, iText");
    document.addAuthor("ADSL Infotech");
    document.addCreator("ADSL Infotech");
  }

  private static void addContent(Document document) throws DocumentException {
    Phrase anchor = new Anchor("", subFont);
    Section subCatPart = new Chapter(new Paragraph(anchor), 1).addSection(new Paragraph("", subFont));
    addEmptyLine(new Paragraph(), 1);
    anchor.add(new Paragraph("Simple Accounting", catFont));
    anchor.add(new Paragraph("\n"));
    anchor.add(new Paragraph("Report generated by: " + SessionManager.getName() + ", " + new Date(), smallBold));
    Anchor a1 = new Anchor("Simple Accounting Desktop Download");
    a1.setReference("http://indianandroidstore.com");
    anchor.add(new Paragraph("\n"));
    document.add(a1);
    switch (mIndex) {
      case 0:
      case 1:
        Paragraph p = new Paragraph("₹", catFont);
        anchor.add(new Paragraph("Ledger of " + mHeader.getFirst() + " : ", smallBold));
        anchor.add(new Paragraph("\n"));
        document.add(p);
        anchor.add(new Paragraph("Mobile : " + mHeader.getFive()));
        anchor.add(new Paragraph("\n"));
        document.add(p);
        anchor.add(new Paragraph("Email : " + mHeader.getSix()));
        anchor.add(new Paragraph("\n"));
        createTable(subCatPart);
        anchor.add(new Paragraph("\n"));
        document.add(p);
        anchor.add(new Paragraph("Credit : " + mHeader.getSecond()));
        anchor.add(new Paragraph("\n"));
        document.add(p);
        anchor.add(new Paragraph("Debit : " + mHeader.getThird()));
        anchor.add(new Paragraph("\n"));
        document.add(p);
        anchor.add(new Paragraph("Balance : " + mHeader.getFour()));
        anchor.add(new Paragraph("\n"));
        if (mIndex == 0) {
          anchor.add(new Paragraph("Opening Bal : " + mHeader.getSeven()));
          anchor.add(new Paragraph("\n"));
          document.add(p);
          anchor.add(new Paragraph("Overall Bal : " + mHeader.getEight()));
          anchor.add(new Paragraph("\n"));
        }
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 2:
        anchor.add(new Paragraph(AppConstants.OVERALL_LEDGER));
        createTable(subCatPart);
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Credit : " + mHeader.getSecond()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Debit : " + mHeader.getThird()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Balance : " + mHeader.getFour()));
        anchor.add(new Paragraph("\n"));
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 3:
        anchor.add(new Paragraph("Day Wise Detail Transaction Report : ", smallBold));
        createTable(subCatPart);
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Credit : " + mHeader.getSecond()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Debit : " + mHeader.getThird()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Balance : " + mHeader.getFour()));
        anchor.add(new Paragraph("\n"));
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 4:
        anchor.add(new Paragraph("Day Wise Commulative Report : ", smallBold));
        createTable(subCatPart);
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Credit : " + mHeader.getSecond()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Debit : " + mHeader.getThird()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Balance : " + mHeader.getFour()));
        anchor.add(new Paragraph("\n"));
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 5:
        anchor.add(new Paragraph("Last 12 Month Transaction Report : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 6:
        anchor.add(new Paragraph("Reminders As On " + dateFormatSystem.format(Long.valueOf(Long.parseLong(mHeader.getSecond()))) + " : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 7:
        anchor.add(new Paragraph("All Reminder List : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 8:
        anchor.add(new Paragraph("Category wise Balance List : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 9:
        anchor.add(new Paragraph("All Account List : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 10:
        anchor.add(new Paragraph("All Transactions List : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 11:
        anchor.add(new Paragraph("Last Transaction Report : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 13:
        anchor.add(new Paragraph("All Account List : ", smallBold));
        createTable(subCatPart);
        document.add(anchor);
        document.add(subCatPart);
        return;
      case 14:
        anchor.add(new Paragraph("Account at a Glance Report : ", smallBold));
        createTable(subCatPart);
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Total Credit : " + mHeader.getSecond()));
        anchor.add(new Paragraph("\n"));
        anchor.add(new Paragraph("Total Debit : " + mHeader.getThird()));
        anchor.add(new Paragraph("\n"));
        document.add(anchor);
        document.add(subCatPart);
        return;
      default:
        return;
    }
  }

  private static void createTable(Section subCatPart) throws BadElementException {
    int i;
    int len = mColumns.size();
    PdfPTable table = new PdfPTable(len);
    for (i = 0; i < len; i++) {
      PdfPCell c1 = new PdfPCell(new Phrase((String) mColumns.get(i)));
      c1.setHorizontalAlignment(1);
      table.addCell(c1);
    }
    table.setHeaderRows(1);
    int j = 1;
    for (PdfDao dao : mValues) {
      for (i = 0; i < len; i++) {
        switch (i) {
          case 0:
            table.addCell("" + dao.getFirst());
            break;
          case 1:
            table.addCell("" + dao.getSecond());
            break;
          case 2:
            table.addCell("" + dao.getThird());
            break;
          case 3:
            table.addCell("" + dao.getFour());
            break;
          case 4:
            table.addCell("" + dao.getFive());
            break;
          case 5:
            table.addCell("" + dao.getSix());
            break;
          default:
            break;
        }
      }
      j++;
    }
    subCatPart.add(table);
  }

  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }
}
