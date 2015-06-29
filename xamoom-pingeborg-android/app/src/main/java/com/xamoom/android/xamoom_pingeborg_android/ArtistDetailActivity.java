package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;


public class ArtistDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Analytics.getInstance(this).setScreenName("Artist Activity");

        Intent myIntent = getIntent(); // gets the previously created intent

        String contentId = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_CONTENT_ID);
        String locationIdentifier= myIntent.getStringExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER);

        if (!contentId.equals("")) {
            Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with contentId: " + contentId);
        } else {
            Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with locationIdentifier: " + locationIdentifier);
        }

        setupXamoomContentFrameLayout(contentId, locationIdentifier);
    }

    private void setupXamoomContentFrameLayout(String contentId, String locationIdentifier) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.XamoomContentFrameLayout, XamoomContentFragment.newInstance(contentId, locationIdentifier, Config.YOUTUBE_API_KEY))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
