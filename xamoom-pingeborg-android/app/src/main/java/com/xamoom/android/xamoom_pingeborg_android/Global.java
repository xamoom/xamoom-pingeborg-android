package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by raphaelseher on 30.06.15.
 */
public class Global {

    private static Global mInstance;
    private static SharedPreferences mSharedPreferences;

    public Global () {
    }

    public static Global getInstance() {
        if (mInstance == null) {
            mInstance = new Global();
        }
        return mInstance;
    }

    public void setActivity(Activity activity) {
        mSharedPreferences = activity.getPreferences(activity.getApplicationContext().MODE_PRIVATE);
    }

    public void saveStringToSharedPref (String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor. commit();
    }

    public String getStringFromSharedPref (String key) {
       return mSharedPreferences.getString(key, null);
    }

    public void saveArtist(String contentId) {
        String savedArtists = getStringFromSharedPref("savedArtists");
        if (savedArtists != null) {
            if (!savedArtists.contains(contentId))
                savedArtists = savedArtists.concat(","+contentId);
        } else {
            savedArtists = contentId;
        }
        saveStringToSharedPref("savedArtists", savedArtists);
    }

    public String getSavedArtists() {
        return getStringFromSharedPref("savedArtists");
    }

    public boolean isFirstStart () {
        if(mSharedPreferences.getBoolean("isFirstStart", false)) {
            return false;
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isFirstStart", true);
            editor.commit();

            return true;
        }
    }

}
