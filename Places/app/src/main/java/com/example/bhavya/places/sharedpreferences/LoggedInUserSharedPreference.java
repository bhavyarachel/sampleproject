package com.example.bhavya.places.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by bhavya on 9/8/16.
 *
 * Sets the Login status of the user
 */

public class LoggedInUserSharedPreference {

    public static String PREF_EMAILID = "EMAIL_KEY";

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUserName(Context ctx, String userEmail) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAILID, userEmail);
        editor.commit();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(PREF_EMAILID, "");
    }
}
