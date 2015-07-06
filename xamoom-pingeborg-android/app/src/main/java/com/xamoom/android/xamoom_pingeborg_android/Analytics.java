package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Analytics allows us to easily send screenNames and events to GoogleAnalytics.
 *
 * @author Raphael Seher
 */
public class Analytics {

    private static Analytics mInstance = null;
    private static GoogleAnalytics mAnalytics;
    private static Tracker mTracker;

    public static Analytics getInstance(Context context){
        if(mInstance == null) {
            mInstance = new Analytics();
            setupAnalytics(context);
        }
        return mInstance;
    }

    public static void setupAnalytics(Context context) {
        mAnalytics = GoogleAnalytics.getInstance(context);
        mTracker = mAnalytics.newTracker("UA-57427460-3");
        mTracker.enableExceptionReporting(true);
        mTracker.enableAdvertisingIdCollection(true);
    }

    /**
     * Send a screenName to our GoogleAnalytics.
     *
     * @param screenName Name of the Screen. E.g. "Home Screen" or "Detail Screen - Artist 1"
     */
    public void setScreenName (String screenName) {
        mTracker.setScreenName(screenName);
    }

    /**
     * Send an event to our GoogleAnalytics.
     *
     * @param category Should be e.g. "UX"
     * @param action Should be e.g. "click", "swipe", "scroll", etc
     * @param label Additional string for additional information.
     */
    public void sendEvent (String category, String action, String label) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
