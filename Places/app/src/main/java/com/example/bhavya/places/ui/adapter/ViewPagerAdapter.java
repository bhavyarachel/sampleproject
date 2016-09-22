package com.example.bhavya.places.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.bhavya.places.R;
import com.example.bhavya.places.ui.fragments.GridRepresentationFragment;
import com.example.bhavya.places.ui.fragments.MapFragment;
import com.example.bhavya.places.ui.fragments.NearByPlaceswithoutViewPagerFragment;

import java.util.HashMap;


/**
 * Created by bhavya on 12/8/16.
 * This class specifies the position of each fragment using getItem method, returns the number of
 * tab cells using getCount method - (Map,List for phone / Map,List,Grid for tablets) and
 * returns the title of each tab using getPageTitle method.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private int tabCount;

    public ViewPagerAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        mContext = context;
        this.tabCount = tabCount;
    }

    /**
     * based on the current page being displayed, this returns the correct fragment for each page
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;

            case 1:
                return new NearByPlaceswithoutViewPagerFragment();

            case 2:
                return new GridRepresentationFragment();
        }
        return null;
    }

    /**
     * Specifies the tab count for each screen size - 2 for phone and 3 for tablets
     */
    @Override
    public int getCount() {
        return tabCount;
    }

    /**
     * specify the required title for each tab
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.pageTitleMap);
            case 1:
                return mContext.getString(R.string.pageTitleNearbyPlaces);
            case 2:
                return mContext.getString(R.string.pageTitleGrid);
        }
        return null;
    }


}
