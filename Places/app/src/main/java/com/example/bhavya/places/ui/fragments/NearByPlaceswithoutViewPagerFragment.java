package com.example.bhavya.places.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Results;
import com.example.bhavya.places.ui.activity.DrawerActivity;
import com.example.bhavya.places.ui.adapter.ListItemsAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This fragment does not have a view pager.
 * This fragment is loaded initially when drawer activity is loaded.
 * This fragment is called only when Near By Places Tab is selected.
 */

public class NearByPlaceswithoutViewPagerFragment extends Fragment {

    @BindView(R.id.listviewplaces)
    ListView listview;

    private String json;
    private Double latitude;
    private Double longitude;
    private LatLng latLng;

    public NearByPlaceswithoutViewPagerFragment() {

    }

    /**
     * Broadcast Receiver for getting location updates of user's current location
     */
    BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        WeakReference<NearByPlaceswithoutViewPagerFragment> fragmentWeakReference = new WeakReference
                (NearByPlaceswithoutViewPagerFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                NearByPlaceswithoutViewPagerFragment nearByPlaceswithoutViewPagerFragment = fragmentWeakReference.get();
                nearByPlaceswithoutViewPagerFragment.updateLocation();
            }
        }
    };

    /**
     * Broadcast Receiver for getting nearby places update
     */
    BroadcastReceiver placesUpdateReceiver = new BroadcastReceiver() {
        WeakReference<NearByPlaceswithoutViewPagerFragment> fragmentWeakReference = new
                WeakReference
                (NearByPlaceswithoutViewPagerFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                json = intent.getStringExtra(getResources().getString(R.string.json));
                NearByPlaceswithoutViewPagerFragment nearByPlaceswithoutViewPagerFragment = fragmentWeakReference.get();
                nearByPlaceswithoutViewPagerFragment.updateListView();
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby_places_withoutviewpager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromDrawerActivity();
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

    /**
     * To update the listview with json result obtained from drawer activity
     */
    private void updateListView(){
        Results resultArray[] = null;
        try {
            Gson gson = new Gson();
            NearbyPlacesPojoClass nearbyPlacesObject = gson.fromJson(json,
                    NearbyPlacesPojoClass.class);
            resultArray = nearbyPlacesObject.getResults();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ArrayList<Results> list = new ArrayList<Results>(Arrays.asList(resultArray));
        ListItemsAdapter myAdapter = new ListItemsAdapter(getContext(),
                R.layout.custom_info_window, list, json, latitude, longitude);
        listview.setAdapter(myAdapter);
    }

    /**
     * Updates the current location of the user
     */
    private void updateLocation() {
        DrawerActivity drawerActivity = (DrawerActivity) getActivity();
        LatLng latLng = drawerActivity.getCurrentLatLong();
        if (latLng != null) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }
        if (json.length() > 0)
            updateListView();
    }

    /**
     * To get the latitude, longitude and json result from API call in the drawer activity
     */
    private void getDataFromDrawerActivity(){
        latLng = ((DrawerActivity)getActivity()).getCurrentLatLong();
        json = ((DrawerActivity)getActivity()).getAPIResult();
        updateLocation();
    }
}