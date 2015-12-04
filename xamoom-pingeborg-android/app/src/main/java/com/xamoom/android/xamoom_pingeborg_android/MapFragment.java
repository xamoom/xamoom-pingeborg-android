package com.xamoom.android.xamoom_pingeborg_android;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentByLocation;
import com.xamoom.android.mapping.ContentByLocationItem;
import com.xamoom.android.mapping.Spot;
import com.xamoom.android.mapping.SpotMap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final ArrayMap<Marker, Spot> markerMap = new ArrayMap<>();

    private static MapFragment mInstance;

    private CoordinatorLayout mCoordinaterLayout;
    private ViewPager mViewPager;
    private SupportMapFragment mSupportMapFragment;
    private MapAdditionFragment mMapAdditionFragment;
    private ProgressBar mProgressBar;

    private GoogleMap mGoogleMap;
    private Marker mActiveMarker;
    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;
    private Location mUserLocation;

    private Bitmap mMarkerIcon;

    private OnMapFragmentInteractionListener mListener;

    /**
     * Returns a MapFragment Singleton.
     *
     * @return A MapFragment.
     */
    public static MapFragment getInstance() {
        if(mInstance == null) {
            mInstance = new MapFragment();
        }
        return mInstance;
    }

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mapProgressBar);
        mCoordinaterLayout = (CoordinatorLayout)view.findViewById(R.id.main_content_map);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
            final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

            //workaround broken TabLayout in appcompat 22.2.1
            if (ViewCompat.isLaidOut(tabLayout)) {
                tabLayout.setupWithViewPager(mViewPager);
            } else {
                tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                        tabLayout.setupWithViewPager(mViewPager);

                        tabLayout.removeOnLayoutChangeListener(this);
                    }
                });
            }
        }
        return view;
    }

    /**
     * Setups a BestLocationProvider and BestLocationProvider to get the users location.
     */
    private void setupLocation() {

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.v(Global.DEBUG_TAG, "Started BestLocationProvider");
        mBestLocationProvider = new BestLocationProvider(getActivity(), false, true, 1000, 1000, 5, 10);
        mBestLocationListener = new BestLocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {
            }

            @Override
            public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
                if(isFresh) {
                    mUserLocation = location;
                    setupGeofencing(location);
                    mBestLocationProvider.stopLocationUpdates();
                }
            }
        };

        //start Location Updates
        startLocationUpdating();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(Global.DEBUG_TAG, "MapFragment - onStop");

        if (mBestLocationListener != null) {
            mBestLocationProvider.stopLocationUpdates();
        }
    }

    /**
     * Setup the viewpager with a supportMapFragment and a SpotListFragment.
     *
     * @param viewPager A ViewPager.
     */
    private void setupViewPager(ViewPager viewPager) {
        mSupportMapFragment = SupportMapFragment.newInstance();

        try {
            //add fragments to viewPager
            FragmentManager fragmentManager = getChildFragmentManager();
            Adapter adapter = new Adapter(fragmentManager);
            adapter.addFragment(mSupportMapFragment, "Map");
            adapter.addFragment(SpotListFragment.newInstance(), "List");
            viewPager.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //hide mapAddition when changing tabs
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    closeMapAdditionFragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mSupportMapFragment.getMapAsync(this);
    }

    /**
     * Starts updating the location.
     */
    public void startLocationUpdating() {
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
    }

    /**
     * Calls xamoom cloud to get a geofence, when near a pingeb.org-sticker.
     *
     * @param location UserLocation for Geofencing.
     */
    public void setupGeofencing(Location location) {
        if(this.getActivity().getApplicationContext() != null) {
            XamoomEndUserApi.getInstance(this.getActivity().getApplicationContext(), getResources().getString(R.string.apiKey)).getContentByLocation(location.getLatitude(), location.getLongitude(), null, new APICallback<ContentByLocation>() {
                @Override
                public void finished(ContentByLocation result) {
                    //open geofence when there is at least on item (you can only get one geofence at a time - the nearest)
                    if (result.getItems().size() > 0) {
                        mBestLocationProvider.stopLocationUpdates();
                        mListener.foundGeofence(result.getItems().get(0).getContentId());
                    }
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                }
            });
        }

    }

    /**
     * Setup mapOnClickListener and onMarkerClickListener.
     * Start to add mapMarkers.
     *
     * @param googleMap A GoogleMap object.
     */
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (marker == mActiveMarker)
                    return true;

                try {
                    mActiveMarker = marker;
                    openMapAdditionFragment(markerMap.get(marker));
                    mCoordinaterLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            zoomMapToMarker(marker);
                        }
                    });
                } catch (Exception e) {
                    Log.e(Global.DEBUG_TAG, "Pressing on many Spot-Markers at the same time. (Stacked Spots in one Place)");
                }

                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeMapAdditionFragment();
            }
        });

        setupMapMarkers(googleMap, markerMap);

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Opens a mapAdditionFragment on clicking a marker.
     * @param spot Spot to open MapAdditionFragment from.
     */
    private void openMapAdditionFragment(Spot spot) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(mMapAdditionFragment == null)
            fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_top, R.anim.slide_top_bottom);

        float distance = 0;
        if(mUserLocation != null) {
            Location spotLocation = new Location("xamoom-api");
            spotLocation.setLatitude(spot.getLocation().getLat());
            spotLocation.setLongitude(spot.getLocation().getLon());
            distance = spotLocation.distanceTo(mUserLocation);
        }

        mMapAdditionFragment = MapAdditionFragment.newInstance(spot.getDisplayName(), spot.getDescription(), spot.getImage(), spot.getLocation(), distance);
        fragmentTransaction.replace(R.id.mapAdditionFrameLayout, mMapAdditionFragment).commit();
    }

    /**
     * Closes the active MapAdditionFragment.
     */
    private void closeMapAdditionFragment() {
        if(mMapAdditionFragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_bottom_top, R.anim.slide_top_bottom)
                    .remove(mMapAdditionFragment)
                    .commit();

            mMapAdditionFragment = null;
        }
    }

    /**
     * Loads the MapMarkers.
     *
     * @param googleMap A GoogleMap Object.
     * @param markerMap ArrayMap with Markers and Spots
     */
    private void setupMapMarkers(final GoogleMap googleMap, final ArrayMap<Marker, Spot> markerMap) {
        if (!markerMap.isEmpty()) {
            Log.v(Global.DEBUG_TAG,"MarkerMap is not empty");
            googleMap.clear();
            addMarkersToMap();
        } else {
            XamoomEndUserApi.getInstance(this.getContext(),
                    getResources().getString(R.string.apiKey)).getSpotMap(
                    new String[]{"showAllTheSpots"}, null, false, new APICallback<SpotMap>() {
                @Override
                public void finished(SpotMap result) {
                    getDataFromSpotMap(result);
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Adds Makers to map with the right mapMarker.
     *
     * @param result Result of {@link com.xamoom.android.XamoomEndUserApi#getSpotMap(String[], String, boolean,  APICallback)}
     */
    public void getDataFromSpotMap(SpotMap result) {
        //get the icon for the mapMarker (drawable image (eg. png) or SVG
        if (result.getStyle().getCustomMarker() != null) {
            String iconString = result.getStyle().getCustomMarker();
            mMarkerIcon = getIconFromBase64(iconString);
        } else {
            mMarkerIcon = BitmapFactory.decodeResource(getActivity().getResources(), com.xamoom.android.xamoomcontentblocks.R.drawable.ic_default_map_marker);
            float imageRatio = (float) mMarkerIcon.getWidth() / (float) mMarkerIcon.getHeight();
            mMarkerIcon = Bitmap.createScaledBitmap(mMarkerIcon, 70, (int) (70 / imageRatio), false);
        }

        //show all markers
        for (Spot s : result.getItems()) {
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(mMarkerIcon))
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .title(s.getDisplayName())
                    .position(new LatLng(s.getLocation().getLat(), s.getLocation().getLon())));

            markerMap.put(marker, s);
        }

        zoomMapAcordingToMarkers();
    }

    /**
     * Adds mapMarker to map, when alreay loaded.
     */
    public void addMarkersToMap() {
        ArrayMap<Marker, Spot> newMarkerMap = new ArrayMap<>();

        for(int i = 0; i < markerMap.size(); i++) {
            Spot s = markerMap.valueAt(i);

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(mMarkerIcon))
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .title(s.getDisplayName())
                    .position(new LatLng(s.getLocation().getLat(), s.getLocation().getLon())));

            newMarkerMap.put(marker, s);
        }

        markerMap.clear();
        markerMap.putAll((Map<? extends Marker, ? extends Spot>) newMarkerMap);

        newMarkerMap.clear();

        zoomMapAcordingToMarkers();
    }

    /**
     * Zooms map to display all markers.
     */
    public void zoomMapAcordingToMarkers() {
        //zoom to display all markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerMap.keySet()) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        //move camera to calulated point
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 70);
        mGoogleMap.moveCamera(cu);

        //remove progressBar
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Zooms the map to a marker, so that it will be above
     * the MapAdditionFragment.
     *
     * @param marker A Marker from a GoogleMap.
     */
    public void zoomMapToMarker(Marker marker) {
        //move camera to display marker above the mapAddtionFragment
        Projection projection = mGoogleMap.getProjection();

        LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude);
        Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
        Point pointHalfScreenAbove = new Point(markerScreenPosition.x,
                markerScreenPosition.y + (this.getResources().getDisplayMetrics().heightPixels / 5));

        LatLng aboveMarkerLatLng = projection
                .fromScreenLocation(pointHalfScreenAbove);

        marker.showInfoWindow();
        CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
        mGoogleMap.animateCamera(center);
    }

    /**
     * Displays a marker connected to a spot.
     *
     * @param spot A {@link com.xamoom.android.mapping.Spot}.
     */
    public void displayMarker(Spot spot) {
        Set<Marker> markerSet = markerMap.keySet();

        for (Marker marker : markerSet) {
            if(marker.getTitle().equalsIgnoreCase(spot.getDisplayName())) {
                mViewPager.setCurrentItem(0);
                marker.showInfoWindow();
                openMapAdditionFragment(spot);

                zoomMapToMarker(marker);
            }
        }
    }

    /**
     * Decodes a base64 string to an icon for mapMarkers.
     * Can handle normal image formats and also svgs.
     * The icon will be resized to width: 70, height will be resized to maintain imageRatio.
     *
     * @param base64String Base64 string that will be resized. Must start with "data:image/"
     * @return icon as BitMap, or null if there was a problem
     */
    public Bitmap getIconFromBase64(String base64String) {
        Bitmap icon = null;
        byte[] data1;
        byte[] data2 = "".getBytes();
        String decodedString1 = "";
        String decodedString2 = "";
        float newImageWidth = 25.0f;

        //image will be resized depending on the density of the screen
        if(getActivity() != null) {
            newImageWidth = newImageWidth * getResources().getDisplayMetrics().density;
        }

        if (base64String == null)
            return null;

        try {
            //encode 2 times
            data1 = Base64.decode(base64String, Base64.DEFAULT);
            decodedString1 = new String(data1, "UTF-8");

            //get rid of image/xxxx base64,
            int index = decodedString1.indexOf("base64,");
            String decodedString1WithoutPrefix = decodedString1.substring(index + 7);

            data2 = Base64.decode(decodedString1WithoutPrefix, Base64.DEFAULT);
            decodedString2 = new String(data2, "UTF-8");

            if (decodedString1.contains("data:image/svg+xml")) {
                //svg stuff
                SVG svg = null;
                svg = SVG.getFromString(decodedString2);

                if (svg != null) {
                    //resize svg
                    float imageRatio = svg.getDocumentWidth() / svg.getDocumentHeight();
                    svg.setDocumentWidth(newImageWidth);
                    svg.setDocumentHeight(newImageWidth / imageRatio);

                    icon = Bitmap.createBitmap((int) svg.getDocumentWidth(), (int) svg.getDocumentHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(icon);
                    svg.renderToCanvas(canvas1);
                }
            } else if (decodedString1.contains("data:image/")) {
                //normal image stuff
                icon = BitmapFactory.decodeByteArray(data2, 0, data2.length);
                //resize the icon
                double imageRatio = (double) icon.getWidth() / (double) icon.getHeight();
                double newHeight = newImageWidth / imageRatio;
                icon = Bitmap.createScaledBitmap(icon, (int) newImageWidth, (int) newHeight, false);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(getActivity().getResources(), com.xamoom.android.xamoomcontentblocks.R.drawable.ic_default_map_marker);
        } catch (SVGParseException e ) {
            e.printStackTrace();
            BitmapFactory.decodeResource(getActivity().getResources(), com.xamoom.android.xamoomcontentblocks.R.drawable.ic_default_map_marker);
        }

        return icon;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMapFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMapFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * closeGeofenceFragment must be implemented to remove the fragment from activity.
     */
    public interface OnMapFragmentInteractionListener {
        void foundGeofence(String contentId);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
