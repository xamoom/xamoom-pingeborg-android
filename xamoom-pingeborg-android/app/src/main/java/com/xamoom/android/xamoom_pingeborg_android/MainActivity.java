package com.xamoom.android.xamoom_pingeborg_android;

import android.Manifest;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xamoom.android.XamoomBeaconService;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.Spot;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import org.altbeacon.beacon.Beacon;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements
        XamoomContentFragment.OnXamoomContentFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener,
        SpotListFragment.OnSpotListFragmentInteractionListener,
        MapFragment.OnMapFragmentInteractionListener {

    public final static int LOCATION_IDENTIFIER_REQUEST_CODE = 1;
    private final static int LOCATION_PERMISSION_CODE = 2;
    private final static int CAMERA_PERMISSION_CODE = 3;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton mQRScannerFAB;
    private Fragment mMainFragment;
    private Fragment mNewFragment;
    private Snackbar mSnackbar;
    private Beacon mLastBeacon;
    private boolean mIsFromBeaconNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(Global.DEBUG_TAG, "onCreate");

        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        //analytics
        Analytics.getInstance(this).sendEvent("App", "Start", "User starts the app");

        //setup Global
        Global.getInstance().setContext(this.getApplicationContext());
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
                if (ContextCompat.checkSelfPermission(getApplication(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    getCameraPermission();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                startActivityForResult(intent, LOCATION_IDENTIFIER_REQUEST_CODE);
            }
        });

        checkPingeborgSystem();

        checkNFC();

        checkBluetooth();

        //show instruction on first start
        if(Global.getInstance().checkFirstStartInstruction())
            showInstructionView();

        //setup artistListFragment
        setupArtistListFragment();

        //ask for permission
        getLocationPermission();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                XamoomBeaconService.getInstance(getApplicationContext()).startRangingBeacons();
            }
        }, new IntentFilter(XamoomBeaconService.BEACON_SERVICE_CONNECT_BROADCAST));

    }

    @Override
    protected void onResume() {
        super.onResume();

        //check if intent is from notification
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(XamoomPingeborgApp.BEACON_NOTIFICATION)) {
            mIsFromBeaconNotification = getIntent().getExtras().getBoolean(XamoomPingeborgApp.BEACON_NOTIFICATION);
        }

        registerReceiver(mFoundBeaconBroadCastReciever, new IntentFilter(XamoomBeaconService.FOUND_BEACON_BROADCAST));
        registerReceiver(mExitBroadCastReciever, new IntentFilter(XamoomBeaconService.EXIT_REGION_BROADCAST));
        XamoomBeaconService.getInstance(getApplicationContext()).startRangingBeacons();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //check if intent is from notification
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(XamoomPingeborgApp.BEACON_NOTIFICATION)) {
            mIsFromBeaconNotification = intent.getExtras().getBoolean(XamoomPingeborgApp.BEACON_NOTIFICATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLastBeacon = null;
        mIsFromBeaconNotification = false;

        //remove intent extra, to don't reopen the ArtistDetailActivity when opened via notification
        getIntent().removeExtra(XamoomPingeborgApp.BEACON_NOTIFICATION);

        XamoomBeaconService.getInstance(getApplicationContext()).stopRangingBeacons();
        unregisterReceiver(mFoundBeaconBroadCastReciever);
        unregisterReceiver(mExitBroadCastReciever);
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
                    //replace fragment when it changed and is not null
                    if (mMainFragment != mNewFragment && mNewFragment != null) {
                        mMainFragment = mNewFragment;
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
                    }
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }
    }

    /**
     * Setup actionbar with title and drawerToggle.
     */
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

    /**
     * Starts ArtistDetailActivity on QR-Scan.
     *
     * @param requestCode Intent RequestCode.
     * @param resultCode Intent ResultCode.
     * @param data Intent
     */
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

    /**
     * Check the pingeborg system
     */
    private void checkPingeborgSystem() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView imagView = (ImageView) headerView.findViewById(R.id.nav_drawer_image);

        if (imagView == null) {
            return;
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.header_image_carinthia)
                .into(imagView);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();

                builder.setView(inflater.inflate(R.layout.nfc_dialog, null))
                        .setTitle("Location")
                        .setMessage("We need your permission for your location to display you" +
                                "location based informations.")
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                        LOCATION_PERMISSION_CODE);
                            }
                        });
                builder.create().show();
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_CODE);
            }
        }
    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();

                builder.setTitle("Camera")
                        .setMessage("To scan QR codes, we need access to your camera")
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                requestPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
                            }
                        });
                builder.create().show();


            } else {
                requestPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
            }
        }
    }

    private void requestPermission(String permission, int permissioncode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                permissioncode);
    }

    /**
     * Checks if the user has NFC and if it is enabled.
     *
     * If it is not enabled, {@link #openNFCDialog()} gets called.
     */
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

    /**
     * Opens a dialog to inform the user, that he can turn on NFC
     * to discover pingeb.org-stickers.
     *
     * On positiveButton automatically sends him to NFC Settings.
     */
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

    /**
     *
     */
    private void checkBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                openBluetoothDialog();
            }
        }
    }

    private void openBluetoothDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.bluetooth_dialog, null))
                .setPositiveButton(R.string.activate_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);
                    }
                })
                .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    /**
     * Creates a ArtistListFragment and displays it.
     */
    private void setupArtistListFragment() {
        mMainFragment = ArtistListFragment.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, mMainFragment).commit();
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
                                mNewFragment = ArtistListFragment.getInstance();
                                break;
                            case R.id.nav_map:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to map fragment", "User navigated to the map fragment");
                                mQRScannerFAB.hide();
                                mNewFragment = MapFragment.getInstance();

                                //display first time map instruction view
                                if (Global.getInstance().checkFirstStartMapInstruction())
                                    showMapInstructionView();
                                break;
                            case R.id.nav_about:
                                Analytics.getInstance(getApplication()).sendEvent("Navigation", "Navigated to about fragment", "User navigated to the about fragment");
                                mQRScannerFAB.hide();
                                mNewFragment = AboutFragment.newInstance();
                                break;
                        }

                        //popBackStack when there is a XamoomContentFragment on BackStack
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

                            //close all fragments in backstack
                            while (backStackCount > 0) {
                                getSupportFragmentManager().popBackStack();
                                backStackCount--;
                            }
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
     * Add new XamoomContentFragment when a content contentBlock is clicked.
     *
     * @param contentId A {@link com.xamoom.android.mapping.Content} contentId
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

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    /**
     * Check when to display home button or back-arrow.
     */
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

    private final BroadcastReceiver mFoundBeaconBroadCastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(XamoomBeaconService.BEACONS);

            beacons = sortBeaconsByRange(beacons);

            if (mLastBeacon == null || !beacons.get(0).equals(mLastBeacon)) {
                mLastBeacon = beacons.get(0);

                View coordinaterView = findViewById(R.id.main_content);
                mSnackbar = Snackbar.make(coordinaterView, R.string.discovered_pingeborg_geofence_message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.Discover, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //open ArtistDetailActivity when clicking on snackbar
                                openArtistDetailView(mLastBeacon);
                            }
                        });
                mSnackbar.show();

                if (mIsFromBeaconNotification) {
                    //open ArtistDetailActivity when app is opened with intent from notification
                    openArtistDetailView(mLastBeacon);
                }
            }
        }
    };

    private final BroadcastReceiver mExitBroadCastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSnackbar.dismiss();
            mLastBeacon = null;
        }
    };

    private void openArtistDetailView(Beacon beacon) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER, beacon.getId3().toString());
        intent.putExtra(ArtistDetailActivity.MAJOR, beacon.getId2().toString());
        context.startActivity(intent);
    }

    private void openArtistDetailView(String contentId) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(XamoomContentFragment.XAMOOM_CONTENT_ID,contentId);
        context.startActivity(intent);
    }

    private ArrayList<Beacon> sortBeaconsByRange(ArrayList<Beacon> beacons) {
        Collections.sort(beacons, new Comparator<Beacon>() {
            public int compare(Beacon beacon1, Beacon beacon2) {
                return Double.valueOf(beacon1.getDistance()).compareTo(beacon2.getDistance());
            }
        });
        return beacons;
    }

    @Override
    public void foundGeofence(final String contentId) {
        if (mLastBeacon == null && !Global.getInstance().getSavedArtists().contains(contentId)) {
            View coordinaterView = findViewById(R.id.main_content);
            Snackbar.make(coordinaterView, R.string.discovered_pingeborg_geofence_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.Discover, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //open ArtistDetailActivity when clicking on snackbar
                            Global.getInstance().saveArtist(contentId);
                            openArtistDetailView(contentId);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void clickedSpot(Spot spot) {
        Log.v(Global.DEBUG_TAG, "MainActivity: ClickedSpot");
        MapFragment mapFragment = (MapFragment) mMainFragment;
        mapFragment.displayMarker(spot);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mQRScannerFAB.callOnClick();

                } else {

                    Toast.makeText(this.getApplicationContext(), "Not allowed to use camera.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
