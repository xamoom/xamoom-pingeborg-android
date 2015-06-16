package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.xamoomcontentblocks.XamoomContentBlocks;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.io.IOException;


public class ArtistDetailActivity extends ActionBarActivity implements XamoomContentFragment.OnXamoomContentBlocksFragmentInteractionListener {

    public static String XAMOOM_CONTENT_ID = "xamoomContentId";
    public static String XAMOOM_LOCATION_IDENTIFIER = "xamoomLocationIdentifier";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Intent myIntent = getIntent(); // gets the previously created intent

        String contentId = myIntent.getStringExtra(XAMOOM_CONTENT_ID); // will return "FirstKeyValue"
        String locationIdentifier= myIntent.getStringExtra(XAMOOM_LOCATION_IDENTIFIER); // will return "SecondKeyValue"

        setupXamoomContentFrameLayout(contentId, locationIdentifier);
    }

    private void setupXamoomContentFrameLayout(String contentId, String locationIdentifier) {
        getSupportFragmentManager().beginTransaction().replace(R.id.XamoomContentFrameLayout, XamoomContentFragment.newInstance(contentId, locationIdentifier)).commit();
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
