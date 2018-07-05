package com.adslinfotech.mobileaccounting.desktop;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.text.format.Formatter;
import android.util.Log;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.itextpdf.text.pdf.PdfBoolean;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class MyHttpServer {
  Context context;
  ServletContextHandler handler = new ServletContextHandler();
  private String mCategory;
  private int mSelectType;
  Server server = new Server(7777);

  class C03971 extends Thread {
    C03971() {
    }

    public void run() {
      try {
        MyHttpServer.this.server.start();
      } catch (Exception e) {
        Log.e("SERVER_START", e.toString());
      }
    }
  }

  static {
    if (VERSION.SDK_INT < 11) {
      System.setProperty("java.net.preferIPv6Addresses", PdfBoolean.FALSE);
    }
  }

  public MyHttpServer(Context paramContext, int type) {
    this.mSelectType = type;
    this.context = paramContext;
    this.handler.setContextPath("/");
    if (this.mSelectType == 3) {
      this.handler.addServlet(new ServletHolder(new AllAccountServlet(paramContext)), "/");
    } else if (this.mSelectType == 4) {
      this.handler.addServlet(new ServletHolder(new AccountAtGlanceServlet()), "/");
    }
    this.server.setHandler(this.handler);
  }

  public MyHttpServer(Context paramContext, String category) {
    this.mCategory = category;
    this.context = paramContext;
    this.handler.setContextPath("/");
    this.handler.addServlet(new ServletHolder(new OverAllLedgerServlet(paramContext, this.mCategory)), "/");
    this.server.setHandler(this.handler);
  }

  public MyHttpServer(Context paramContext, Account dao) {
    Account mDao = dao;
    this.context = paramContext;
    this.handler.setContextPath("/");
    this.handler.addServlet(new ServletHolder(new AccountLedgerServlet(paramContext, mDao)), "/");
    this.server.setHandler(this.handler);
  }

  public String getIPAddress() {
    if (((ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1).isConnected()) {
      return "http://" + Formatter.formatIpAddress(((WifiManager) this.context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress()) + ":7777";
    }
    return "You are not connected to WIFI";
  }

  public void start() {
    new C03971().start();
  }

  public void stop() {
    try {
      this.server.stop();
    } catch (Exception e) {
      Log.e("SERVER_STOP", e.toString());
    }
  }
}
