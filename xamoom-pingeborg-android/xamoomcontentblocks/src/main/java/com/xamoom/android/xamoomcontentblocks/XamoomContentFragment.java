package com.xamoom.android.xamoomcontentblocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType6;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;

import java.io.IOException;
import java.util.List;


/**
 * XamoomContentBlock is a helper for everyone to display the different contentBlocks delivered
 * from the xamoom cloud.
 *
 * ATM there are nine different contentBlocks displaying different contents from the customer.
 * In your app you can display the contentBlocks like you want, we decided to display them
 * like on our website (https://www.xm.gl).
 *
 * To get started with the XamoomContentFragment create a new Instance via {@link #newInstance(String, String)}.
 * You also have to implement {@link com.xamoom.android.xamoomcontentblocks.XamoomContentFragment.OnXamoomContentFragmentInteractionListener}.
 *
 * @author Raphael Seher
 *
 * @version 0.1
 *
 */
public class XamoomContentFragment extends Fragment {


    public static final String XAMOOM_CONTENT_ID = "xamoomContentId";
    public static final String XAMOOM_LOCATION_IDENTIFIER = "xamoomLocationIdentifier";

    private static final String YOUTUBE_API_KEY = "youtubeApiKeyParam";
    private static final String LINK_COLOR_KEY = "LinkColorKeyParam";

    private RecyclerView mRecyclerView;
    private ContentBlockAdapter mContentBlockAdapter;

    private List<ContentBlock> mContentBlocks;
    private String mContentId;
    private String mLocationIdentifier;
    private String mYoutubeApiKey;
    private String mLinkColor;

    private OnXamoomContentFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance.
     * You also need a youtube api to display youtube videos.
     * You can set a special linkcolor as hex. (e.g. "00F")
     *
     * @param youtubeApiKey Youtube API key from the google play store.
     * @param linkColor LinkColor as hex (e.g. "00F"), will be blue if null
     * @return XamoomContentFragment Returns an Instance of XamoomContentFragment
     */
    public static XamoomContentFragment newInstance(String youtubeApiKey, String linkColor) {
        XamoomContentFragment fragment = new XamoomContentFragment();
        Bundle args = new Bundle();
        args.putString(YOUTUBE_API_KEY, youtubeApiKey);

        if(linkColor == null)
            linkColor = "00F";
        args.putString(LINK_COLOR_KEY, linkColor);
        fragment.setArguments(args);
        return fragment;
    }

    public XamoomContentFragment() {
        // Required empty public constructor
    }

    public void setContentBlocks(List<ContentBlock> contentBlocks) {
        this.mContentBlocks = contentBlocks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYoutubeApiKey = getArguments().getString(YOUTUBE_API_KEY);
            mLinkColor = getArguments().getString(LINK_COLOR_KEY);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentBlockAdapter = null;
        mRecyclerView.setAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_xamoom_content, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contentBlocksRecycler);
        setupRecyclerView(mRecyclerView);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContentBlockAdapter = null;
        mRecyclerView.setAdapter(null);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //DISPLAY DATA
        mContentBlockAdapter = new ContentBlockAdapter(this, mContentBlocks, mYoutubeApiKey, mLinkColor);
        mRecyclerView.setAdapter(mContentBlockAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnXamoomContentFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnXamoomContentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Implement OnXamoomContentFragmentInteractionListener and override
     * <code>clickedContentBlock(String)</code>.
     *
     * <code>clickedContentBlock(String)</code> gets called, when somebody clicks
     * a {@link ContentBlockType6} and you have to handle it. Normally you would open
     * a new activity or update the XamoomContentFragment with the passed contentId.
     */
    public interface OnXamoomContentFragmentInteractionListener {
        public void clickedContentBlock(String contentId);
    }

    public void contentBlockClick(String contentId) {
        mListener.clickedContentBlock(contentId);
    }
}