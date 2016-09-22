package com.example.bhavya.places.interfaces;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
/**
 * Created by bhavya on 20/9/16.
 */

public interface FindServiceAPIInterface {

    @GET("nearbysearch/json")
    Call<NearbyPlacesPojoClass> getNearbyPlaces(@Query("location") String latlng, @Query("radius")
            String radius, @Query("type") String type, @Query("name") String name, @Query("key")
            String apiKey);

    @GET("details/json")
    Call<NearbyPlacesPojoClass> getPlaceDetails(@Query("placeid") String placeid, @Query
            ("api_key") String apiKey);


}








