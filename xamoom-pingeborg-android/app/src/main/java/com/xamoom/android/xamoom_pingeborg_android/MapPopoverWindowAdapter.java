package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.xamoom.android.mapping.Spot;

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
