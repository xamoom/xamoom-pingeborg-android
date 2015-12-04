package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.RetrofitError;

/**
 * ArtistDetailActivity can be used to display content from the xamoom cloud.
 *
 * We use it to display content discovered with NFC.
 *
 */
public class ArtistDetailActivity extends AppCompatActivity implements XamoomContentFragment.OnXamoomContentFragmentInteractionListener {

    public static final String MAJOR = "MAJOR";

    private String mContentId;
    private String mLocationIdentifier;
    private String mMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Log.v(Global.DEBUG_TAG, "ArtistDetailActivity - onCreate");

        //set statusbar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.pingeborg_dark_yellow));
        }

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup Global
        Global.getInstance().setContext(this.getApplicationContext());
        Global.getInstance().setCurrentSystem(0);

        //setup actionbar
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(Global.getInstance().getCurrentSystemName());
        }

        //get mContentId or mLocationIdentifier from intent
        Intent myIntent = getIntent();
        mContentId = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_CONTENT_ID);
        mLocationIdentifier = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER);
        mMajor = myIntent.getStringExtra(MAJOR);

        if(mContentId != null || mLocationIdentifier != null) {
            loadData(mContentId, mLocationIdentifier);
        } else {
            onNewIntent(getIntent());
        }
    }

    /**
     * Get from an NFC scan the URL and display the data.
     *
     * @param intent Intent send from system when scanning NFC
     */
    protected void onNewIntent(Intent intent) {
        Log.v(Global.DEBUG_TAG, "ArtistDetailActivity - onNewIntent");
        final String[] url = {intent.getDataString()};

        Analytics.getInstance(this).sendEvent("App", "NFC Scan in App", "User scanned an NFC Sticker");

        if(url[0].contains("pingeb.org")) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //get the url redirected
                        URL url2 = new URL(url[0]);
                        HttpURLConnection ucon = (HttpURLConnection) url2.openConnection();
                        ucon.setInstanceFollowRedirects(false);
                        final String newUrl = ucon.getHeaderField("Location");
                        ucon.disconnect();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                url[0] = newUrl;

                                Uri mUri = Uri.parse(url[0]);
                                mLocationIdentifier = mUri.getLastPathSegment();

                                loadData(mContentId, mLocationIdentifier);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.old_pingeborg_sticker_redirect_failure), Toast.LENGTH_LONG).show();
                    }
                }
            };
            thread.start();
        } else {
            Uri mUri = Uri.parse(url[0]);
            mLocationIdentifier = mUri.getLastPathSegment();
            loadData(mContentId, mLocationIdentifier);
        }
    }

    private void loadData(final String contentId, final String locationIdentifier) {
        Log.v(Global.DEBUG_TAG, "ArtistDetailActivity - loadData");
        //load data
        if (contentId != null) {
            if(Global.getInstance().getSavedArtists().contains(contentId)) {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mContentId: " + contentId);
                XamoomEndUserApi.getInstance(this.getApplicationContext(), getResources().getString(R.string.apiKey)).getContentbyId(contentId, false, false, null, true, false, new APICallback<ContentById>() {
                    @Override
                    public void finished(ContentById result) {
                        setupXamoomContentFrameLayout(result.getContent());
                    }

                    @Override
                    public void error(RetrofitError error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            } else {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mContentId: " + contentId);
                XamoomEndUserApi.getInstance(this.getApplicationContext(), getResources().getString(R.string.apiKey)).getContentbyId(contentId, false, false, null, false, false, new APICallback<ContentById>() {
                    @Override
                    public void finished(ContentById result) {
                        setupXamoomContentFrameLayout(result.getContent());
                    }

                    @Override
                    public void error(RetrofitError error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            }
        } else if (locationIdentifier != null) {
            if (mMajor == null) {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mLocationIdentifier: " + locationIdentifier);
            } else {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail with iBeacon", "User opened artist detail activity with mLocationIdentifier: " + locationIdentifier);
            }

            XamoomEndUserApi.getInstance(this.getApplicationContext(), getResources().getString(R.string.apiKey)).getContentByLocationIdentifier(locationIdentifier, mMajor, false, false, null, new APICallback<ContentByLocationIdentifier>() {
                @Override
                public void finished(ContentByLocationIdentifier result) {

                    if(!result.isHasSpot()) {
                        openInBrowser(contentId, locationIdentifier);
                    }

                    //save artist
                    Global.getInstance().saveArtist(result.getContent().getContentId());
                    Log.v(Global.DEBUG_TAG, "Scanned artist: " + result.getContent().getContentId());
                    Log.v(Global.DEBUG_TAG, "Saved artists: " + Global.getInstance().getSavedArtists());
                    setupXamoomContentFrameLayout(result.getContent());
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                    openInBrowser(contentId, locationIdentifier);
                }
            });
        } else {
            Log.w(Global.DEBUG_TAG, "There is no mContentId or mLocationIdentifier");
            finish();
        }
    }

    private void setupXamoomContentFrameLayout(Content content) {
        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2), getResources().getString(R.string.apiKey));
        fragment.setContent(content);

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.XamoomContentFrameLayout, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    /**
     * Opens the browser with a xamoom content.
     *
     * @param contentId ContentId from xamoom cloud
     * @param locationIdentifier LocationIdentifier from xamoom cloud
     */
    public void openInBrowser(String contentId, String locationIdentifier) {
        Intent browserIntent;
        if(contentId != null) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xm.gl/content/" + contentId));
        } else {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xm.gl/" + locationIdentifier));
        }
        //finish this activity
        finish();

        //open url in browser
        startActivity(browserIntent);
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

    /**
     * Override the handling of content ContentBlocks.
     * These are links to a contentBlock {@link com.xamoom.android.mapping.ContentBlocks.ContentBlockType6}
     * and should be handled like you handle other contents.
     *
     * We add a new XamoomContentFragment to the activity.
     *
     * @param content Content you can pass to XamoomContentFragment
     */
    @Override
    public void clickedContentBlock(Content content) {
        //also discover this artist
        Global.getInstance().saveArtist(content.getContentId());

        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2), getResources().getString(R.string.apiKey));
        fragment.setContentId(content.getContentId());
        fragment.setLoadFullContent(true);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_swipe_in, 0, 0, R.anim.bottom_swipe_out)
                .add(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Override the handling of SpotMap links.
     * These are links to a contentBlock {@link com.xamoom.android.mapping.ContentBlocks.ContentBlockType6}
     * and should be handled like you handle other contents.
     *
     * We add a new XamoomContentFragment to the activity.
     *
     * @param contentId ContentId you can pass to XamoomContentFragment
     */
    @Override
    public void clickedSpotMapContentLink(String contentId) {
        //also discover this artist
        Global.getInstance().saveArtist(contentId);

        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2), getResources().getString(R.string.apiKey));
        fragment.setContentId(contentId);
        fragment.setLoadFullContent(true);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_swipe_in, 0, 0, R.anim.bottom_swipe_out)
                .add(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void setMajor(String major) {
        mMajor = major;
    }
}
