package com.example.bhavya.places.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

/**
 * A simple {@link Fragment} subclass.
 * Displays the nearby restaurants from user's location in a grid view format.
 */
public class GridRepresentationFragment extends Fragment {

    GridView gridView;
    private String json;
    private Double latitude;
    private Double longitude;
    private LatLng latLng;


    public GridRepresentationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_representation, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromDrawerActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewProperties();
    }

    /**
     * To initialize view properties
     */
    private void setViewProperties(){
        gridView = (GridView) getView().findViewById(R.id.gridview);
    }

    /**
     * Broadcast Receiver for getting location updates of user's current location
     */
    BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        WeakReference<GridRepresentationFragment> fragmentWeakReference = new WeakReference(GridRepresentationFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                GridRepresentationFragment gridRepresentationFragment = fragmentWeakReference.get();
                gridRepresentationFragment.updateLocation();
            }
        }
    };

    /**
     * Broadcast Receiver for getting nearby places update
     */
    BroadcastReceiver placesUpdateReceiver = new BroadcastReceiver() {
        WeakReference<GridRepresentationFragment> fragmentWeakReference = new WeakReference
                (GridRepresentationFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                json = intent.getStringExtra(getResources().getString(R.string.json));
                GridRepresentationFragment gridRepresentationFragment = fragmentWeakReference.get();
                gridRepresentationFragment.updateGridView();
            }
        }
    };

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
     * To update grid view with json data from drawer activity
     */
    private void updateGridView(){
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
                R.layout.custom_info_window, list, json.toString(), latitude, longitude);
        gridView.setAdapter(myAdapter);
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
            updateGridView();
    }

    /***
     * To get latitude, longitude and json API result from drawer activity
     */
    private void getDataFromDrawerActivity(){
        latLng = ((DrawerActivity)getActivity()).getCurrentLatLong();
        json = ((DrawerActivity)getActivity()).getAPIResult();
        updateLocation();
    }
}