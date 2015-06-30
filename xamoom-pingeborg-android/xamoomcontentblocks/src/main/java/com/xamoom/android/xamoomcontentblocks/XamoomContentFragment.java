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
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;

import java.io.IOException;
import java.util.List;


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

    private static final String YOUTUBE_API_KEY = "youtubeApiKeyParam";

    private RecyclerView mRecyclerView;
    private ContentBlockAdapter mContentBlockAdapter;

    private List<ContentBlock> mContentBlocks;
    private String mContentId;
    private String mLocationIdentifier;
    private String mYoutubeApiKey;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * //TODO: comment
     * @param contentId contentId.
     * @return A new instance of fragment XamoomContentFragment.
     */
    public static XamoomContentFragment newInstance(List<ContentBlock> contentBlocks, String youtubeApiKey) {
        XamoomContentFragment fragment = new XamoomContentFragment();
        Bundle args = new Bundle();
        args.putString(YOUTUBE_API_KEY, youtubeApiKey);
        fragment.setArguments(args);
        return fragment;
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

    public void setContentBlocks(List<ContentBlock> contentBlocks) {
        this.mContentBlocks = contentBlocks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mYoutubeApiKey = getArguments().getString(YOUTUBE_API_KEY);
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
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_xamoom_content, container, false);
        setupRecyclerView(mRecyclerView);

        return mRecyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //DISPLAY DATA
        mContentBlockAdapter = new ContentBlockAdapter(this, mContentBlocks, mYoutubeApiKey);
        mRecyclerView.setAdapter(mContentBlockAdapter);
    }

}
