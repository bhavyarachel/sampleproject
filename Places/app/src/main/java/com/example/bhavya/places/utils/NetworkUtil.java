package com.example.bhavya.places.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.bhavya.places.R;

/**
 * Created by bhavya on 22/9/16.
 */

public class NetworkUtil {

    public static int TYPE_CONNECTION_NOCONNECTION = 0;
    public static int TYPE_CONNECTION_MOBILE = 1;
    public static int TYPE_CONNECTION_WIFI = 2;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_CONNECTION_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_CONNECTION_MOBILE;
        }
        return TYPE_CONNECTION_NOCONNECTION;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_CONNECTION_WIFI) {
            status = Resources.getSystem().getString(R.string.wifi_enabled);
        } else if (conn == NetworkUtil.TYPE_CONNECTION_MOBILE) {
            status = Resources.getSystem().getString(R.string.mobile_data_enabled);
        } else if (conn == NetworkUtil.TYPE_CONNECTION_NOCONNECTION) {
            status = Resources.getSystem().getString(R.string.no_connection);
        }
        return status;
    }

}
