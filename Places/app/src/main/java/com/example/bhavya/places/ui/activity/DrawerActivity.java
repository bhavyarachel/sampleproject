package com.example.bhavya.places.ui.activity;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.interfaces.FindServiceAPIInterface;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Results;
import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.retrofit.Findserviceretrofit;
import com.example.bhavya.places.ui.adapter.ListItemsAdapter;
import com.example.bhavya.places.ui.adapter.ViewPagerAdapter;
import com.example.bhavya.places.ui.fragments.FavouritePlacesFragment;
import com.example.bhavya.places.ui.fragments.FindAServiceFragment;
import com.example.bhavya.places.ui.fragments.MyProfileFragment;
import com.example.bhavya.places.ui.fragments.NearByPlaceswithoutViewPagerFragment;
import com.example.bhavya.places.ui.fragments.NearbyPlaceswithViewPagerFragment;
import com.example.bhavya.places.utils.RealPathUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_LOGIN;


/**
 * Created by bhavya on 9/8/16.
 * <p>
 * This is the Home Page. It contains navigation drawer and tabs.
 * Tab Layout contains- Map,Nearby Places, Grid.
 * Nearby places is set as default page when the tab is loaded.
 * Drawer Activity contains- Nearby places, Favourite places, Find a service, Profile, Logout
 */

