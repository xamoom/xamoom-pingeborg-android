package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xamoom.android.mapping.ContentByLocationItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeofenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GeofenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeofenceFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "contentTitleParam";
    private static final String ARG_PARAM2 = "contentImageUrlParam";
    private static ContentByLocationItem mSavedGeofence;

    public static void setSavedGeofence(ContentByLocationItem mSavedGeofence) {
        GeofenceFragment.mSavedGeofence = mSavedGeofence;
    }

    private String mContentTitle;
    private String mContentImageUrl;

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param imageUrl Parameter 2.
     * @return A new instance of fragment GeofenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeofenceFragment newInstance(String title, String imageUrl) {
        GeofenceFragment fragment = new GeofenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public GeofenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContentTitle = getArguments().getString(ARG_PARAM1);
            mContentImageUrl = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_geofence, container, false);
        Log.v("pingeborg", "Hellyeah in GeofenceFragment");

        return view;
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
