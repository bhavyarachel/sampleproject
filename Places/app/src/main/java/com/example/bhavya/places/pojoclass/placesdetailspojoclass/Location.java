package com.example.bhavya.places.pojoclass.placesdetailspojoclass;

import java.io.Serializable;

/**
 * Created by bhavya on 30/8/16.
 */

public class Location implements Serializable{

    private Double lat;

    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
