package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xamoom.android.mapping.ContentByLocationItem;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import org.w3c.dom.Text;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;


/**
 * TODO
 */
public class GeofenceFragment extends android.support.v4.app.Fragment {
    private static final String ARG_PARAM1 = "contentTitleParam";
    private static final String ARG_PARAM2 = "contentImageUrlParam";
    private static final String ARG_PARAM3 = "contentIdParam";
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private static ContentByLocationItem mSavedGeofence;

    private CardView mCardView;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    public static void setSavedGeofence(ContentByLocationItem mSavedGeofence) {
        GeofenceFragment.mSavedGeofence = mSavedGeofence;
    }

    private OnGeofenceFragmentInteractionListener mListener;

    private String mContentTitle;
    private String mContentImageUrl;
    private String mContentId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * TODO
     */
    public static GeofenceFragment newInstance(String title, String imageUrl, String contentId) {
        GeofenceFragment fragment = new GeofenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, imageUrl);
        args.putString(ARG_PARAM3, contentId);
        fragment.setArguments(args);
        return fragment;
    }

    public GeofenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContentTitle = getArguments().getString(ARG_PARAM1);
            mContentImageUrl = getArguments().getString(ARG_PARAM2);
            mContentId = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_geofence, container, false);

        mCardView = (CardView) view.findViewById(R.id.card_view);
        TextView textView = (TextView) view.findViewById(R.id.geofenceTextView);
        textView.setText(mContentTitle);
        ImageView imageView = (ImageView) view.findViewById(R.id.geofenceImage);
        ImageView overlayImageView = (ImageView) view.findViewById(R.id.geofenceOverlayImageView);

        //check if already a unlocked artist
        if(!Global.getInstance().getSavedArtists().contains(mContentId)) {
            Glide.with(mCardView.getContext())
                    .load(mContentImageUrl)
                    .crossFade()
                    .bitmapTransform(new GrayscaleTransformation(Glide.get(mCardView.getContext()).getBitmapPool()))
                    .into(imageView);

            overlayImageView.setVisibility(View.VISIBLE);
            overlayImageView.setImageResource(android.R.color.transparent);

            Glide.with(mCardView.getContext())
                    .load(R.drawable.discoverable)
                    .dontTransform() //or the alpha will be ignored
                    .into(overlayImageView);
        } else {
            overlayImageView.setVisibility(View.GONE);
            Glide.with(getActivity())
                    .load(mContentImageUrl)
                    .into(imageView);
        }

        //create gestureDetector/Listener to make the geofence "fling" able
        gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 1 && mCardView.getVisibility() == View.VISIBLE) {
                    //reset if not flinged
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
                    parms.leftMargin = 16;
                    parms.rightMargin = 16;
                    mCardView.setLayoutParams(parms);
                }
                return gestureDetector.onTouchEvent(event);
            }
        };
        view.setOnTouchListener(gestureListener);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGeofenceFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGeofenceFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * TODO
     */
    public interface OnGeofenceFragmentInteractionListener {
        public void closeGeofenceFragment();
    }

    /**
     * TODO
     */
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;

                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int[] viewLocation = new int[2];
                    mCardView.getLocationInWindow(viewLocation);

                    //animation
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
                    TranslateAnimation ab = new TranslateAnimation( Animation.ABSOLUTE, viewLocation[0], Animation.ABSOLUTE, -1500, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    ab.setFillAfter(true);
                    ab.setDuration(200);

                    ab.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mListener.closeGeofenceFragment();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mCardView.startAnimation(ab);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int[] viewLocation = new int[2];
                    mCardView.getLocationInWindow(viewLocation);

                    //animation
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
                    TranslateAnimation ab = new TranslateAnimation( Animation.ABSOLUTE, viewLocation[0], Animation.ABSOLUTE, 1500, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
                    ab.setFillAfter(true);
                    ab.setDuration(200);

                    ab.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mListener.closeGeofenceFragment();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mCardView.startAnimation(ab);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //element will scroll right and left, if you want
            RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
            parms.leftMargin -= Math.round(distanceX);
            parms.rightMargin += Math.round(distanceX);
            mCardView.setLayoutParams(parms);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //open artist in artistDetailActivity and save to savedArtists (unlock)
            Context context = mCardView.getContext();
            Intent intent = new Intent(context, ArtistDetailActivity.class);
            intent.putExtra(XamoomContentFragment.XAMOOM_CONTENT_ID, mContentId);
            context.startActivity(intent);
            Global.getInstance().saveArtist(mContentId);
            mListener.closeGeofenceFragment();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
