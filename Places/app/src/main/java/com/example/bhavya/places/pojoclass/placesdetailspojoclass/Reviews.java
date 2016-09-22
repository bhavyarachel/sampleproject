package com.example.bhavya.places.pojoclass.placesdetailspojoclass;

import java.io.Serializable;

/**
 * Created by bhavya on 30/8/16.
 */

public class Reviews implements Serializable
{
    private static final long serialVersionUID =-3203837548359793413L;

    private String time;

    private String text;

    private String profile_photo_url;

    private String author_url;

    private String author_name;


    private String rating;

    private String language;

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getProfile_photo_url ()
    {
        return profile_photo_url;
    }

    public void setProfile_photo_url (String profile_photo_url)
    {
        this.profile_photo_url = profile_photo_url;
    }

    public String getAuthor_url ()
    {
        return author_url;
    }

    public void setAuthor_url (String author_url)
    {
        this.author_url = author_url;
    }

    public String getAuthor_name ()
    {
        return author_name;
    }

    public void setAuthor_name (String author_name)
    {
        this.author_name = author_name;
    }


    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public String getLanguage ()
    {
        return language;
    }

    public void setLanguage (String language)
    {
        this.language = language;
    }

}