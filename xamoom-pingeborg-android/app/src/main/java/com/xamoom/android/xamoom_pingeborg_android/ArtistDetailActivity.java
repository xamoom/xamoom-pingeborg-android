package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.util.List;


public class ArtistDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //actionbar
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(Global.getInstance().getCurrentSystemName());

        //get contentId or locationIdentifier from intent
        Intent myIntent = getIntent();
        String contentId = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_CONTENT_ID);
        String locationIdentifier= myIntent.getStringExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER);

        //load data
        if (!contentId.equals("")) {
            Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with contentId: " + contentId);
            XamoomEndUserApi.getInstance().getContentbyIdFull(contentId, false, false, null, true, new APICallback<ContentById>() { //TODO: Check if full o
                @Override
                public void finished(ContentById result) {
                    //create title and titleImage from content & add them to contentBlocks
                    ContentBlockType0 cb0 = new ContentBlockType0(result.getContent().getTitle(), true, 0, result.getContent().getDescriptionOfContent());
                    result.getContent().getContentBlocks().add(0, cb0);
                    ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                    result.getContent().getContentBlocks().add(1, cb3);

                    setupXamoomContentFrameLayout(result.getContent().getContentBlocks());
                }
            });
        } else if (!locationIdentifier.equals("")){
            Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with locationIdentifier: " + locationIdentifier);
            XamoomEndUserApi.getInstance().getContentByLocationIdentifier(locationIdentifier, false, false, null, new APICallback<ContentByLocationIdentifier>() {
                @Override
                public void finished(ContentByLocationIdentifier result) {
                    //create title and titleImage from content & add them to contentBlocks
                    ContentBlockType0 cb0 = new ContentBlockType0(result.getContent().getTitle(), true, 0, result.getContent().getDescriptionOfContent());
                    result.getContent().getContentBlocks().add(0, cb0);
                    ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                    result.getContent().getContentBlocks().add(1, cb3);

                    setupXamoomContentFrameLayout(result.getContent().getContentBlocks());
                }
            });
        } else {
            Log.w(Global.DEBUG_TAG, "There is no contentId or locationIdentifier");
        }
    }

    private void setupXamoomContentFrameLayout(List<ContentBlock> contentBlocks) {
        XamoomContentFragment fragment = XamoomContentFragment.newInstance(null, Global.YOUTUBE_API_KEY);
        fragment.setContentBlocks(contentBlocks);

        if (!this.isDestroyed())
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.XamoomContentFrameLayout, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
