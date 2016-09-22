package com.example.bhavya.places.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.Results;
import com.example.bhavya.places.ui.activity.DrawerActivity;
import com.example.bhavya.places.ui.adapter.ListItemsAdapter;
import com.example.bhavya.places.ui.adapter.ViewPagerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * This is a parent fragment with 3 children fragments- Map, Nearby, Grid.
 * This fragment has a view pager.
 * This fragment is loaded when near by places is selected from the navigation drawer menu.
 */

public class NearbyPlaceswithViewPagerFragment extends Fragment {


    @BindView(R.id.listview)
    ListView listview;


    private int tabCount;
    private ViewPager viewPager;
    private View view;
    private ViewPagerAdapter ViewPagerAdapter;
    private String json;
    private Double latitude;
    private Double longitude;
    private LatLng latLng;


    public NearbyPlaceswithViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_places_with_viewpager, container, false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewProperties();
        ButterKnife.bind(this,view);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromDrawerActivity();
    }


    /**
     * Broadcast Receiver for getting location updates of user's current location
     */
    BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        WeakReference<NearbyPlaceswithViewPagerFragment> fragmentWeakReference = new WeakReference(NearbyPlaceswithViewPagerFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                NearbyPlaceswithViewPagerFragment nearbyPlaceswithViewPagerFragment = fragmentWeakReference.get();
                nearbyPlaceswithViewPagerFragment.updateLocation();
            }
        }
    };

    /**
     * Broadcast Receiver for getting nearby places update
     */
    BroadcastReceiver placesUpdateReceiver = new BroadcastReceiver() {
        WeakReference<NearbyPlaceswithViewPagerFragment> fragmentWeakReference = new WeakReference
                (NearbyPlaceswithViewPagerFragment.this);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (fragmentWeakReference != null) {
                json = intent.getStringExtra(getResources().getString(R.string.json));
                NearbyPlaceswithViewPagerFragment nearbyPlaceswithViewPagerFragment = fragmentWeakReference.get();
                nearbyPlaceswithViewPagerFragment.updateListView();
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
     * Initializing view properties, view pager and tab layout
     */
    private void setViewProperties() {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabCount = getResources().getInteger(R.integer.tab_count);
        ViewPagerAdapter = new ViewPagerAdapter(getContext(),getFragmentManager(), tabCount);
        viewPager.setAdapter(ViewPagerAdapter);
//        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(getResources().getInteger(R.integer.start_page));
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

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

    private void getDataFromDrawerActivity(){
        latLng = ((DrawerActivity)getActivity()).getCurrentLatLong();
        json = ((DrawerActivity)getActivity()).getAPIResult();
        updateLocation();
    }
}
