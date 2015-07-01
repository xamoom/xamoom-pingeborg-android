package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caverock.androidsvg.SVG;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.xamoom.android.mapping.Spot;
import com.xamoom.android.xamoomcontentblocks.SvgDecoder;
import com.xamoom.android.xamoomcontentblocks.SvgDrawableTranscoder;
import com.xamoom.android.xamoomcontentblocks.SvgSoftwareLayerSetter;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by raphaelseher on 01.07.15.
 */
public class MapPopoverWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;
    private HashMap<Marker, Spot> mMarkerMap;
    final HashMap<Marker, Drawable> mImageMap;
    private ImageView spotImageView;

    MapPopoverWindowAdapter(Context context, HashMap<Marker, Spot> markerMap,  HashMap<Marker, Drawable> imageMap) {
        mContext = context;
        mMarkerMap = markerMap;
        mImageMap = imageMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Spot spot = mMarkerMap.get(marker);
        final View popup = View.inflate(mContext, R.layout.map_popover,null);

        TextView titleTextView = (TextView) popup.findViewById(R.id.titleTextView);
        titleTextView.setText(spot.getDisplayName());

        spotImageView = (ImageView) popup.findViewById(R.id.spotImageView);
        Drawable image = mImageMap.get(marker);
        //spotImageView.setMinimumHeight(image.getIntrinsicHeight()/(image.getIntrinsicWidth()/image.getIntrinsicHeight()));
        spotImageView.setImageDrawable(mImageMap.get(marker));

        TextView descriptionTextView = (TextView) popup.findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(spot.getDescription());
        return popup;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Spot spot = mMarkerMap.get(marker);
        Glide.with(mContext)
                .load(spot.getImage())
                .fitCenter()
                .placeholder(R.drawable.placeholder)
                .into(spotImageView);

        return null;
    }
}
