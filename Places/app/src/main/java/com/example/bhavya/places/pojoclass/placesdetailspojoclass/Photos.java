package com.example.bhavya.places.pojoclass.placesdetailspojoclass;

import java.io.Serializable;

/**
 * Created by bhavya on 30/8/16.
 */

public class Photos implements Serializable
{
    private String photo_reference;

    private Integer height;

    private String[] html_attributions;

    private String width;

    public String getPhoto_reference ()
    {
        return photo_reference;
    }

    public void setPhoto_reference (String photo_reference)
    {
        this.photo_reference = photo_reference;
    }

    public Integer getHeight ()
    {
        return height;
    }

    public void setHeight (Integer height)
    {
        this.height = height;
    }

    public String[] getHtml_attributions ()
    {
        return html_attributions;
    }

    public void setHtml_attributions (String[] html_attributions)
    {
        this.html_attributions = html_attributions;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

}