package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;


public class MainActivity extends ActionBarActivity implements ArtistListFragment.OnFragmentInteractionListener, GeofenceFragment.OnGeofenceFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mQRScannerFAB;
    private Fragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Strict Policy
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyFlashScreen().build());

        Analytics.getInstance(this).sendEvent("App", "Start", "User startet the app");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        mQRScannerFAB = (FloatingActionButton) findViewById(R.id.fab);
        mQRScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //first start?
        Global.getInstance().setActivity(this);
        if (Global.getInstance().isFirstStart()) {
            Log.v("pingeborg", "First time starting the app");
        }

        //setup artistListFragment
        setupArtistListFragment();
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
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
                                break;
                            case R.id.nav_map:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to map fragment", "User navigated to the map fragment");
                                mQRScannerFAB.setVisibility(View.GONE);
                                mMainFragment = MapActivityFragment.newInstance();
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
                                break;
                            case R.id.nav_about:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to about fragment", "User navigated to the about fragment");
                                mQRScannerFAB.setVisibility(View.GONE);
                                mMainFragment = AboutFragment.newInstance();
                                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
                                break;
                            case R.id.nav_settings:
                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
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
            case R.id.action_settings:
                Analytics.getInstance(this).sendEvent("UX", "Artists reloaded", "User called reload in artists action-bar menu");
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, ArtistListFragment.newInstance()).commit();
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
            Log.v("pingeborg","MainFragment: " + mMainFragment);
            MapActivityFragment mapActivityFragment = (MapActivityFragment) mMainFragment;
            mapActivityFragment.closeGeofenceFragment();
        }
    }
}
