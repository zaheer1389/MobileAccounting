package com.adslinfotech.mobileaccounting.rest;

import android.util.Log;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.Retrofit.Builder;

public class ApiClient {
  public static final String BASE_URL_ADSL = "http://www.adslinfotech.com";
  public static final String BASE_URL_VISION = "";
  private static Retrofit retrofit = null;

  public static class LoggingInterceptor implements Interceptor {
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      long t1 = System.nanoTime();
      String requestLog = String.format("Sending request %s on %s%n%s", new Object[]{request.url(), chain.connection(), request.headers()});
      if (request.method().compareToIgnoreCase("post") == 0) {
        requestLog = "\n" + requestLog + "\n" + ApiClient.bodyToString(request);
      }
      Log.d("TAG", "request\n" + requestLog);
      Response response = chain.proceed(request);
      long t2 = System.nanoTime();
      String responseLog = String.format("Received response for %s in %.1fms%n%s", new Object[]{response.request().url(), Double.valueOf(((double) (t2 - t1)) / 1000000.0d), response.headers()});
      String bodyString = response.body().string();
      Log.d("TAG", "response\n" + responseLog + "\n" + bodyString);
      return response.newBuilder().body(ResponseBody.create(response.body().contentType(), bodyString)).build();
    }
  }

  public static Retrofit getClient(String URL) {
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(5, TimeUnit.MINUTES);
    client.setReadTimeout(5, TimeUnit.MINUTES);
    retrofit = new Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
    return retrofit;
  }

  public static String bodyToString(Request request) {
    try {
      Request copy = request.newBuilder().build();
      Buffer buffer = new Buffer();
      copy.body().writeTo(buffer);
      return buffer.readUtf8();
    } catch (IOException e) {
      return "did not work";
    }
  }
}
