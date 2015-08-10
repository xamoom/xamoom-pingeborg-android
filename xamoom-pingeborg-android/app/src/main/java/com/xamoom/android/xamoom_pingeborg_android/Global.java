package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ResourceBundle;

/**
 * Global is used for everything that is used globally.
 */
public class Global {
    public static final String DEBUG_TAG = "pingeborg";

    private static final String SAVED_ARTISTS_KEY = "savedArtists.android.xamoom.at";
    private static final String IS_FIRST_START_KEY = "checkFirstStart.android.xamoom.at";

    private static Global mInstance;

    private SharedPreferences mSharedPreferences;
    private String mAboutPage;
    private int mCurrentSystem;
    private Activity mContext;
    private Boolean mIsFirstStart = false;

    public Global () {
    }

    /**
     * TODO
     *
     * @return Global
     */
    public static Global getInstance() {
        if (mInstance == null) {
            mInstance = new Global();
        }
        return mInstance;
    }

    /**
     * TODO
     */
    public void setActivity(Activity activity) {
        mContext = activity;
        mSharedPreferences = activity.getPreferences(activity.getApplicationContext().MODE_PRIVATE);
    }

    /**
     * TODO
     */
    public void saveStringToSharedPref (String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * TODO
     */
    public String getStringFromSharedPref (String key) {
       return mSharedPreferences.getString(key, "");
    }

    /**
     * TODO
     */
    public void saveArtist(String contentId) {
        String savedArtists = getStringFromSharedPref(SAVED_ARTISTS_KEY);
        if (savedArtists != null) {
            if (!savedArtists.contains(contentId))
                savedArtists = savedArtists.concat(","+contentId);
        } else {
            savedArtists = contentId;
        }
        saveStringToSharedPref(SAVED_ARTISTS_KEY, savedArtists);
    }

    /**
     * TODO
     */
    public String getSavedArtists() {
        return getStringFromSharedPref(SAVED_ARTISTS_KEY);
    }

    /**
     * TODO
     */
    public boolean checkFirstStart() {
        if(mSharedPreferences.getBoolean(IS_FIRST_START_KEY, false)) {
            return false;
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(IS_FIRST_START_KEY, true);
            editor.apply();

            return true;
        }
    }

    public String getAboutPage() {
        return mAboutPage;
    }

    /**
     * TODO
     */
    public void setCurrentSystem(int id) {
        switch (id) {
            case 0:
                mAboutPage = mContext.getString(R.string.pingeborg_carinthia_about_page_id);
                break;
            default:
                mAboutPage = mContext.getString(R.string.pingeborg_carinthia_about_page_id);
                break;
        }
        mCurrentSystem = id;
    }

    /**
     * TODO
     */
    public String getCurrentSystemName() {
        switch (mCurrentSystem) {
            case 0:
                return mContext.getString(R.string.pingeborg_carinthia_system_name);
            default:
                return mContext.getString(R.string.pingeborg_carinthia_system_name);
        }
    }
}
