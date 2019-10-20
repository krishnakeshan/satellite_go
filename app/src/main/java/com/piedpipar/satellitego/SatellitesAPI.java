package com.piedpipar.satellitego;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SatellitesAPI {
    //Properties
    String apiKey = "RA9PPK-P8DALG-ANBNDN-47UU";
//    int[] satelliteIds = {25544, 27424, 29155, 28376, 25682};
    int[] satelliteIds = {25544};

    //Methods
    //Method to get satellite data for a given satellite
    @GET("positions/{satId}/{userLat}/{userLng}/{userAltitude}/{seconds}/?apiKey=" + apiKey)
    Call<ResponseBody> getSatelliteData(@Path("satId") int satId, @Path("userLat") double userLat, @Path("userLng") double userLng, @Path("userAltitude") double userAlt, @Path("seconds") int seconds);

    //Method to get visual pass data for a given satellite
    @GET("visualpasses/{satId}/{userLat}/{userLng}/{userAlt}/{days}/{minVisibility}?apiKey=" + apiKey)
    Call<ResponseBody> getSatelliteVisualPasses(@Path("satId") int satId, @Path("userLat") double userLat, @Path("userLng") double userLng, @Path("userAlt") double userAlt, @Path("days") int days, @Path("minVisibility") int minVisibility);
}
