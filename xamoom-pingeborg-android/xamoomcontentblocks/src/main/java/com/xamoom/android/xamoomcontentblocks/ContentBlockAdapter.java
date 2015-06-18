package com.xamoom.android.xamoomcontentblocks;

import android.app.Activity;
import android.content.Context;
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
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType1;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType2;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType4;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType5;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType6;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType7;
import com.xamoom.android.mapping.ContentById;

import java.io.IOException;
import java.util.List;
import java.util.NavigableSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raphaelseher on 16.06.15.
 */
public class ContentBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mParentActivity;
    private List<ContentBlock> mContentBlocks;
    private String mYoutubeApiKey;
    private XamoomContentFragment.OnXamoomContentBlocksFragmentInteractionListener mListener;

    public ContentBlockAdapter(Activity context, List<ContentBlock> contentBlocks, String youtubeApiKey, XamoomContentFragment.OnXamoomContentBlocksFragmentInteractionListener listener) {
        mParentActivity = context;
        mListener = listener;
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
                        .inflate(R.layout.content_block_5_layout, parent, false);
                return new ContentBlock5ViewHolder(view5, mParentActivity);
            case 6:
                View view6 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_6_layout, parent, false);
                return new ContentBlock6ViewHolder(view6, mParentActivity, mListener);
            case 7:
                View view7 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_7_layout, parent, false);
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
                ContentBlockType5 cb5 = (ContentBlockType5) cb;
                ContentBlock5ViewHolder newHolder5 = (ContentBlock5ViewHolder) holder;
                newHolder5.setupContentBlock(cb5);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock6ViewHolder":
                ContentBlockType6 cb6 = (ContentBlockType6) cb;
                ContentBlock6ViewHolder newHolder6 = (ContentBlock6ViewHolder) holder;
                newHolder6.setupContentBlock(cb6);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock7ViewHolder":
                ContentBlockType7 cb7 = (ContentBlockType7) cb;
                ContentBlock7ViewHolder newHolder7 = (ContentBlock7ViewHolder) holder;
                newHolder7.setupContentBlock(cb7);
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

/**
 * TextBlock
 */
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

/**
 * AudioBlock
 */
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

/**
 * YoutubeBlock
 */
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

/**
 * ImageBlock
 */
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

/**
 * LinkBlock
 */
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
                mRootLayout.setBackgroundResource(R.color.facebook_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_facebook);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 1:
                mRootLayout.setBackgroundResource(R.color.twitter_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_twitter);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 2:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_web);
                mTitleTextView.setTextColor(Color.parseColor("#333333"));
                mContentTextView.setTextColor(Color.parseColor("#333333"));
                break;
            case 3:
                mRootLayout.setBackgroundResource(R.color.amazon_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_cart);
                mTitleTextView.setTextColor(Color.BLACK);
                mContentTextView.setTextColor(Color.BLACK);
                break;
            case 4:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_wikipedia);
                mTitleTextView.setTextColor(Color.BLACK);
                mContentTextView.setTextColor(Color.BLACK);
                break;
            case 5:
                mRootLayout.setBackgroundResource(R.color.linkedin_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_linkedin_box);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 6:
                mRootLayout.setBackgroundResource(R.color.flickr_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_flickr9);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 7:
                mRootLayout.setBackgroundResource(R.color.soundcloud_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_soundcloud);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 8:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_itunes);
                mTitleTextView.setTextColor(Color.parseColor("#333333"));
                mContentTextView.setTextColor(Color.parseColor("#333333"));
                break;
            case 9:
                mRootLayout.setBackgroundResource(R.color.youtube_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_youtube_play);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 10:
                mRootLayout.setBackgroundResource(R.color.googleplus_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_google_plus);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 11:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_phone);
                mTitleTextView.setTextColor(Color.parseColor("#333333"));
                mContentTextView.setTextColor(Color.parseColor("#333333"));
                break;
            case 12:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_email);
                mTitleTextView.setTextColor(Color.parseColor("#333333"));
                mContentTextView.setTextColor(Color.parseColor("#333333"));
                break;
            case 13:
                mRootLayout.setBackgroundResource(R.color.spotify_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_spotify);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            case 14:
                mRootLayout.setBackgroundResource(R.color.googlemaps_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_navigation);
                mTitleTextView.setTextColor(Color.WHITE);
                mContentTextView.setTextColor(Color.WHITE);
                break;
            default:
                mRootLayout.setBackgroundResource(R.color.default_linkblock_background_color);
                mIcon.setImageResource(R.drawable.ic_web);
                mTitleTextView.setTextColor(Color.parseColor("#333333"));
                mContentTextView.setTextColor(Color.parseColor("#333333"));
                break;
        }
    }
}