public class DrawerActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public StringBuilder strURL;
    public Double latitude;
    public Double longitude;
    private Toolbar mToolbar;
    private TextView mUsername;
    private TextView mUserEmail;
    private CircleImageView mProfilepic;
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private FragmentTransaction mFragmentTransaction;
    private NavigationView mNavigationView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private LatLng latLng;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ProgressDialog pDialog;
    private StringBuilder json = new StringBuilder();
    private String CurrentRadius;
    private int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        setCustomActionBar();
        setViewProperties();
        loadNearbyPlaceswithViewPagerFragment();
        getDataFromPreferences();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerlayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }


    /**
     * Sync the toggle state after onRestoreInstanceState has occurred.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        buildGoogleApiClient();
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = latLng.latitude;
        longitude = latLng.longitude;

        createUrl();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        broadcastLocationUpdate();
        try {
            String myURL = strURL.toString();
            new GetPlacesData().execute(myURL);
        } catch (Exception e) {
            Log.i(getString(R.string.urlerror), getString(R.string.error));
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(DrawerActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == Activity.RESULT_OK && data != null){
            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

            Uri uriFromPath = Uri.fromFile(new File(realPath));
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mProfilepic.setImageBitmap(bitmap);

            saveImageURI(realPath);
        }
    }

    /**
     * Saves the image path into shared preference in user email key from loading it again once
     * the user has logged out
     * @param imagepath
     */
    private void saveImageURI(String imagepath){
        //to get the current user email
        SharedPreferences sharedpreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String email= sharedpreferences.getString(PREF_LOGIN,"");

        // to update the shared preference
        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
        String jsonstring = sharedpreferences.getString(email,"");
        Gson gson = new Gson();
        UserDetails newUser = gson.fromJson(jsonstring,UserDetails.class);
        newUser.setImageURI(imagepath);
        jsonstring = gson.toJson(newUser);
        prefsEditor.putString(email, jsonstring);
        prefsEditor.commit();
    }

    /**
     * Gets the current latitude and longitude of the user     *
     * @return latlng object
     */
    public LatLng getCurrentLatLong() {
        return latLng;
    }

    public String getAPIResult(){
        String apiResult = "";
        if (json != null && json.toString() != null ) {
            apiResult = json.toString();
        }

        return apiResult;
    }

    /**
     * to build google api client
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(DrawerActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    View.OnClickListener profilePicOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType(getString(R.string.set_image_type));
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)),
                    PICK_IMAGE_REQUEST);
        }
    };

    /**
     * Loads the near by places fragment having view pager to show the listview, gridview and map
     */
    private void loadNearbyPlaceswithViewPagerFragment() {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.mainContainer, new NearbyPlaceswithViewPagerFragment());
        mFragmentTransaction.commit();
    }

    /**
     * Customizing toolbar with no title
     */
    private void setCustomActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // to refer the toolbar
        setSupportActionBar(mToolbar);
        //to eliminate the app name from the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
    }

    /**
     * Initializing view properties
     */
    private void setViewProperties() {
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        View headerview = mNavigationView.inflateHeaderView(R.layout.nav_header);
        mUsername = (TextView) headerview.findViewById(R.id.textview_username);
        mUserEmail = (TextView) headerview.findViewById(R.id.textview_useremail);
        mProfilepic = (CircleImageView) headerview.findViewById(R.id.profile_image);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerlayout, mToolbar, R.string
                .drawerOpen, R.string.drawerClose);
        mDrawerlayout.addDrawerListener(mActionBarDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
        mProfilepic.setOnClickListener(profilePicOnClick);

    }

    /**
     * Sets the PREF_LOGIN as null to show that the user has logged out
     */
    private void deleteUserFromSharedPreference() {
        mSharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.putString(PREF_LOGIN, null);
        editor.commit();
    }


    /**
     * To get the name and email id of the user to be displayed on the header layout of nav drawer
     */
    private void getDataFromPreferences() {
        mSharedPreferences = this.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String email = mSharedPreferences.getString(PREF_LOGIN, "rerrew");

        String obj = mSharedPreferences.getString(email, "");
        Gson gson = new Gson();
        UserDetails newuser = gson.fromJson(obj, UserDetails.class);
        String Firstname = newuser.getFirstname();
        String Useremail = newuser.getEmail();
        CurrentRadius = newuser.getRadius();
        String imageURI = newuser.getImageURI();

        mUsername.setText(Firstname);
        mUserEmail.setText(Useremail);
        if(imageURI!=""){
            mProfilepic.setImageURI(Uri.parse(imageURI));
        }
    }

    /**
     * Create a new fragment and specify the fragment to show based on nav item clicked
     */
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.navNearbyPlacesFragment:
                fragmentClass = NearbyPlaceswithViewPagerFragment.class;
                break;
            case R.id.navFavoritePlacesFragment:
                fragmentClass = FavouritePlacesFragment.class;
                break;
            case R.id.navFindAServiceFragment:
                fragmentClass = FindAServiceFragment.class;
                break;
            case R.id.navMyProfileFragment:
                fragmentClass = MyProfileFragment.class;
                break;
            case R.id.nav_LogoutFragemnt:
                deleteUserFromSharedPreference();
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
                finish();
                return;
            default:
                fragmentClass = NearbyPlaceswithViewPagerFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment previousFragment = fragmentManager.findFragmentById(R.id.mainContainer);

        if (previousFragment == null || !previousFragment.getClass().equals(fragmentClass)) {
            if (fragment != null)
                fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment).commit();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        mDrawerlayout.closeDrawers();
    }

    /**
     * Create URL to make Places API call in nearbyplaceswithoutviewpager Fragment
     */
    private void createUrl() {
        strURL = new StringBuilder(getString(R.string.initial_api));
        strURL.append(getString(R.string.setlocation)).append(latitude).append(getString(R.string
                .comma)).append(longitude).append(getString(R.string.and))
                .append(getString(R.string.radius)).append(CurrentRadius).append
                (getString(R.string.and)).append(getString(R.string.type)).append(getString(R.string
                .restaurant)).append(getString(R.string.and)).append(getString(R.string.key))
                .append(getString(R.string.APIkey));

        StringBuilder s = strURL;
        Log.i(getString(R.string.log_api), strURL.toString());
    }

    /**
     * Broadcast update in user's location
     */
    void broadcastLocationUpdate() {
        Intent locationUpdate = new Intent(getResources().getString(R.string.locationupdate));
        sendBroadcast(locationUpdate);
    }

    /**
     * Broadcasts update for nearby places
     */
    void broadcastPlacesUpdate() {
        Intent placesUpdate = new Intent(getResources().getString(R.string.placesupdate));
        placesUpdate.putExtra(getResources().getString(R.string.json), json.toString());
        sendBroadcast(placesUpdate);
    }

    /**
     * To parse Places API for parsing data to child fragments
     */
    class GetPlacesData extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DrawerActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_map));
            pDialog.show();

        }
        protected String doInBackground(String[] urls) {
            try {
                String s = urls[0];
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        json.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String s1 = json.toString();
                    return json.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e(getResources().getString(R.string.error), e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
            if (response == null) {
                response = getResources().getString(R.string.error);
                Log.i(getString(R.string.log_info), response);
                return;
            }
            broadcastPlacesUpdate();
            pDialog.dismiss();
        }
    }
}
