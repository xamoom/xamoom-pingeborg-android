package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;


public class MainActivity extends ActionBarActivity implements ArtistListFragment.OnFragmentInteractionListener, GeofenceFragment.OnGeofenceFragmentInteractionListener {

    public final static int LOCATION_IDENTIFIER_REQUEST_CODE = 0001;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mQRScannerFAB;
    private Fragment mMainFragment;
    private Fragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Strict Policy
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyFlashScreen().build());

        //analytics
        Analytics.getInstance(this).sendEvent("App", "Start", "User startet the app");

        //check if this is the first start of the app
        Global.getInstance().setActivity(this);
        Global.getInstance().setCurrentSystem(0);
        if (Global.getInstance().isFirstStart()) {
            Log.v(Global.DEBUG_TAG, "First time starting the app");
        }

        //setup toolbar/actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(Global.getInstance().getCurrentSystemName());

        //setup navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        //setup FAB Button
        mQRScannerFAB = (FloatingActionButton) findViewById(R.id.fab);
        mQRScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                startActivityForResult(intent, LOCATION_IDENTIFIER_REQUEST_CODE);
            }
        });

        checkNFC();

        //setup artistListFragment
        setupArtistListFragment();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_IDENTIFIER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String locationIdentifier = data.getStringExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER);
                Log.v(Global.DEBUG_TAG, locationIdentifier);

                Intent intent = new Intent(MainActivity.this, ArtistDetailActivity.class);
                intent.putExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER, locationIdentifier);
                startActivity(intent);
            }
        }
    }

    private void checkNFC() {
        NfcManager manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null ) {
            if (!adapter.isEnabled()) {
                Log.v(Global.DEBUG_TAG, "NFC is not activated");
                openNFCDialog();
            }
        }
    }

    private void openNFCDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.nfc_dialog, null))
                .setPositiveButton(R.string.activate_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), 0);
                    }
                })
                .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private void setupArtistListFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, ArtistListFragment.newInstance()).commit();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to artist list fragment", "User navigated to the artist list fragment");
                                mQRScannerFAB.setVisibility(View.VISIBLE);
                                mMainFragment =  ArtistListFragment.newInstance();
                                break;
                            case R.id.nav_map:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to map fragment", "User navigated to the map fragment");
                                mQRScannerFAB.setVisibility(View.GONE);
                                mMainFragment = MapActivityFragment.newInstance();
                                break;
                            case R.id.nav_about:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to about fragment", "User navigated to the about fragment");
                                mQRScannerFAB.setVisibility(View.GONE);
                                mMainFragment = AboutFragment.newInstance();
                                break;
                            case R.id.nav_settings:
                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Analytics.getInstance(this).sendEvent("UX", "Drawer opened", "User opened the navigation drawer");
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public void closeGeofenceFragment() {
        if(mMainFragment.getClass().equals(MapActivityFragment.class)) {
            Log.v("pingeborg", "MainFragment: " + mMainFragment);
            MapActivityFragment mapActivityFragment = (MapActivityFragment) mMainFragment;
            mapActivityFragment.closeGeofenceFragment();
        }
    }
}
