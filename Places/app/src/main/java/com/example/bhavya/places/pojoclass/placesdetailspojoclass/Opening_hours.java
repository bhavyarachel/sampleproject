package com.example.bhavya.places.pojoclass.placesdetailspojoclass;

import java.io.Serializable;

/**
 * Created by bhavya on 30/8/16.
 */

public class Opening_hours implements Serializable
{

    private String open_now;

    private String[] weekday_text;


    public String getOpen_now ()
    {
        return open_now;
    }

    public void setOpen_now (String open_now)
    {
        this.open_now = open_now;
    }

    public String[] getWeekday_text ()
    {
        return weekday_text;
    }

    public void setWeekday_text (String[] weekday_text)
    {
        this.weekday_text = weekday_text;
    }


}