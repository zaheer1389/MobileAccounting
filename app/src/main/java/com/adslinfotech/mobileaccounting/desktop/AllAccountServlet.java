package com.adslinfotech.mobileaccounting.desktop;

import android.content.Context;
import com.adslinfotech.mobileaccounting.dao.Account;
import com.adslinfotech.mobileaccounting.database.FetchData;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AllAccountServlet extends HttpServlet {
  private static final long serialVersionUID = -621262173529602L;
  private FetchData mFetchData;

  public AllAccountServlet(Context context) {
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    this.mFetchData = new FetchData();
    ArrayList<Account> list = this.mFetchData.getAllAccounts(false, false);
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html><html lang='en' class='no-js'>");
    sb.append("<head><title>Simple Accounting - All Account List</title>");
    sb.append("<meta charset='UTF-8' /><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    sb.append("<style>" + getStyle() + "</style>");
    sb.append("<style media='print'>header{display: none;} td, th { padding: 3px 10px; border: solid 1px #ddd; }</style>");
    sb.append("</head><body> <div class='container'>");
    sb.append("<header><h1>Simple Accounting <em> by <a href='http://bit.ly/1TqOgDy' target='_blank'>ADSL Infotech</a></em> <span>Manage your Accounting easily.</span></h1>");
    sb.append("<nav class='codrops-demos'><a href='mailto:adslinfosoft99@gmail.com' title='Email us'>Contact</a><a href='javascript:window.print();' title='Print'>Print</a></nav>");
    sb.append("</header><div class='component'><h2>Simple Accounting - All Account List <span style='font-weight: normal;font-style: italic;'> by ADSL Infotech</span></h2>");
    sb.append("<p>Displaying all the requested data from the mobile storage.<a href='http://bit.ly/1TEjbYt' title='Follow us on Facebook' target='_blank'> Follow us on Facebook</a>.</p>");
    sb.append("<table>\n");
    sb.append("<th>SNo</th><th>Name</th><th>Email.</th><th>Mobile</th><th>Category</th>");
    int i = 1;
    Iterator it = list.iterator();
    while (it.hasNext()) {
      Account dao = (Account) it.next();
      sb.append("<tr>\n");
      int i2 = i + 1;
      sb.append("<td>" + i + "</td><td>" + dao.getName() + "</td><td class='left'>" + dao.getEmail() + "</td><td class='left'>" + dao.getMobile() + "</td><td>" + dao.getCategory() + "</td>");
      sb.append("</tr>\n");
      i = i2;
    }
    resp.setContentType("text/plain");
    resp.setStatus(200);
    resp.getWriter().println(sb.toString());
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/plain");
    resp.setStatus(200);
    PrintWriter out = resp.getWriter();
    String username = req.getParameter("user_name");
    if (username == null || username.equals("")) {
      username = "<NO_NAME_SPECIFIED>";
    }
    out.println("<html><title>Welcome</title><body>Welcome " + username + "</body></html>");
  }

  private String getStyle() {
    return "@import url(http://fonts.googleapis.com/css?family=Lato:300,400,700);.codrops-demos a,.codrops-top a,a{text-decoration:none}@font-face{font-family:codropsicons;src:url(../fonts/codropsicons/codropsicons.eot);src:url(../fonts/codropsicons/codropsicons.eot?#iefix) format('embedded-opentype'),url(../fonts/codropsicons/codropsicons.woff) format('woff'),url(../fonts/codropsicons/codropsicons.ttf) format('truetype'),url(../fonts/codropsicons/codropsicons.svg#codropsicons) format('svg');font-weight:400;font-style:normal}*,:after,:before{-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box}body{font-family:Lato,Arial,sans-serif;color:#FFFFFF;background:#34374a}a{color:#3F9CD6}a:focus,a:hover{color:#FFFFFF}.container>header{margin:0 auto;padding:2em;text-align:center;background:rgba(0,0,0,.01)}.container>header h1{font-size:2.625em;line-height:1.3;margin:0;font-weight:300}.container>header span{display:block;font-size:60%;opacity:.7;padding:0 0 .6em .1em}.codrops-top{background:#fff;background:rgba(255,255,255,.6);text-transform:uppercase;width:100%;font-size:.69em;line-height:2.2}.codrops-top a{padding:0 1em;letter-spacing:.1em;display:inline-block}.codrops-top a:hover{background:rgba(255,255,255,.95)}.codrops-top span.right{float:right}.codrops-top span.right a{float:left;display:block}.codrops-icon:before{font-family:codropsicons;margin:0 4px;speak:none;font-style:normal;font-weight:400;font-variant:normal;text-transform:none;line-height:1;-webkit-font-smoothing:antialiased}.codrops-icon-drop:before{content:'\\e001'}.codrops-icon-prev:before{content:'\\e004'}.codrops-demos{padding-top:1em;font-size:.8em}.codrops-demos a{display:inline-block;margin:.5em;padding:.7em 1.1em;outline:0;border:2px solid #3F9CD6;text-transform:uppercase;letter-spacing:1px;font-weight:700}.codrops-demos a.current-demo,.codrops-demos a.current-demo:hover,.codrops-demos a:hover{border-color:#7c8d87;color:#7c8d87}.related{text-align:center;font-size:1.5em;padding-bottom:3em}@media screen and (max-width:25em){.codrops-icon span{display:none}}@font-face{font-family:Blokk;src:url(../fonts/blokk/BLOKKRegular.eot);src:url(../fonts/blokk/BLOKKRegular.eot?#iefix) format('embedded-opentype'),url(../fonts/blokk/BLOKKRegular.woff) format('woff'),url(../fonts/blokk/BLOKKRegular.svg#BLOKKRegular) format('svg');font-weight:400;font-style:normal}.component{line-height:1.5em;margin:0 auto;padding:2em 0 3em;width:90%;max-width:1000px;overflow:hidden}.component .filler{font-family:Blokk,Arial,sans-serif;color:#d3d3d3}td.err,th{color:#fff}table{border-collapse:collapse;margin-bottom:3em;width:100%;background:#3b4259}td,th{padding:.75em 1.5em;text-align:left;border: 1px solid #eee;}td.right{text-align:right!important;}td.err{background-color:#e992b9;font-size:.75em;text-align:center;line-height:1}th{background-color:#3F9CD6;font-weight:700;white-space:nowrap}tbody th{background-color:#236087}tbody tr:nth-child(2n-1){background-color:#3b4259;transition:all .125s ease-in-out}tbody tr:hover{background-color:rgba(129,208,177,.3)}.sticky-wrap{overflow-x:auto;overflow-y:hidden;position:relative;margin:3em 0;width:100%}.sticky-wrap .sticky-col,.sticky-wrap .sticky-intersect,.sticky-wrap .sticky-thead{opacity:0;position:absolute;top:0;left:0;transition:all .125s ease-in-out;z-index:50;width:auto}.sticky-wrap .sticky-thead{box-shadow:0 .25em .1em -.1em rgba(0,0,0,.125);z-index:100;width:100%}.sticky-wrap .sticky-intersect{opacity:1;z-index:150}.sticky-wrap .sticky-intersect th{background-color:#666;color:#eee}.sticky-wrap td,.sticky-wrap th{box-sizing:border-box}td.user-name{text-transform:capitalize}.sticky-wrap.overflow-y{overflow-y:auto;max-height:50vh}";
  }
}
