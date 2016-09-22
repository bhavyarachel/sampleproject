package com.example.bhavya.places.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.OpeningHours;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Results;
import com.example.bhavya.places.ui.activity.DetailsActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhavya on 20/9/16.
 *
 * List view adapter to show the service result as list view to show the nearby places based on
 * the place type and place name enetered by the user in Find a service fragment.
 */

public class ServiceResultAdapter  extends ArrayAdapter<Results> {

    @BindView(R.id.textview_title)
    TextView mPlaceName;
    @BindView(R.id.textview_address)
    TextView mPlaceAddress;
    @BindView(R.id.opennow)
    TextView mPlaceStatus;
    @BindView(R.id.rating)
    RatingBar mRating;
    @BindView(R.id.image_place)
    ImageView mPlaceImage;

    private ArrayList<Results> list;
    private Double latitude;
    private Double longitude;
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap bitmap;
    private Results[] places;
    private Context mContext;


    public ServiceResultAdapter(Context context, int resource, ArrayList<Results> list, Results[]
            places, double latitude, double longitude) {
        super(context, resource,list);
        mContext = context;
        this.list = list;
        this.latitude = latitude;
        this.longitude = longitude;
        this.places = places;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.custom_info_window, null);
        }

        view.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String placeid;

                    Results[] resultArray = places;
                    placeid = resultArray[position].getPlaceID();

                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getContext().getString(R.string.place_id), placeid);
                    bundle.putString(getContext().getString(R.string.latitude), latitude.toString());
                    bundle.putString(getContext().getString(R.string.longitude), longitude.toString());
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Results results = list.get(position);
        if (results != null) {
            ButterKnife.bind(this,view);
            if (mPlaceName != null) {
                mPlaceName.setText(results.getName());
            }

            if (mPlaceAddress != null) {
                mPlaceAddress.setText(results.getVicinity());
            }

            OpeningHours openingHours = results.getOpening_hours();
            if (openingHours != null) {

                if (openingHours.getOpen_now().equals(getContext().getString(R.string.status_opennow))){
                    mPlaceStatus.setText(getContext().getString(R.string.status).concat
                            (getContext().getString(R.string
                                    .open)));
                } else {
                    mPlaceStatus.setText(getContext().getString(R.string.status).concat
                            (getContext().getString(R.string
                                    .closed)));
                }
            } else {
                mPlaceStatus.setText(getContext().getString(R.string.status).concat
                        (getContext().getString(R.string
                                .statusUnavailable)));
            }

            if (mRating != null) {
                results.getRating();
                float numStars = results.getRating();
                mRating.setRating(numStars);
            }

            try {
                loadBitmap(results.getIcon());
            } catch (Exception e) {
                Log.i(getContext().getString(R.string.urlerror), getContext().getString(R.string
                        .error));
            }

        }

        return view;
    }


    @Override
    public Results getItem(int position) {
        return super.getItem(position);

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Adds image url and image into cache
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Gets the stored btimap from cache
     *
     * @param key
     * @return Bitmap image
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Loads the bitmap from cache.
     * if bitmap is not cached, it displays a default image- image_profile else it displays
     * restaurant photo parsed from details api
     *
     * @param url
     */
    public void loadBitmap(String url) {
        final String imageKey = String.valueOf(url);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            mPlaceImage.setImageBitmap(bitmap);
        } else {
            mPlaceImage.setImageResource(R.drawable.ic_launcher);
            new GetIcon().execute(url);
        }
    }

    /**
     * To parse Places API to get the icon of all restaurants appearing in the listview
     */
    private class GetIcon extends AsyncTask<String, String, Bitmap> {
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                addBitmapToMemoryCache(String.valueOf(args[0]), bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                mPlaceImage.setImageBitmap(image);
            }
        }

    }


}
