package com.xamoom.android.xamoom_pingeborg_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Spot;
import com.xamoom.android.mapping.SpotMap;

import java.util.LinkedList;
import java.util.List;

import retrofit.RetrofitError;


/**
 * TODO
 */
public class SpotListFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLayoutManager;
    private BestLocationProvider mBestLocationProvider;
    final List<Spot> mSpotList = new LinkedList<>();
    static Location mUserLocation = null;

    private OnSpotListFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment SpotListFragment.
     */
    public static SpotListFragment newInstance() {
        return new SpotListFragment();
    }

    public SpotListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.spotListRecyclerView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.loadingSpotsProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        setupRecyclerView(mRecyclerView);
        setupLocation();
        return view;
    }

    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mBestLocationProvider = new BestLocationProvider(getActivity(), false, true, 10000, 10000, 10, 40);
        BestLocationListener mBestLocationListener = new BestLocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Log.i("pingeborg", "onStatusChanged PROVIDER:" + provider + " STATUS:" + String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                //Log.i("pingeborg", "onProviderEnabled PROVIDER:" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                //Log.i("pingeborg", "onProviderDisabled PROVIDER:" + provider);
            }

            @Override
            public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {
                //Log.w("pingeborg", "onLocationUpdateTimeoutExceeded PROVIDER:" + type);
            }

            @Override
            public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
                if(isFresh) {
                    mUserLocation = location;
                    Log.i(Global.DEBUG_TAG, "onLocationUpdate TYPE:" + type + " Location:" + mBestLocationProvider.locationToString(location));
                    getClosesSpots(location);
                    mBestLocationProvider.stopLocationUpdates();
                }
            }
        };

        //start Location Updates
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
    }

    private void getClosesSpots(final Location location) {
        if(this.isAdded()) {
            XamoomEndUserApi.getInstance(this.getActivity().getApplicationContext(), getResources().getString(R.string.apiKey)).getClosestSpots(location.getLatitude(), location.getLongitude(), null, 2000, 20, new APICallback<SpotMap>() {
                @Override
                public void finished(final SpotMap result) {
                    mProgressBar.setVisibility(View.GONE);
                    mSpotList.addAll(result.getItems());
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                }
            });
        }

    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new SpotListRecyclerViewAdapter(getActivity(), mSpotList));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSpotListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSpotListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * closeGeofenceFragment must be implemented to remove the fragment from activity.
     */
    public interface OnSpotListFragmentInteractionListener {
        void clickedSpot(Spot spot);
    }

    /**
     *
     *
     *
     *
     */
    public class SpotListRecyclerViewAdapter extends RecyclerView.Adapter<SpotListRecyclerViewAdapter.ViewHolder> {
        private final static int VIEW_ITEM = 1;
        private final static int VIEW_PROG = 0;

        private final TypedValue mTypedValue = new TypedValue();
        private final Context mContext;
        private int mBackground;
        private List<Spot> mSpotList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Spot mBoundContent;

            public final View mView;
            public final LinearLayout mTextOverlay;
            public final TextView mTitleTextView;
            public final TextView mDistanceTextView;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextOverlay = (LinearLayout) view.findViewById(R.id.spotListTextOverlay);
                mTitleTextView = (TextView) view.findViewById(R.id.spotNameTextView);
                mDistanceTextView = (TextView) view.findViewById(R.id.spotDistanceTextView);
                mImageView = (ImageView) view.findViewById(R.id.spotListImageView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleTextView.getText();
            }
        }

        public Spot getValueAt(int position) {
            return mSpotList.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mSpotList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        public SpotListRecyclerViewAdapter(Context context, List<Spot> items) {
            if(context != null)
                context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);

            mBackground = mTypedValue.resourceId;
            mSpotList = items;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spot_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (holder.getItemViewType() == VIEW_ITEM) {
                //save content for later use
                holder.mBoundContent = mSpotList.get(position);

                //set text
                holder.mTitleTextView.setText(holder.mBoundContent.getDisplayName());

                //display distance
                Location spotLocation = new Location("");
                spotLocation.setLatitude(holder.mBoundContent.getLocation().getLat());
                spotLocation.setLongitude(holder.mBoundContent.getLocation().getLon());
                float distance = mUserLocation.distanceTo(spotLocation);
                holder.mDistanceTextView.setText(String.valueOf(Math.round(distance)) + " Meter");

                //holder.mImageView.setImageURI(null);

                //add Image
                if(holder.mBoundContent.getImage() != null) {
                    Glide.with(mContext)
                            .load(holder.mBoundContent.getImage())
                            .dontTransform()
                            .crossFade()
                            .into(holder.mImageView);
                }
                else {
                    holder.mImageView.setImageResource(0);
                   if(position%2 == 1) {
                       holder.mTextOverlay.setBackgroundColor(mContext.getResources().getColor(R.color.artist_list_text_background_lighter));
                   } else {
                       holder.mTextOverlay.setBackgroundColor(mContext.getResources().getColor(R.color.artist_list_text_background));
                   }
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.clickedSpot(holder.mBoundContent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mSpotList.size();
        }
    }
}
