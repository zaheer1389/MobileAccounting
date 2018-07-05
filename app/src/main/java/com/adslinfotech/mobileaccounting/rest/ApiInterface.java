package com.adslinfotech.mobileaccounting.rest;

import com.adslinfotech.mobileaccounting.dao.UpgradeResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ApiInterface {
  @GET("/androidapi/youtubelink.php")
  Call<JsonArray> getVideoLink();

  @Headers({"Content-type: application/json", "Accept: */*"})
  @POST("/Customers.svc/RegisterADSL")
  Call<JsonObject> registerGSM(@Body JsonObject jsonObject);

  @Headers({"Content-type: application/json", "Accept: */*"})
  @POST("/Customers.svc/RegisterEasyAccounting")
  Call<JsonObject> registerUser(@Body JsonObject jsonObject);

  @GET("/androidapi/pin_check.php")
  Call<UpgradeResponse> validatePin(@Query("email") String str, @Query("pinno") String str2, @Query("imei") String str3);
}
