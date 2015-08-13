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
    private static final String FIRST_START_INSTRUCTION_KEY = "checkFirstStartInstruction.android.xamoom.at";
    private static final String FIRST_START_MAP_INSTRUCTION_KEY = "checkFirstStartMapInstruction.android.xamoom.at";

    private static Global mInstance;

    private SharedPreferences mSharedPreferences;
    private String mAboutPage;
    private String mSystemName;
    private int mCurrentSystem;
    private Activity mContext;

    public Global () {
    }

    /**
     * Returns a Global singleton.
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
     * Setter for activity.
     * Directly saves an instance of preferences.
     *
     * @param activity
     */
    public void setActivity(Activity activity) {
        mContext = activity;
        mSharedPreferences = activity.getPreferences(activity.getApplicationContext().MODE_PRIVATE);
    }

    /**
     * Save a string to shared preferences with key.
     *
     * @param key String Key
     * @param value String value
     */
    public void saveStringToSharedPref (String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a value saved to shared preferences with key.
     *
     * @param key String key
     * @return String result
     */
    public String getStringFromSharedPref (String key) {
       return mSharedPreferences.getString(key, "");
    }

    /**
     * Saves an artists contentId (unlock).
     *
     * @param contentId ContentId to save.
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
     * Returns all saved artists.
     *
     * @return Comma seprateted string of all artists.
     */
    public String getSavedArtists() {
        return getStringFromSharedPref(SAVED_ARTISTS_KEY);
    }

    /**
     * Check if this is the first start of the app.
     *
     * @return true if first start, else false.
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

    /**
     * Check if the first Start Instruction are displayed.
     *
     * @return True if not shown, else false.
     */
    public boolean checkFirstStartInstruction() {
        if(mSharedPreferences.getBoolean(FIRST_START_INSTRUCTION_KEY, false)) {
            return false;
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(FIRST_START_INSTRUCTION_KEY, true);
            editor.apply();

            return true;
        }
    }

    /**
     * Check if first Map Start Instruction are displayed.
     *
     * @return True if not shown, else false.
     */
    public boolean checkFirstStartMapInstruction() {
        if(mSharedPreferences.getBoolean(FIRST_START_MAP_INSTRUCTION_KEY, false)) {
            return false;
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(FIRST_START_MAP_INSTRUCTION_KEY, true);
            editor.apply();

            return true;
        }
    }

    /**
     * Returns the systems about page.
     *
     * @return ContentId of the about page from xamoom cloud.
     */
    public String getAboutPage() {
        return mAboutPage;
    }

    /**
     * Set another system, to get the right systemName and aboutPage.
     *
     * 0 = Carinthia
     * Default = Carinthia
     *
     * @param id
     */
    public void setCurrentSystem(int id) {
        switch (id) {
            case 0:
                mAboutPage = mContext.getString(R.string.pingeborg_carinthia_about_page_id);
                mSystemName = mContext.getString(R.string.pingeborg_carinthia_system_name);
                break;
            default:
                mAboutPage = mContext.getString(R.string.pingeborg_carinthia_about_page_id);
                mSystemName = mContext.getString(R.string.pingeborg_carinthia_system_name);
                break;
        }
        mCurrentSystem = id;
    }

    /**
     * Returns the current systemName.
     *
     * @return String systemName.
     */
    public String getCurrentSystemName() {
        return mSystemName;
    }
}
