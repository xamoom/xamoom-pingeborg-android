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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpotListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpotListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotListFragment extends android.support.v4.app.Fragment {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLayoutManager;
    private BestLocationProvider mBestLocationProvider;
    final List<Spot> mSpotList = new LinkedList<Spot>();
    private MapAdditionFragment mMapAdditionFragment;
    static Location mUserLocation = null;

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpotListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpotListFragment newInstance() {
        SpotListFragment fragment = new SpotListFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
                    Log.i("pingeborg", "onLocationUpdate TYPE:" + type + " Location:" + mBestLocationProvider.locationToString(location));
                    getClosesSpots(location);
                }
            }
        };

        //start Location Updates
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
    }

    private void getClosesSpots(final Location location) {
        final boolean[] isLoading = {false};

        XamoomEndUserApi.getInstance().getClosestSpots(location.getLatitude(), location.getLongitude(), null, 2000, 100, new APICallback<SpotMap>() {
            @Override
            public void finished(final SpotMap result) {
                mProgressBar.setVisibility(View.GONE);
                mSpotList.addAll(result.getItems());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new SpotListRecyclerViewAdapter(getActivity(), mSpotList));
    }

    private void openMapAdditionFragment(Spot spot) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(mMapAdditionFragment == null)
            fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_top, R.anim.slide_top_bottom);

        mMapAdditionFragment = MapAdditionFragment.newInstance(spot.getDisplayName(), spot.getDescription(), spot.getImage(), spot.getLocation());
        fragmentTransaction.replace(R.id.mapAdditionFrameLayout, mMapAdditionFragment).commit();
    }

    private void closeMapAdditionFragment() {
        if(mMapAdditionFragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_bottom_top, R.anim.slide_top_bottom)
                    .remove(mMapAdditionFragment)
                    .commit();

            mMapAdditionFragment = null;
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //public void onFragmentInteraction(Uri uri);
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

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v, "Hello", Snackbar.LENGTH_SHORT).show();
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
