package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
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

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeofenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GeofenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeofenceFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "contentTitleParam";
    private static final String ARG_PARAM2 = "contentImageUrlParam";
    private static ContentByLocationItem mSavedGeofence;
    private CardView mCardView;

    public static void setSavedGeofence(ContentByLocationItem mSavedGeofence) {
        GeofenceFragment.mSavedGeofence = mSavedGeofence;
    }

    private String mContentTitle;
    private String mContentImageUrl;

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param imageUrl Parameter 2.
     * @return A new instance of fragment GeofenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeofenceFragment newInstance(String title, String imageUrl) {
        GeofenceFragment fragment = new GeofenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, imageUrl);
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
        }
    }

    /**
                test
     */
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

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

                    Log.v("pingeborg","Velocity: " + velocityX);
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

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mCardView.startAnimation(ab);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    int[] viewLocation = new int[2];
                    mCardView.getLocationInWindow(viewLocation);

                    Log.v("pingeborg","Velocity: " + velocityX);
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

            RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
            parms.leftMargin -= Math.round(distanceX);
            parms.rightMargin += Math.round(distanceX);
            mCardView.setLayoutParams(parms);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_geofence, container, false);
        Log.v("pingeborg", "Hellyeah in GeofenceFragment");

        mCardView = (CardView) view.findViewById(R.id.card_view);
        TextView textView = (TextView) view.findViewById(R.id.geofenceTextView);
        textView.setText(mContentTitle);

        ImageView imageView = (ImageView) view.findViewById(R.id.geofenceImage);
        Glide.with(getActivity())
                .load(mContentImageUrl)
                .into(imageView);

        ImageView overlayImageView = (ImageView) view.findViewById(R.id.geofenceOverlayImageView);

        gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 1 && mCardView.getVisibility() == View.VISIBLE) {
                    Log.v("pingeborg","View on up?");
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
                    parms.leftMargin = 16;
                    parms.rightMargin = 16;
                    //mCardView.setLayoutParams(parms);
                }

                return gestureDetector.onTouchEvent(event);
            }
        };
        view.setOnTouchListener(gestureListener);

        return view;
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
