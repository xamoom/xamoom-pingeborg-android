package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.net.HttpURLConnection;
import java.net.URL;


public class QRCodeScannerActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView mydecoderview;
    private Toast mToast;
    boolean isScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        //setup toolbar/actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.scan_qr_text));
        }

        //set statusbar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.pingeborg_dark_yellow));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrcode_scanner, menu);
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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks the scanned urls.
     *
     * When it is an old pingeb.org-sticker, the url contains "pingeb.org".
     * Can also have a subdomain like "villach.pingeb-org.at". These urls
     * gets redirected and to normal xm.gl urls. After redirecting the locationIdentifier
     * will be used to display the content.
     * Normal xm.gl urls will used to display the content.
     * Opens an {@link ArtistDetailActivity} to display the content.
     *
     * Error Toast when trying to scan any other QRs.
     *
     * @param url String url.
     */
    private void checkUrl(final String url) {
        //check for "old" pingeb.org urls
        if(url.contains("pingeb.org")) {
            Log.v(Global.DEBUG_TAG, "pingeb.org URL");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //get the url redirected
                        URL url2 = new URL(url);
                        HttpURLConnection ucon = (HttpURLConnection) url2.openConnection();
                        ucon.setInstanceFollowRedirects(false);
                        final String newUrl = ucon.getHeaderField("Location");
                        ucon.disconnect();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startDetailActivityWithXamoomUrl(newUrl);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.old_pingeborg_sticker_redirect_failure), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            thread.start();
        } else if(url.contains("xm.gl")) {
            startDetailActivityWithXamoomUrl(url);
        } else {
            if(mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), getString(R.string.scanned_false_qr_code), Toast.LENGTH_SHORT);
            }

            if(!mToast.getView().isShown()) {
                mToast.show();
            }

            isScanned = false;
        }
    }

    /**
     * Starts a ArtistDetailActivity with an url.
     *
     * @param url String url of xm.gl.
     */
    private void startDetailActivityWithXamoomUrl(String url) {
        //get locationIdentifier
        Uri mUri = Uri.parse(url);
        String locationIdentifier = mUri.getLastPathSegment();

        //start artist detail activity
        Intent returnIntent = new Intent();
        returnIntent.putExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER, locationIdentifier);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onQRCodeRead(String s, PointF[] pointFs) {
        if (!isScanned) {
            isScanned = true;
            checkUrl(s);
        }
    }

    @Override
    public void cameraNotFound() {
        Toast.makeText(getApplicationContext(), "Camera not found", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {
    }
}
