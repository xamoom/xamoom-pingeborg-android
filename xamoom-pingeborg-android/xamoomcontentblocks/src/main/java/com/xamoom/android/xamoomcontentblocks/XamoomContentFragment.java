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
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link XamoomContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class XamoomContentFragment extends Fragment {

    public static String XAMOOM_CONTENT_ID = "xamoomContentId";
    public static String XAMOOM_LOCATION_IDENTIFIER = "xamoomLocationIdentifier";

    private static final String CONTENT_ID = "contentIdParam";
    private static final String LOCATION_IDENTIFIER = "locationIdentifierParam";
    private static final String YOUTUBE_API_KEY = "youtubeApiKeyParam";

    private RecyclerView mRecyclerView;

    private String mContentId;
    private String mLocationIdentifier;
    private String mYoutubeApiKey;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentId contentId.
     * @return A new instance of fragment XamoomContentFragment.
     */
    public static XamoomContentFragment newInstance(String contentId, String locationIdentifier, String youtubeApiKey) {
        XamoomContentFragment fragment = new XamoomContentFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT_ID, contentId);
        args.putString(LOCATION_IDENTIFIER, locationIdentifier);
        args.putString(YOUTUBE_API_KEY, youtubeApiKey);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadFromContentId(String contentId, final Fragment fragment) {
        XamoomEndUserApi.getInstance().getContentById(contentId, false, false, null, new APICallback<ContentById>() {
            @Override
            public void finished(ContentById result) {
                //create contentBlock1
                ContentBlockType0 cb0 = new ContentBlockType0(result.getContent().getTitle(), true, 0, result.getContent().getDescriptionOfContent());
                result.getContent().getContentBlocks().add(0, cb0);

                //create contentBlock3
                ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                result.getContent().getContentBlocks().add(1, cb3);

                //DISPLAY DATA
                mRecyclerView.setAdapter(new ContentBlockAdapter(fragment, result.getContent().getContentBlocks(), mYoutubeApiKey));
            }
        });
    }

    private void loadFromLocationIdentifier(String locationIdentifier, final Fragment fragment) {
        XamoomEndUserApi.getInstance().getContentByLocationIdentifier(locationIdentifier, false, false, null, new APICallback<ContentByLocationIdentifier>() {
            @Override
            public void finished(ContentByLocationIdentifier result) {
            }
        });
    }

    public void contentBlockClick(String contentId) {
        Context context = this.getActivity().getApplicationContext();
        Intent intent = new Intent(context, this.getActivity().getClass());
        intent.putExtra(XAMOOM_CONTENT_ID,contentId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
            mYoutubeApiKey = getArguments().getString(YOUTUBE_API_KEY);

            if(mContentId != null)
                loadFromContentId(mContentId, this);
            else if (mLocationIdentifier != null)
                loadFromLocationIdentifier(mLocationIdentifier, this);
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

}