/**
 * EbookBlock
 */
class ContentBlock5ViewHolder extends RecyclerView.ViewHolder {

    private Activity mActivity;
    private LinearLayout mRootLayout;
    private TextView mTitleTextView;
    private TextView mContentTextView;

    public ContentBlock5ViewHolder(View itemView, Activity activity) {
        super(itemView);
        mActivity = activity;
        mRootLayout = (LinearLayout) itemView.findViewById(R.id.linkBlockLinearLayout);
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mContentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
    }

    public void setupContentBlock(final ContentBlockType5 cb5) {
        if(cb5.getTitle() != null)
            mTitleTextView.setText(cb5.getTitle());
        else
            mTitleTextView.setText(null);

        if(cb5.getArtist() != null)
            mContentTextView.setText(cb5.getArtist());
        else
            mContentTextView.setText(null);

        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(cb5.getFileId()));
                mActivity.startActivity(i);
            }
        });
    }
}

/**
 * ContentBlock
 */
class ContentBlock6ViewHolder extends RecyclerView.ViewHolder {
    private Activity mActivity;
    private TextView mTitleTextView;
    private LinearLayout mRootLayout;
    private ImageView mContentThumbnailImageView;
    private XamoomContentFragment.OnXamoomContentBlocksFragmentInteractionListener mListener;

    public ContentBlock6ViewHolder(View itemView, Activity activity, XamoomContentFragment.OnXamoomContentBlocksFragmentInteractionListener listener) {
        super(itemView);
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mRootLayout = (LinearLayout) itemView.findViewById(R.id.contentBlockLinearLayout);
        mContentThumbnailImageView = (ImageView) itemView.findViewById(R.id.contentThumbnailImageView);
        mActivity = activity;
        mListener = listener;
    }

    public void setupContentBlock(final ContentBlockType6 cb6) {
        if(cb6.getTitle() != null)
            mTitleTextView.setText(cb6.getTitle());
        else
            mTitleTextView.setText(null);

        XamoomEndUserApi.getInstance().getContentById(cb6.getContentId(), false, false, null, new APICallback<ContentById>() {
            @Override
            public void finished(ContentById result) {
                mTitleTextView.setText(result.getContent().getTitle());

                Glide.with(mActivity)
                        .load(result.getContent().getImagePublicUrl())
                        .crossFade()
                        .centerCrop()
                        .into(mContentThumbnailImageView);
            }
        });

        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickContentBlock(cb6.getContentId());
            }
        });
    }
}

/**
 * SoundcloudBlock
 */
class ContentBlock7ViewHolder extends RecyclerView.ViewHolder {

    private TextView mTitleTextView;
    private WebView mSoundCloudWebview;
    private String mSoundCloudHTML = "<body style=\"margin: 0; padding: 0\">" +
            "<iframe width='100%%' height='100%%' scrolling='no'" +
            " frameborder='no' src='https://w.soundcloud.com/player/?url=%s&auto_play=false" +
            "&hide_related=true&show_comments=false&show_comments=false" +
            "&show_user=false&show_reposts=false&sharing=false&download=false&buying=false" +
            "&visual=true'></iframe>" +
            "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script>" +
            "</body>";

    public ContentBlock7ViewHolder(View itemView) {
        super(itemView);
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mSoundCloudWebview = (WebView) itemView.findViewById(R.id.soundcloudWebview);
        WebSettings webSettings = mSoundCloudWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    public void setupContentBlock(ContentBlockType7 cb7) {
        if(cb7.getTitle() != null)
            mTitleTextView.setText(cb7.getTitle());
        else
            mTitleTextView.setText(null);

        String test =  String.format(mSoundCloudHTML, cb7.getSoundcloudUrl());
        mSoundCloudWebview.loadData(test, "text/html", "utf-8");
    }
}

/**
 * DownloadBlock
 */
class ContentBlock8ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock8ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 8");
    }
}

/**
 * SpotMapBlock
 */
class ContentBlock9ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock9ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 9");
    }
}