package com.adslinfotech.mobileaccounting.activities.invoice;

import android.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ConvertHTMLToPDF
{
  public static void createPDF(String paramString1, String paramString2)
  {
    Log.e("createPDF", "pdfFilename: " + paramString1);
    Document localDocument = new Document();
    PdfWriter pdfWriter = null;
    try
    {
      pdfWriter = PdfWriter.getInstance(localDocument, new FileOutputStream(paramString1));
      localDocument.addAuthor("betterThanZero");
      localDocument.addCreationDate();
      localDocument.addProducer();
      localDocument.addCreator("MySampleCode.com");
      localDocument.addTitle("Demo for iText XMLWorker");
      localDocument.setPageSize(PageSize.LETTER);
      localDocument.open();
      new InputStreamReader(new URL("http://demo.mysamplecode.com/").openStream());
      //XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, localDocument, new FileInputStream(paramString2), Charset.forName("UTF-8"));
      XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, localDocument, new FileInputStream(paramString2));
      localDocument.close();
      pdfWriter.close();
      return;
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
      return;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return;
    }
    catch (DocumentException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String paramString1, String paramString2)
  {
    new ConvertHTMLToPDF();
    createPDF(paramString1, paramString2);
  }
}


/* Location:              /home/zaheer/Desktop/Zaheer/Reverse Engg/classes-dex2jar.jar!/com/adslinfotech/mobileaccounting/activities/invoice/ConvertHTMLToPDF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */