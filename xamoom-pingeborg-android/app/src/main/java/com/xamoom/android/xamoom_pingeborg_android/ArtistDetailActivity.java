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

import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;
import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Enums.ContentFlags;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;

import at.rags.morpheus.Error;

/**
 * ArtistDetailActivity can be used to display content from the xamoom cloud.
 *
 * We use it to display content discovered with NFC.
 *
 */
public class ArtistDetailActivity extends AppCompatActivity implements XamoomContentFragment.OnXamoomContentFragmentInteractionListener {
    public static final String CONTENT_ID = "0000";
    public static final String LOCATION_IDENTIFIER = "0001";
    public static final String MAJOR = "0002";

    private String mContentId;
    private String mLocationIdentifier;
    private int mMajor;

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
        mContentId = myIntent.getStringExtra(CONTENT_ID);
        mLocationIdentifier = myIntent.getStringExtra(LOCATION_IDENTIFIER);
        if (myIntent.getStringExtra(MAJOR) != null) {
            mMajor = Integer.parseInt(myIntent.getStringExtra(MAJOR));
        }

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

                        if (newUrl == null) {
                            Uri fallbackUri = Uri.parse(url[0]);
                            openInBrowser(null, fallbackUri.getLastPathSegment());
                            return;
                        }

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

    private void loadData(final String contentId, final String locationIdentifier) {
        Log.v(Global.DEBUG_TAG, "ArtistDetailActivity - loadData");
        //load data
        if (contentId != null) {
            if(Global.getInstance().getSavedArtists().contains(contentId)) {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mContentId: " + contentId);

                EnduserApi.getSharedInstance().getContent(contentId, EnumSet.of(ContentFlags.PRIVATE), new APICallback<Content, List<Error>>() {
                    @Override
                    public void finished(Content result) {
                        setupXamoomContentFrameLayout(result);
                    }

                    @Override
                    public void error(List<Error> error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            } else {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mContentId: " + contentId);
                EnduserApi.getSharedInstance().getContent(contentId, new APICallback<Content, List<Error>>() {
                    @Override
                    public void finished(Content result) {
                        setupXamoomContentFrameLayout(result);

                    }

                    @Override
                    public void error(List<Error> error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            }
        } else if (locationIdentifier != null) {
            if (mMajor < 0) {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mLocationIdentifier: " + locationIdentifier);

                EnduserApi.getSharedInstance().getContentByBeacon(mMajor, Integer.parseInt(locationIdentifier), new APICallback<Content, List<Error>>() {
                    @Override
                    public void finished(Content result) {
                        //save artist
                        Global.getInstance().saveArtist(result.getId());
                        setupXamoomContentFrameLayout(result);
                    }

                    @Override
                    public void error(List<Error> error) {
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            } else {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail with iBeacon", "User opened artist detail activity with mLocationIdentifier: " + locationIdentifier);

                EnduserApi.getSharedInstance().getContentByLocationIdentifier(locationIdentifier, new APICallback<Content, List<Error>>() {
                    @Override
                    public void finished(Content result) {
                        Global.getInstance().saveArtist(result.getId());
                        setupXamoomContentFrameLayout(result);
                    }

                    @Override
                    public void error(List<Error> error) {
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            }


        } else {
            Log.w(Global.DEBUG_TAG, "There is no mContentId or mLocationIdentifier");
            finish();
        }
    }

    private void setupXamoomContentFrameLayout(Content content) {
        XamoomContentFragment fragment = XamoomContentFragment.newInstance(getResources().getString(R.string.youtubekey));
        fragment.setEnduserApi(EnduserApi.getSharedInstance());
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

    public void downloadContent(String contentId, boolean full, final APICallback<Content, List<Error>> callback) {
        EnumSet<ContentFlags> contentFlags = null;
        if (!full) {
            contentFlags = EnumSet.of(ContentFlags.PRIVATE);
        }

        EnduserApi.getSharedInstance().getContent(contentId, contentFlags, new APICallback<Content, List<Error>>() {
            @Override
            public void finished(Content result) {
                callback.finished(result);
            }

            @Override
            public void error(List<Error> error) {
                //TODO errorhandling
            }
        });
    }

    /**
     * Override the handling of content ContentBlocks.
     * We add a new XamoomContentFragment to the activity.
     *
     * @param content Content you can pass to XamoomContentFragment
     */
    @Override
    public void clickedContentBlock(Content content) {
        //also discover this artist
        Global.getInstance().saveArtist(content.getId());

        downloadContent(content.getId(), true, new APICallback<Content, List<Error>>() {
            @Override
            public void finished(Content result) {
                setupXamoomContentFrameLayout(result);
            }

            @Override
            public void error(List<Error> error) {
                //already done
            }
        });

    }

    /**
     * Override the handling of SpotMap links.
     *
     * We add a new XamoomContentFragment to the activity.
     *
     * @param contentId ContentId you can pass to XamoomContentFragment
     */
    @Override
    public void clickedSpotMapContentLink(String contentId) {
        //also discover this artist
        Content content = new Content();
        content.setId(contentId);
        clickedContentBlock(content);
    }
}
