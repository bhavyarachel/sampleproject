package com.example.bhavya.places.retrofit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bhavya on 20/9/16.
 */

public class Findserviceretrofit {

    public static final String BASE_URL ="https://maps.googleapis.com/maps/api/place/" ;
    private static Retrofit retrofit = null;


    public Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}







