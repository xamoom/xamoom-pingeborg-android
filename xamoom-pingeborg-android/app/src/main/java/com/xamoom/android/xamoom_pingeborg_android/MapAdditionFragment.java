package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xamoom.android.request.Location;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MapAdditionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapAdditionFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "spotNameParam";
    private static final String ARG_PARAM2 = "spotDescriptionParam";
    private static final String ARG_PARAM3 = "spotImageUrlParam";
    private static final String ARG_PARAM4 = "spotLocationLatitudeParam";
    private static final String ARG_PARAM5 = "spotLocationLongitudeParam";

    // TODO: Rename and change types of parameters
    private String mSpotName;
    private String mSpotDescription;
    private String mSpotImageUrl;
    private Location mSpotLocation;

    //private OnFragmentInteractionListener mListener;


    public static MapAdditionFragment newInstance(String name, String description, String imageUrl, Location location) {
        MapAdditionFragment fragment = new MapAdditionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, imageUrl);
        args.putDouble(ARG_PARAM4, location.getLat());
        args.putDouble(ARG_PARAM5, location.getLon());
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_addition, container, false);
        TextView tx = (TextView) view.findViewById(R.id.spotDescriptionTextView);
        tx.setText(mSpotDescription);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mSpotName);

        final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        Glide.with(this)
                .load(mSpotImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(imageView);

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

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */
    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    */
}
