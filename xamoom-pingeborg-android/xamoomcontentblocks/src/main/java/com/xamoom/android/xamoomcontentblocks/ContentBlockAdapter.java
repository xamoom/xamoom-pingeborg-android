package com.xamoom.android.xamoomcontentblocks;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType1;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType2;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType4;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raphaelseher on 16.06.15.
 */
public class ContentBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mParentActivity;
    private List<ContentBlock> mContentBlocks;
    private String mYoutubeApiKey;

    public ContentBlockAdapter(Activity context, List<ContentBlock> contentBlocks, String youtubeApiKey) {
        mParentActivity = context;
        mContentBlocks = contentBlocks;
        mYoutubeApiKey = youtubeApiKey;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return mContentBlocks.get(position).getContentBlockType();
    }

    @Override
    public int getItemCount() {
        return mContentBlocks.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_0_layout, parent, false);
                return new ContentBlock0ViewHolder(view);
            case 1:
                View view1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_1_layout, parent, false);
                return new ContentBlock1ViewHolder(view1, mParentActivity);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_2_layout, parent, false);
                return new ContentBlock2ViewHolder(view2, mParentActivity, mYoutubeApiKey);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_3_layout, parent, false);
                return new ContentBlock3ViewHolder(view3, mParentActivity);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_4_layout, parent, false);
                return new ContentBlock4ViewHolder(view4, mParentActivity);
            case 5:
                View view5 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock5ViewHolder(view5);
            case 6:
                View view6 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock6ViewHolder(view6);
            case 7:
                View view7 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock7ViewHolder(view7);
            case 8:
                View view8 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock8ViewHolder(view8);
            case 9:
                View view9 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock9ViewHolder(view9);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentBlock cb = mContentBlocks.get(position);

        switch (holder.getClass().toString()) {
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock0ViewHolder":
                ContentBlockType0 cb0 = (ContentBlockType0)cb;
                ContentBlock0ViewHolder newHolder = (ContentBlock0ViewHolder) holder;
                newHolder.setupContentBlock(cb0);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock1ViewHolder":
                ContentBlockType1 cb1 = (ContentBlockType1)cb;
                ContentBlock1ViewHolder newHolder1 = (ContentBlock1ViewHolder) holder;
                newHolder1.setupContentBlock(cb1);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock2ViewHolder":
                ContentBlockType2 cb2 = (ContentBlockType2)cb;
                ContentBlock2ViewHolder newHolder2 = (ContentBlock2ViewHolder) holder;
                newHolder2.setupContentBlock(cb2);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock3ViewHolder":
                ContentBlockType3 cb3 = (ContentBlockType3)cb;
                ContentBlock3ViewHolder newHolder3 = (ContentBlock3ViewHolder) holder;
                newHolder3.setupContentBlock(cb3);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock4ViewHolder":
                ContentBlockType4 cb4 = (ContentBlockType4) cb;
                ContentBlock4ViewHolder newHolder4 = (ContentBlock4ViewHolder) holder;
                newHolder4.setupContentBlock(cb4);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock5ViewHolder":
                Log.v("pingeborg", "Hellyeah");
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock6ViewHolder":
                Log.v("pingeborg", "Hellyeah");
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock7ViewHolder":
                Log.v("pingeborg", "Hellyeah");
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock8ViewHolder":
                Log.v("pingeborg", "Hellyeah");
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock9ViewHolder":
                Log.v("pingeborg", "Hellyeah");
                break;
        }
    }
}

class ContentBlock0ViewHolder extends RecyclerView.ViewHolder {

    public TextView mTitleTextView;
    public TextView mContentTextView;

    public ContentBlock0ViewHolder(View itemView) {
        super(itemView);
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mContentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
    }

    public void setupContentBlock(ContentBlockType0 cb0){
        if(cb0.getTitle() != null)
            mTitleTextView.setText(cb0.getTitle());
        else
            mTitleTextView.setText(null);

        if(cb0.getText() != null)
            mContentTextView.setText(Html.fromHtml(cb0.getText()));
        else
            mContentTextView.setText(null);
    }
}

class ContentBlock1ViewHolder extends RecyclerView.ViewHolder {

    private Activity mParentActivity;
    public TextView mTitleTextView;
    public TextView mArtistTextView;
    public Button mPlayPauseButton;
    public MediaPlayer mMediaPlayer;

    public ContentBlock1ViewHolder(View itemView, Activity activity) {
        super(itemView);
        mParentActivity = activity;
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mArtistTextView = (TextView) itemView.findViewById(R.id.artistTextView);
        mPlayPauseButton = (Button) itemView.findViewById(R.id.playPauseButton);
    }

