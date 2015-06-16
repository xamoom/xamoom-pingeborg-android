package com.xamoom.android.xamoomcontentblocks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.mapping.ContentList;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link XamoomContentFragment.OnXamoomContentBlocksFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link XamoomContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class XamoomContentFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONTENT_ID = "contentIdParam";
    private static final String LOCATION_IDENTIFIER = "locationIdentifierParam";

    private RecyclerView mRecyclerView;

    private String mContentId;
    private String mLocationIdentifier;

    private OnXamoomContentBlocksFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentId contentId.
     * @return A new instance of fragment XamoomContentFragment.
     */
    public static XamoomContentFragment newInstance(String contentId, String locationIdentifier) {
        XamoomContentFragment fragment = new XamoomContentFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT_ID, contentId);
        args.putString(LOCATION_IDENTIFIER, locationIdentifier);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadFromContentId(String contentId) {
        XamoomEndUserApi.getInstance().getContentById(contentId, false, false, null, new APICallback<ContentById>() {
            @Override
            public void finished(ContentById result) {
                Log.v("pingeborg", "Hellyeah: " + result);

                //DISPLAY DATA
                mRecyclerView.setAdapter(new ContentBlockAdapter(getActivity(), result.getContent().getContentBlocks()));
            }
        });
    }

    private void loadFromLocationIdentifier(String locationIdentifier) {
        XamoomEndUserApi.getInstance().getContentByLocationIdentifier(locationIdentifier, false, false, null, new APICallback<ContentByLocationIdentifier>() {
            @Override
            public void finished(ContentByLocationIdentifier result) {
                Log.v("pingeborg", "Hellyeah: " + result);
            }
        });
    }

    public XamoomContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContentId = getArguments().getString(CONTENT_ID);
            mLocationIdentifier = getArguments().getString(LOCATION_IDENTIFIER);

            if(mContentId != null)
                loadFromContentId(mContentId);
            else if (mLocationIdentifier != null)
                loadFromLocationIdentifier(mLocationIdentifier);
            else
                try {
                    throw new IOException("No identifier");
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_xamoom_content, container, false);
        setupRecyclerView(mRecyclerView);
        return mRecyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnXamoomContentBlocksFragmentInteractionListener) activity;
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
    public interface OnXamoomContentBlocksFragmentInteractionListener {
        // TODO: Update argument type and name

    }

}
