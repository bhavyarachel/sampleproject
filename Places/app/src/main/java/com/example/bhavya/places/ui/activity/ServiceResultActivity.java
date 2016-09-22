package com.example.bhavya.places.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.interfaces.FindServiceAPIInterface;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Results;
import com.example.bhavya.places.retrofit.Findserviceretrofit;
import com.example.bhavya.places.ui.adapter.ServiceResultAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bhavya on 20/9/16
 *
 * Shows the nearby places based on the name of the place the user types in the search box and
 * the type of place user chooses from the listview.
 * On clicking an item from the service result activity list view, more details of the place is
 * shown in details activity.
 */

public class ServiceResultActivity extends AppCompatActivity {

    @BindView(R.id.resultlistview)
    ListView findServiceListView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String mValueFromList;
    private String mSearchBoxValue;
    private String CurrentRadius;
    private String myKey;
    private String mylatlng;
    private Double latitude;
    private Double longitude;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_result);
        ButterKnife.bind(this);
        setCustomActionBar();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.getString(getString(R.string.fs_placetype)) != null) {
                mValueFromList = bundle.getString(getString(R.string.fs_placetype));
            }
            if (bundle.getString(getString(R.string.fs_placename)) != null) {
                mSearchBoxValue = bundle.getString(getString(R.string.fs_placename));
            }
            if (bundle.getString(getString(R.string.fs_Radius)) != null) {
                CurrentRadius = bundle.getString(getString(R.string.fs_Radius));
            }
            if (bundle.getString(getString(R.string.fs_LatLngString)) != null) {
                mylatlng = bundle.getString(getString(R.string.fs_LatLngString));
            }
            if (bundle.getString(getString(R.string.fs_APIKey)) != null) {
                myKey = bundle.getString(getString(R.string.fs_APIKey));
            }
            if (bundle.getString(getString(R.string.fs_latitude)) != null) {
                String temp = bundle.getString(getString(R.string.fs_latitude));
                latitude = Double.valueOf(temp);
            }
            if (bundle.getString(getString(R.string.fs_longitude)) != null) {
                String temp = bundle.getString(getString(R.string.fs_longitude));
                longitude = Double.valueOf(temp);
            }
        }

        findservice(mValueFromList,mSearchBoxValue,CurrentRadius,latitude,longitude, mylatlng,
                myKey);
    }

    /**
     * Uses retrofit to call the Places API based on the place name typed in search box and place
     * type selected from listview.
     * @param mValueFromList
     * @param mSearchBoxValue
     * @param CurrentRadius
     * @param latitude
     * @param longitude
     * @param latLng
     * @param APIkey
     */
    private void findservice(String mValueFromList, String mSearchBoxValue, String
            CurrentRadius, final Double latitude, final Double longitude, String latLng, String
            APIkey) {

        FindServiceAPIInterface apiService =
                new Findserviceretrofit().getClient().create(FindServiceAPIInterface.class);
        Call<NearbyPlacesPojoClass> call = apiService.getNearbyPlaces(latLng,
                CurrentRadius, mValueFromList, mSearchBoxValue, APIkey);
        pDialog = new ProgressDialog(ServiceResultActivity.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();
        call.enqueue(new Callback<NearbyPlacesPojoClass>() {
            @Override
            public void onResponse(Call<NearbyPlacesPojoClass> call, Response<NearbyPlacesPojoClass> response) {
                Results[] places = response.body().getResults();
//                Log.d("info", "Number of places received: " + places.length);
                ArrayList<Results> list = new ArrayList<Results>(Arrays.asList(places));
                ServiceResultAdapter myAdapter = new ServiceResultAdapter(ServiceResultActivity.this, R.layout
                        .custom_info_window, list, places, latitude, longitude);
                findServiceListView.setAdapter(myAdapter);
                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<NearbyPlacesPojoClass> call, Throwable t) {
                Log.e(getString(R.string.log_info), t.toString());
            }
        });
    }

    /**
     * Customizing toolbar with no title
     */
    private void setCustomActionBar() {
        // to refer the toolbar
        setSupportActionBar(mToolbar);
        //to eliminate the app name from the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}
