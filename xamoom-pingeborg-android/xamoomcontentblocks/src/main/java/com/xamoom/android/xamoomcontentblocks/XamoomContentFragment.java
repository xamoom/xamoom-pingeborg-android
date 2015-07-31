package com.xamoom.android.xamoomcontentblocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType6;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.mapping.Menu;
import com.xamoom.android.mapping.Style;

import java.io.IOException;
import java.util.LinkedList;
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

    private Content mContent;
    private List<ContentBlock> mContentBlocks;
    private Style mStyle;
    private Menu mMenu;
    private String mYoutubeApiKey;
    private String mLinkColor;

    private boolean isAnimated = false;

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

    public void setContent(Content content) {
        this.mContent = content;
    }

    public void setMenu(Menu mMenu) {
        this.mMenu = mMenu;
    }

    public void setStyle(Style mStyle) {
        this.mStyle = mStyle;
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
        Log.v("pingeborg.xamoom.com", "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_xamoom_content, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contentBlocksRecycler);
        addContentTitleAndImage();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("pingeborg.xamoom.com", "onStart");
        if(!isAnimated) {
            setupRecyclerView();
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Log.v("pingeborg.xamoom.com", "onCreateAnimation");

        if (nextAnim != 0) {
            isAnimated = true;
            Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.d("pingeborg.xamoom.com", "Animation ended.");
                    setupRecyclerView();
                }
            });

            return anim;
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void addContentTitleAndImage() {
        ContentBlockType3 cb3 = new ContentBlockType3(null, true, 3, mContent.getImagePublicUrl());
        ContentBlockType0 cb0 = new ContentBlockType0(mContent.getTitle(), true, 0, mContent.getDescriptionOfContent());

        mContentBlocks = new LinkedList<ContentBlock>();
        mContentBlocks.addAll(mContent.getContentBlocks());
        mContentBlocks.add(0, cb3);
        mContentBlocks.add(0, cb0);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new PreCachingLayoutManager(mRecyclerView.getContext()));

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
        public void clickedContentBlock(Content content);
    }

    public void contentBlockClick(Content content) {
        mListener.clickedContentBlock(content);
    }

    public class PreCachingLayoutManager extends LinearLayoutManager {
        private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
        private int extraLayoutSpace = -1;
        private Context context;

        public PreCachingLayoutManager(Context context) {
            super(context);
            this.context = context;
        }

        public PreCachingLayoutManager(Context context, int extraLayoutSpace) {
            super(context);
            this.context = context;
            this.extraLayoutSpace = extraLayoutSpace;
        }

        public PreCachingLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.context = context;
        }

        public void setExtraLayoutSpace(int extraLayoutSpace) {
            this.extraLayoutSpace = extraLayoutSpace;
        }

        @Override
        protected int getExtraLayoutSpace(RecyclerView.State state) {
            if (extraLayoutSpace > 0) {
                return extraLayoutSpace;
            }
            return DEFAULT_EXTRA_LAYOUT_SPACE;
        }
    }
}

