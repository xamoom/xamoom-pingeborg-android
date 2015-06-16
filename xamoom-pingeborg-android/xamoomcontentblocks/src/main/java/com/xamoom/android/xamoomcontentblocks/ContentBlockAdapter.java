package com.xamoom.android.xamoomcontentblocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;

import java.util.List;

/**
 * Created by raphaelseher on 16.06.15.
 */
public class ContentBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContentBlock> mContentBlocks;

    public ContentBlockAdapter(Context context, List<ContentBlock> contentBlocks) {
        mContext = context;
        mContentBlocks = contentBlocks;
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
                        .inflate(R.layout.content_block_1_layout, parent, false);
                return new ContentBlock0ViewHolder(view);
            case 1:
                View view1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock1ViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock2ViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_3_layout, parent, false);
                return new ContentBlock3ViewHolder(view3, mContext);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ContentBlock4ViewHolder(view4);
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
                Log.v("pingeborg", "Hellyeah");
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock2ViewHolder":
                Log.v("pingeborg", "Hellyeah");
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock3ViewHolder":
                ContentBlockType3 cb3 = (ContentBlockType3)cb;
                ContentBlock3ViewHolder newHolder3 = (ContentBlock3ViewHolder)  holder;

                newHolder3.setupContentBlock(cb3);
                break;
            case "class com.xamoom.android.xamoomcontentblocks.ContentBlock4ViewHolder":
                Log.v("pingeborg", "Hellyeah");
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
            mTitleTextView.setHeight(0);

        if(cb0.getText() != null)
            mContentTextView.setText(Html.fromHtml(cb0.getText()));
        else
            mContentTextView.setHeight(0);
    }
}


class ContentBlock1ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock1ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 1");
    }
}

class ContentBlock2ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock2ViewHolder(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 2");
    }
}


class ContentBlock3ViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    public TextView mTitleTextView;
    public ImageView mImageView;

    public ContentBlock3ViewHolder(View itemView, Context context) {
        super(itemView);

        mContext = context;
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mImageView = (ImageView) itemView.findViewById(R.id.imageImageView);
    }

    public void setupContentBlock(ContentBlockType3 cb3) {
        if(cb3.getTitle() != null)
            mTitleTextView.setText(cb3.getTitle());
        else
            mTitleTextView.setHeight(0);

        if(cb3.getFileId() != null) {
            Picasso.with(mContext).load(cb3.getFileId()).into(mImageView);
        }
    }
}

class ContentBlock4ViewHolder extends RecyclerView.ViewHolder {

    public ContentBlock4ViewHolder(View itemView) {
        super(itemView);
        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 4");
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