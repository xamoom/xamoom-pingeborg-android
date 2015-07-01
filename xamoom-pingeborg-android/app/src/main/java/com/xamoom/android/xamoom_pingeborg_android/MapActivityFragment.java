package com.xamoom.android.xamoom_pingeborg_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Spot;
import com.xamoom.android.mapping.SpotMap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mSupportMapFragment;

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
        setupMapFragment();
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setupMapFragment() {
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.pingeborgMap, mSupportMapFragment).commit();
            mSupportMapFragment.getMapAsync(this);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        Log.v("pingeborg", "Mapready");

        final HashMap<Marker, Spot> markerMap = new HashMap<Marker, Spot>();
        final HashMap<Marker, Drawable> imageMap = new HashMap<Marker, Drawable>();

        addMarkersToMap(googleMap,imageMap, markerMap);
        setupInfoWindow(googleMap, imageMap, markerMap);
        googleMap.setMyLocationEnabled(true);
    }

    private void setupInfoWindow(GoogleMap googleMap, HashMap<Marker, Drawable> imageMap, HashMap<Marker, Spot> markerMap) {
        googleMap.setInfoWindowAdapter(new MapPopoverWindowAdapter(getActivity().getApplicationContext(), markerMap, imageMap));
    }

    private void addMarkersToMap(final GoogleMap googleMap, final HashMap<Marker, Drawable> imageMap, final HashMap<Marker, Spot> markerMap) {
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

                    Glide.with(getActivity())
                            .load(s.getImage())
                            .into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    imageMap.put(marker, resource);
                                }
                            });

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
}
