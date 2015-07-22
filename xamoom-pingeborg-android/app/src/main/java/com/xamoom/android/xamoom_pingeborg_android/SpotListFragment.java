package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentList;
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
    final List<Spot> mSpotList = new LinkedList<Spot>();
    private MapAdditionFragment mMapAdditionFragment;
    static Location mUserLocation = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment SpotListFragment.
     */
    public static SpotListFragment newInstance() {
        SpotListFragment fragment = new SpotListFragment();
        return fragment;
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
        mBestLocationProvider = new BestLocationProvider(getActivity(), true, true, 10000, 10000, 10, 40);

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
                }
            }
        };

        //start Location Updates
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
    }

    private void getClosesSpots(final Location location) {
        final boolean[] isLoading = {false};

        XamoomEndUserApi.getInstance(this.getActivity().getApplicationContext()).getClosestSpots(location.getLatitude(), location.getLongitude(), null, 2000, 100, new APICallback<SpotMap>() {
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

    private void setupRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new SpotListRecyclerViewAdapter(getActivity(), mSpotList));
    }

    /**
     *
     *
     *
     *
     */
    public static class SpotListRecyclerViewAdapter extends RecyclerView.Adapter<SpotListRecyclerViewAdapter.ViewHolder> {
        private final static int VIEW_ITEM = 1;
        private final static int VIEW_PROG = 0;

        private final TypedValue mTypedValue = new TypedValue();
        private final Context mContext;
        private int mBackground;
        private List<Spot> mSpotList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public Spot mBoundContent;

            public final View mView;
            public final TextView mTitleTextView;
            public final TextView mDistanceTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleTextView = (TextView) view.findViewById(R.id.spotNameTextView);
                mDistanceTextView = (TextView) view.findViewById(R.id.spotDistanceTextView);
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

                Location spotLocation = new Location("");
                spotLocation.setLatitude(holder.mBoundContent.getLocation().getLat());
                spotLocation.setLongitude(holder.mBoundContent.getLocation().getLon());
                float distance = mUserLocation.distanceTo(spotLocation);
                holder.mDistanceTextView.setText(String.valueOf(Math.round(distance)) + " Meter");
            }
        }

        @Override
        public int getItemCount() {
            return mSpotList.size();
        }
    }
}
