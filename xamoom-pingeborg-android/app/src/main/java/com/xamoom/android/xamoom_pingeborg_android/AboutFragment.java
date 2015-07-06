package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // private static final String ARG_PARAM1 = "param1";
    // private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    // private String mParam1;
    // private String mParam2;

    // private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        setupXamoomContentFragment();
        return view;
    }

    public void setupXamoomContentFragment () {
        XamoomEndUserApi.getInstance().getContentById("f0da3d3d28d3418e9ccc4a6e9b3493c0", false, false, null, new APICallback<ContentById>() {
            @Override
            public void finished(ContentById result) {

                ContentBlockType0 title = new ContentBlockType0(result.getContent().getTitle(),true, 0, result.getContent().getDescriptionOfContent());
                ContentBlockType3 image = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());

                result.getContent().getContentBlocks().add(0, title);
                result.getContent().getContentBlocks().add(1, image);

                XamoomContentFragment fragment = XamoomContentFragment.newInstance(null, Config.YOUTUBE_API_KEY);
                fragment.setContentBlocks(result.getContent().getContentBlocks());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.aboutContentFrameLayout, fragment).commit();
            }
        });
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

}
