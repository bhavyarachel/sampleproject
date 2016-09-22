package com.example.bhavya.places.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.Opening_hours;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.PlacesDetailsPojoClass;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhavya on 6/9/16.
 *
 * List view Adapter to show favorite places selected by the user
 */

public class FavPlacesListViewAdpater extends ArrayAdapter<LinkedHashMap<String,
        PlacesDetailsPojoClass>> {



    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap bitmap;
    private ViewHolder holder;
    private LinkedHashMap<String,PlacesDetailsPojoClass> mHashMap;



    public FavPlacesListViewAdpater(Context context, int resource, LinkedHashMap<String,
            PlacesDetailsPojoClass> hMap) {
        super(context, resource);
        this.mHashMap = hMap;

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

    static class ViewHolder {
        @BindView(R.id.textview_title)
        TextView mtextPlaceName;
        @BindView(R.id.textview_address)
        TextView mtextPlaceAddress;
        @BindView(R.id.opennow)
        TextView mtextPlaceStatus;
        @BindView(R.id.rating)
        RatingBar mRatingbar;
        @BindView(R.id.image_place)
        ImageView mPlaceImage;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return mHashMap.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_info_window,
                    parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        PlacesDetailsPojoClass item = (new ArrayList<PlacesDetailsPojoClass>(mHashMap.values()))
                .get(position);
        if (holder.mtextPlaceName != null) {
            holder.mtextPlaceName.setText(item.getResult().getName());
        }
        if (holder.mtextPlaceAddress != null) {
            holder.mtextPlaceAddress.setText(item.getResult().getFormatted_address());
        }

        Opening_hours openingHours = item.getResult().getOpening_hours();
        if (openingHours != null) {

            if (openingHours.getOpen_now().equals(getContext().getString(R.string.status_opennow))){
                holder.mtextPlaceStatus.setText(getContext().getString(R.string.status).concat
                        (getContext().getString(R.string
                                .open)));
            } else {
                holder.mtextPlaceStatus.setText(getContext().getString(R.string.status).concat
                        (getContext().getString(R.string
                                .closed)));
            }
        } else {
            holder.mtextPlaceStatus.setText(getContext().getString(R.string.status).concat
                    (getContext().getString(R.string
                            .statusUnavailable)));
        }

        if (holder.mRatingbar != null) {
            item.getResult().getRating();
            float numStars = Float.parseFloat(item.getResult().getRating());
            holder.mRatingbar.setRating(numStars);
        }

        try {
            loadBitmap(item.getResult().getIcon());
        } catch (Exception e) {
            Log.i(getContext().getString(R.string.urlerror), getContext().getResources().getString(R
                    .string.error));
        }


        return view;
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
            holder.mPlaceImage.setImageBitmap(bitmap);
        } else {
            holder.mPlaceImage.setImageResource(R.drawable.ic_launcher);
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
                holder.mPlaceImage.setImageBitmap(image);
            }
        }

    }
}