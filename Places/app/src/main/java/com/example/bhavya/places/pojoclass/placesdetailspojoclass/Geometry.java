package com.example.bhavya.places.pojoclass.placesdetailspojoclass;

import java.io.Serializable;

/**
 * Created by bhavya on 30/8/16.
 */

public class Geometry implements Serializable
{

    private Location location;

    public Location getLocation ()
    {
        return location;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

}
