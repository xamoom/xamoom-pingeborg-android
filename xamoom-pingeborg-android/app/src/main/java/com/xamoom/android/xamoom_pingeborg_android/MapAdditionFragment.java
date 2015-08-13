package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xamoom.android.request.Location;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MapAdditionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapAdditionFragment extends android.support.v4.app.Fragment {
    private static final String ARG_PARAM1 = "spotNameParam";
    private static final String ARG_PARAM2 = "spotDescriptionParam";
    private static final String ARG_PARAM3 = "spotImageUrlParam";
    private static final String ARG_PARAM4 = "spotLocationLatitudeParam";
    private static final String ARG_PARAM5 = "spotLocationLongitudeParam";
    private static final String ARG_PARAM6 = "spotUserDistanceParam";

    private String mSpotName;
    private String mSpotDescription;
    private String mSpotImageUrl;
    private Location mSpotLocation;
    private float mDistance;

    /**
     * Create a new Instance of MapAdditionFragment.
     *
     * @param name Name of the spot.
     * @param description Description of the spot.
     * @param imageUrl Url of the image.
     * @param location Location of the spot.
     * @param distance Distance from user to spot.
     * @return A MapAdditionFragment.
     */
    public static MapAdditionFragment newInstance(String name, String description, String imageUrl, Location location, float distance) {
        MapAdditionFragment fragment = new MapAdditionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, imageUrl);
        args.putDouble(ARG_PARAM4, location.getLat());
        args.putDouble(ARG_PARAM5, location.getLon());
        args.putFloat(ARG_PARAM6, distance);
        fragment.setArguments(args);
        return fragment;
    }

    public MapAdditionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSpotName = getArguments().getString(ARG_PARAM1);
            mSpotDescription = getArguments().getString(ARG_PARAM2);
            mSpotImageUrl = getArguments().getString(ARG_PARAM3);
            Double lat, lon;
            lat = getArguments().getDouble(ARG_PARAM4);
            lon = getArguments().getDouble(ARG_PARAM5);
            mSpotLocation = new Location(lat, lon);
            mDistance = getArguments().getFloat(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_addition, container, false);
        TextView tx = (TextView) view.findViewById(R.id.spotDescriptionTextView);

        //set the description
        tx.setText(mSpotDescription);

        //set the title
        TextView titleTextView = (TextView) view.findViewById(R.id.spotTitleTextView);
        titleTextView.setText(mSpotName);

        //set the distance
        TextView distanceTextView = (TextView) view.findViewById(R.id.spotDistanceTextView);
        distanceTextView.setText(String.format("%.0f %s", mDistance, getString(R.string.meterLabel)));

        //load the image
        final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        Glide.with(this)
                .load(mSpotImageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        //fab to navigate
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.navigationFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+mSpotLocation.getLat()+","+mSpotLocation.getLon()));
                startActivity(intent);
            }
        });

        return view;
    }

}
