package com.xamoom.android.xamoom_pingeborg_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment implements OnMapReadyCallback {

    private MapFragment mMapFragment;

    public static MapActivityFragment newInstance() {
        MapActivityFragment mapActivityFragment = new MapActivityFragment();
        return mapActivityFragment;
    }

    public MapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.pingeborgMap);
        mMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.v("pingeborg", "Mapready");

        final ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

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
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .title(s.getDisplayName())
                            .position(new LatLng(s.getLocation().getLat(), s.getLocation().getLon())));

                    mMarkerArray.add(marker);
                }

                //zoom to display all markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : mMarkerArray) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                //TODO: set padding to markersize
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
                icon = Bitmap.createScaledBitmap(icon, 100, (int) newHeight, false);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SVGParseException e ) {
            e.printStackTrace();
        }

        return icon;
    }
}