    public void setupContentBlock(ContentBlockType1 cb1) {
        if(cb1.getTitle() != null)
            mTitleTextView.setText(cb1.getTitle());
        else
            mTitleTextView.setText(null);

        if(cb1.getArtist() != null)
            mArtistTextView.setText(cb1.getArtist());
        else
            mArtistTextView.setText(null);

        if(cb1.getFileId() != null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(mParentActivity, Uri.parse(cb1.getFileId()));
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    } else {
                        mMediaPlayer.start();
                    }
                }
            });
        }
    }
}

class ContentBlock2ViewHolder extends RecyclerView.ViewHolder implements YouTubeThumbnailView.OnInitializedListener {

    final static String reg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";

    private Activity mParentActivity;
    public TextView mTitleTextView;
    public YouTubeThumbnailView mYoutubeThumbnail;
    public String mYoutubeVideoCode;
    public String mYoutubeApiKey;

    public ContentBlock2ViewHolder(View itemView, Activity activity, String youtubeApiKey) {
        super(itemView);
        mParentActivity = activity;
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mYoutubeThumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.youtubeThumbnailView);
        mYoutubeApiKey = youtubeApiKey;
    }

    public void setupContentBlock(ContentBlockType2 cb2) {
        mYoutubeVideoCode = getVideoId(cb2.getYoutubeUrl());

        if(cb2.getTitle() != null)
            mTitleTextView.setText(cb2.getTitle());
        else
            mTitleTextView.setText(null);

        if(mYoutubeThumbnail != null)
            mYoutubeThumbnail.initialize(mYoutubeApiKey, this);
    }

    public static String getVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0)
            return null;

        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);

        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        youTubeThumbnailLoader.setVideo(mYoutubeVideoCode);
        youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open video in YoutubeStandalonePlayer
                        Intent intent = YouTubeStandalonePlayer.createVideoIntent(mParentActivity, mYoutubeApiKey, mYoutubeVideoCode);
                        mParentActivity.startActivity(intent);
                    }
                });
            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                //TODO: Notify user
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
        //TODO: Notify user
    }
}

class ContentBlock3ViewHolder extends RecyclerView.ViewHolder {

    private Activity mActivity;
    public TextView mTitleTextView;
    public ImageView mImageView;

    public ContentBlock3ViewHolder(View itemView, Activity activity) {
        super(itemView);
        mActivity = activity;
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mImageView = (ImageView) itemView.findViewById(R.id.imageImageView);
    }

    public void setupContentBlock(ContentBlockType3 cb3) {
        if(cb3.getTitle() != null)
            mTitleTextView.setText(cb3.getTitle());
        else
            mTitleTextView.setText(null);

        if(cb3.getFileId() != null) {
            Glide.with(mActivity)
                    .load(cb3.getFileId())
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
        }
    }
}

class ContentBlock4ViewHolder extends RecyclerView.ViewHolder {

    private Activity mActivity;
    private LinearLayout mRootLayout;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private ImageView mIcon;

    public ContentBlock4ViewHolder(View itemView, Activity activity) {
        super(itemView);
        mActivity = activity;
        mRootLayout = (LinearLayout) itemView.findViewById(R.id.linkBlockLinearLayout);
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mContentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
        mIcon = (ImageView) itemView.findViewById(R.id.iconImageView);
    }

    public void setupContentBlock(final ContentBlockType4 cb4) {
        if(cb4.getTitle() != null)
            mTitleTextView.setText(cb4.getTitle());
        else
            mTitleTextView.setText(null);

        if(cb4.getText() != null)
            mContentTextView.setText(cb4.getText());
        else
            mContentTextView.setText(null);

        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(cb4.getLinkUrl()));
                mActivity.startActivity(i);
            }
        });

        switch (cb4.getLinkType()) {
            case 0:
                break;
            case 3:
                mRootLayout.setBackgroundResource(R.color.amazon_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_cart);
                mTitleTextView.setTextColor(Color.BLACK);
                mContentTextView.setTextColor(Color.BLACK);
                break;
            default:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.abc_ic_voice_search_api_mtrl_alpha);
                mTitleTextView.setTextColor(Color.parseColor("#333333"));
                mContentTextView.setTextColor(Color.parseColor("#333333"));
                break;
        }
    }
}

class ContentBlock5ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock5ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 5");
    }
}

class ContentBlock6ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock6ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 6");
    }
}

class ContentBlock7ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock7ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 7");
    }
}

class ContentBlock8ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock8ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 8");
    }
}

class ContentBlock9ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock9ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 9");
    }
}