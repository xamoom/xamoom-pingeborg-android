package com.xamoom.android.xamoom_pingeborg_android;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ArtistListFragment.OnFragmentInteractionListener, GeofenceFragment.OnGeofenceFragmentInteractionListener, XamoomContentFragment.OnXamoomContentFragmentInteractionListener, FragmentManager.OnBackStackChangedListener {

    public final static int LOCATION_IDENTIFIER_REQUEST_CODE = 0001;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton mQRScannerFAB;
    private Fragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        //Strict Policy
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyFlashScreen().build());

        //analytics
        Analytics.getInstance(this).sendEvent("App", "Start", "User starts the app");

        //setup Global
        Global.getInstance().setActivity(this);
        Global.getInstance().setCurrentSystem(0);

        //setup navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //setup toolbar/actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        //setup NavigationDrawer
        setupNavigationDrawer(navigationView);

        //setup FAB Button
        mQRScannerFAB = (FloatingActionButton) findViewById(R.id.fab);
        mQRScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                startActivityForResult(intent, LOCATION_IDENTIFIER_REQUEST_CODE);
            }
        });

        checkPingeborgSystem();

        checkNFC();

        //show instruction on first start
        if(Global.getInstance().checkFirstStartInstruction())
            showInstructionView();

        //setup artistListFragment
        setupArtistListFragment();
    }

    public void setupNavigationDrawer(NavigationView navigationView) {
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {

                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (mMainFragment != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }
    }

    public void setupActionBar() {
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            //set title
            ab.setTitle(Global.getInstance().getCurrentSystemName());

            //setup DrawerToggle
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    //toolbar,
                    R.string.app_name,
                    R.string.app_name);

            if (mDrawerLayout != null)
                mDrawerLayout.setDrawerListener(mDrawerToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mDrawerToggle.syncState();
        }
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

    private void checkPingeborgSystem() {
        ImageView imagView = (ImageView) this.findViewById(R.id.nav_drawer_image);
        Glide.with(this.getApplicationContext())
                .load(R.drawable.header_image_carinthia)
                .into(imagView);
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
        Log.v(Global.DEBUG_TAG, "setupArtistListFragment");
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
                                mQRScannerFAB.show();
                                mMainFragment = ArtistListFragment.newInstance();
                                break;
                            case R.id.nav_map:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to map fragment", "User navigated to the map fragment");
                                mQRScannerFAB.hide();
                                mMainFragment = MapFragment.newInstance();

                                if (Global.getInstance().checkFirstStartMapInstruction())
                                    showMapInstructionView();

                                break;
                            case R.id.nav_about:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to about fragment", "User navigated to the about fragment");
                                mQRScannerFAB.hide();
                                mMainFragment = AboutFragment.newInstance();
                                break;
                        }

                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
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
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
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
        if(mMainFragment.getClass().equals(MapFragment.class)) {
            MapFragment mapFragment = (MapFragment) mMainFragment;
            mapFragment.closeGeofenceFragment();
        }
    }

    @Override
    public void clickedContentBlock(Content content) {
        //also discover this artist
        Global.getInstance().saveArtist(content.getContentId());

        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2));
        fragment.setContent(content);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_swipe_in, 0, 0, R.anim.bottom_swipe_out)
                .add(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            mQRScannerFAB.setVisibility(View.GONE);
            makeBackButton();
        } else {
            mQRScannerFAB.setVisibility(View.VISIBLE);
            makeBurgerButton();
        }
    }

    public void makeBackButton() {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(300);
        anim.start();
    }

    public void makeBurgerButton() {
        ValueAnimator anim = ValueAnimator.ofFloat(1, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(300);
        anim.start();
    }

    public void showInstructionView() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.artistlist_overlay_layout, null);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
        });

        getWindow().addContentView(v, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    public void showMapInstructionView() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.map_overlay_layout, null);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
        });

        getWindow().addContentView(v, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
    }
}
