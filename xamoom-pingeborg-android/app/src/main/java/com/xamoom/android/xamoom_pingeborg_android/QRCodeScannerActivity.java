package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.net.HttpURLConnection;
import java.net.URL;


public class QRCodeScannerActivity extends ActionBarActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView mydecoderview;
    boolean isScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUrl(final String url) {
        if(url.contains("pingeb.org")) {
            Log.v(Global.DEBUG_TAG, "pingeb.org URL");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
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
                        Toast.makeText(getApplicationContext(), getString(R.string.old_pingeborg_sticker_redirect_failure), Toast.LENGTH_LONG).show();
                    }
                }
            };
            thread.start();
        } else if(url.contains("xm.gl")) {
            startDetailActivityWithXamoomUrl(url);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.scanned_false_qr_code), Toast.LENGTH_LONG).show();
            isScanned = false;
        }
    }

    private void startDetailActivityWithXamoomUrl(String url) {
        //get locationIdentifier
        Uri mUri = Uri.parse(url);
        String locationIdentifier = mUri.getLastPathSegment();

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
