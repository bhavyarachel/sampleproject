package com.example.bhavya.places.ui.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Geometry;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.OpeningHours;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Results;
import com.example.bhavya.places.ui.activity.DrawerActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Map fragment inflates map showing current location of the user and nearby restaurants.
 *
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private Bitmap smallMarker;
    private String json;
    private LatLng position;
    private ImageView placeImage;
    private Bitmap bitmap;
    private LruCache<String, Bitmap> mMemoryCache;
    private LatLng latLng;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Broadcast Receiver for getting location updates of user's current location
     */
    BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        WeakReference<MapFragment> fragmentWeakReference = new WeakReference(MapFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                MapFragment mapFragment = fragmentWeakReference.get();
                mapFragment.updateLocation();
            }
        }
    };

    /**
     * Broadcast Receiver for getting nearby places update
     */
    BroadcastReceiver placesUpdateReceiver = new BroadcastReceiver() {
        WeakReference<MapFragment> fragmentWeakReference = new WeakReference(MapFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                json = intent.getStringExtra(getResources().getString(R.string.json));
                MapFragment mapFragment = fragmentWeakReference.get();
                mapFragment.updatePlaces();
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromDrawerActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(locationUpdateReceiver, new IntentFilter(getResources()
                .getString(R.string.locationupdate)));
        getActivity().registerReceiver(placesUpdateReceiver, new IntentFilter(getResources()
                .getString(R.string.placesupdate)));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(locationUpdateReceiver);
        getActivity().unregisterReceiver(placesUpdateReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        updateLocation();
        if (json.length() > 0)
            updatePlaces();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
        placeImage = (ImageView) v.findViewById(R.id.image_place);
        TextView textviewName = (TextView) v.findViewById(R.id.textview_title);
        TextView textviewAddress = (TextView) v.findViewById(R.id.textview_address);
        TextView textviewOpennow = (TextView) v.findViewById(R.id.opennow);
        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.rating);

        try {

            Gson gson = new Gson();
            NearbyPlacesPojoClass nearbyPlacesObject = gson.fromJson(json,
                    NearbyPlacesPojoClass.class);
            Results[] resultArray = nearbyPlacesObject.getResults();
            for (int i = 0; i < resultArray.length; i++) {
                if (i == Integer.parseInt(marker.getSnippet())) {

                    textviewName.setText(resultArray[i].getName());

                    textviewAddress.setText(resultArray[i].getVicinity());

                    OpeningHours openingHours = resultArray[i].getOpening_hours();
                    if (openingHours != null) {
                        if(openingHours.getOpen_now().equals(getContext().getString(R.string
                                .status_opennow))){
                            textviewOpennow.setText(getContext().getString(R.string.status).concat
                                    (getContext().getString(R.string
                                            .open)));
                        }else{
                            textviewOpennow.setText(getContext().getString(R.string.status).concat
                                    (getContext().getString(R.string
                                            .closed)));
                        }
                    } else {
                        textviewOpennow.setText(getContext().getString(R.string.status).concat
                                (getContext().getString(R.string
                                        .statusUnavailable)));
                    }

                    float numStars = resultArray[i].getRating();
                    ratingBar.setRating(numStars);

                    try {
                        loadBitmap(resultArray[i].getIcon());
                    } catch (Exception e) {
                        Log.i(getString(R.string.urlerror), getString(R.string.error));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;

    }

    /**
     * Updates the current location of the user
     */
    void updateLocation() {
        DrawerActivity drawerActivity = (DrawerActivity) getActivity();
        LatLng latLng = drawerActivity.getCurrentLatLong();
        if (latLng != null) {
            this.updateMap(latLng);
        }
    }

    /**
     * Update the map with current latitude and longitude
     */
    public void updateMap(LatLng latLng) {
        MarkerOptions currentLocMarker = new MarkerOptions();
        currentLocMarker.position(latLng);
        currentLocMarker.title(getString(R.string.current_position));
        currentLocMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                .HUE_RED));
        mCurrLocationMarker = mMap.addMarker(currentLocMarker);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void getDataFromDrawerActivity(){
        latLng = ((DrawerActivity)getActivity()).getCurrentLatLong();
        json = ((DrawerActivity)getActivity()).getAPIResult();
    }

    /**
     * Updates the nearby places and adds marker on each location based on the current location
     * of the user and shows info window
     */
    void updatePlaces() {
        try {
            Gson gson = new Gson();
            NearbyPlacesPojoClass nearbyPlacesObject = gson.fromJson(json,
                    NearbyPlacesPojoClass.class);
            Results[] resultArray = nearbyPlacesObject.getResults();
            Geometry geometry;
            mMap.setInfoWindowAdapter(this);
            for (int i = 0; i < resultArray.length; i++) {
                geometry = resultArray[i].getGeometry();
                Double lat = geometry.getLocation().getLat();
                Double lon = geometry.getLocation().getLng();
                position = new LatLng(lat, lon);

                createMarkerBitmap();
                mMap.addMarker(new MarkerOptions().position(position)
                        .snippet(i + "")
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .title(resultArray[i].getName()))
                        .showInfoWindow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * Adds image url and image into cache
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
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
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Loads the bitmap from cache.
     * if bitmap is not cached, it displays a default image- image_profile else it displays
     * restaurant photo parsed from details api
     *
     * @param url
     */
    private void loadBitmap(String url) {
        final String imageKey = String.valueOf(url);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            placeImage.setImageBitmap(bitmap);
        } else {
            placeImage.setImageResource(R.drawable.ic_launcher);
            new GetIcon().execute(url);

        }
    }

    /**
     * To scale the ic_nearbyplaces_marker to be used as marker in google maps
     */
    private void createMarkerBitmap() {
        int height = 32;
        int width = 32;
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.ic_nearbyplaces_marker);
        smallMarker = Bitmap.createScaledBitmap(icon, width, height, false);
    }

    /**
     * To load the icons of the restaurants from URL
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
                placeImage.setImageBitmap(image);
            }
        }
    }
}





