package com.xamoom.android.xamoom_pingeborg_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private final HashMap<Marker, Spot> markerMap = new HashMap<Marker, Spot>();
    private MapAdditionFragment mMapAdditionFragment;
    private GeofenceFragment mGeofenceFragment;
    private Marker mActiveMarker;
    private BestLocationProvider mBestLocationProvider;
    private ViewPager mViewPager;

    public static MapActivityFragment newInstance() {
        MapActivityFragment mapActivityFragment = new MapActivityFragment();
        return mapActivityFragment;
    }

    public MapActivityFragment() {
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

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
        //setupMapFragment();
        setupLocation();

        return view;
    }

    private void setupLocation() {
        mBestLocationProvider = new BestLocationProvider(getActivity(), true, true, 10000, 10000, 10, 40);

        BestLocationListener mBestLocationListener = new BestLocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Log.i("pingeborg", "onStatusChanged PROVIDER:" + provider + " STATUS:" + String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                 //Log.i("pingeborg", "onProviderEnabled PROVIDER:" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                //Log.i("pingeborg", "onProviderDisabled PROVIDER:" + provider);
            }

            @Override
            public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {
                //Log.w("pingeborg", "onLocationUpdateTimeoutExceeded PROVIDER:" + type);
            }

            @Override
            public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
                if(isFresh) {
                    Log.i("pingeborg", "onLocationUpdate TYPE:" + type + " Location:" + mBestLocationProvider.locationToString(location));
                    setupGeofencing(location);
                }
            }
        };

        //start Location Updates
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupViewPager(ViewPager viewPager) {
        mSupportMapFragment = SupportMapFragment.newInstance();

        FragmentManager fragmentManager = getChildFragmentManager();
        Adapter adapter = new Adapter(fragmentManager);
        adapter.addFragment(mSupportMapFragment, "Map");
        adapter.addFragment(SpotListFragment.newInstance(), "List");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != 0) {
                    closeMapAdditionFragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSupportMapFragment.getMapAsync(this);
    }

    public void setupGeofencing(Location location) {
        XamoomEndUserApi.getInstance().getContentByLocation(location.getLatitude(), location.getLongitude(), null, new APICallback<ContentByLocation>() {
            @Override
            public void finished(ContentByLocation result) {
                mBestLocationProvider.stopLocationUpdates();
                if (result.getItems().size() > 0) {
                    Log.v("pingeborg","Hellyeah, got a geofence: " + result.getItems().get(0).getContentName());
                    openGeofenceFragment(result.getItems().get(0));
                }
            }
        });
    }

    public void openGeofenceFragment(ContentByLocationItem content) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(mGeofenceFragment == null)
            fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_top, R.anim.slide_top_bottom);

        mGeofenceFragment = GeofenceFragment.newInstance(content.getContentName(), content.getImagePublicUrl());
        mGeofenceFragment.setSavedGeofence(content);
        //fragmentTransaction.replace(R.id.geofenceFrameLayout, mGeofenceFragment).commit();
    }

    public void onMapReady(GoogleMap googleMap) {
        Log.v("pingeborg", "Mapready");
        mGoogleMap = googleMap;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker == mActiveMarker)
                    return true;

                mActiveMarker = marker;
                openMapAdditionFragment(markerMap.get(marker));

                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeMapAdditionFragment();
            }
        });

        addMarkersToMap(googleMap, markerMap);
        googleMap.setMyLocationEnabled(true);
    }

    private void openMapAdditionFragment(Spot spot) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(mMapAdditionFragment == null)
            fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_top, R.anim.slide_top_bottom);

        mMapAdditionFragment = MapAdditionFragment.newInstance(spot.getDisplayName(), spot.getDescription(), spot.getImage(), spot.getLocation());
        fragmentTransaction.replace(R.id.mapAdditionFrameLayout, mMapAdditionFragment).commit();
    }

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

    private void addMarkersToMap(final GoogleMap googleMap, final HashMap<Marker, Spot> markerMap) {
        XamoomEndUserApi.getInstance().getSpotMap(null, new String[]{"showAllTheSpots"}, null, new APICallback<SpotMap>() {
            @Override
            public void finished(SpotMap result) {
                Bitmap icon;
                if (result.getStyle().getCustomMarker() != null) {
                    String iconString = result.getStyle().getCustomMarker();
                    icon = getIconFromBase64(iconString);
                } else {
                    icon = BitmapFactory.decodeResource(getActivity().getResources(), com.xamoom.android.xamoomcontentblocks.R.drawable.ic_default_map_marker);
                    float imageRatio = (float) icon.getWidth() / (float) icon.getHeight();
                    icon = Bitmap.createScaledBitmap(icon, 70, (int) (70 / imageRatio), false);
                }

                //show all markers
                for (Spot s : result.getItems()) {
                    final Marker marker = googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .title(s.getDisplayName())
                            .position(new LatLng(s.getLocation().getLat(), s.getLocation().getLon())));

                    markerMap.put(marker, s);
                }

                //zoom to display all markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markerMap.keySet()) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 70);
                googleMap.moveCamera(cu);
            }
        });
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
                    Log.v("pingeborg", "HELLYEAH SVG: " + svg);

                    //resize svg
                    float imageRatio = svg.getDocumentWidth() / svg.getDocumentHeight();
                    svg.setDocumentWidth(70.0f);
                    svg.setDocumentHeight(70 / imageRatio);

                    icon = Bitmap.createBitmap((int) svg.getDocumentWidth(), (int) svg.getDocumentHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(icon);
                    svg.renderToCanvas(canvas1);
                }
            } else if (decodedString1.contains("data:image/")) {
                //normal image stuff
                icon = BitmapFactory.decodeByteArray(data2, 0, data2.length);
                //resize the icon
                double imageRatio = (double) icon.getWidth() / (double) icon.getHeight();
                double newHeight = 70.0 / imageRatio;
                icon = Bitmap.createScaledBitmap(icon, 70, (int) newHeight, false);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SVGParseException e ) {
            e.printStackTrace();
        }

        return icon;
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
