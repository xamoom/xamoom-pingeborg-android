package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentList;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    /**
     *
     */
    public static ArtistListFragment newInstance() {
        ArtistListFragment fragment = new ArtistListFragment();
        return fragment;
    }

    public ArtistListFragment() {
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
        //Log.v("pingeborg", "Hellyeah: " + inflater.inflate(R.layout.fragment_artist_list, container, false).toString());
        mRecyclerView = (RecyclerView)inflater.inflate(R.layout.fragment_artist_list, container, false);
        setupRecyclerView(mRecyclerView);
        return mRecyclerView;
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        final String[] mCursor = new String[1];
        final boolean[] isLoading = {false};

        XamoomEndUserApi.getInstance().getContentList(null, 7, null, new String[]{"artists"}, new APICallback<ContentList>() {
            @Override
            public void finished(final ContentList result) {
                recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), result.getItems()));

                mCursor[0] = result.getCursor();

                //load more on scrolling
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int pastVisiblesItems, visibleItemCount, totalItemCount;

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                        //true when there are only 2 items until end
                        if ( (visibleItemCount+pastVisiblesItems) >= totalItemCount-1) {
                            Log.v("pingeborg", "Hellyeah, Nachladen!");

                            if (!isLoading[0]) {
                                isLoading[0] = true;
                                XamoomEndUserApi.getInstance().getContentList(null, 7, mCursor[0], new String[]{"artists"}, new APICallback<ContentList>() {
                                    @Override
                                    public void finished(ContentList resultReload) {
                                        mCursor[0] = resultReload.getCursor();
                                        Log.v("pingeborg", "NACHGELADEN!");
                                        result.getItems().addAll(resultReload.getItems());
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        isLoading[0] = false;
                                    }
                                });
                            }

                        }
                    }
                });
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String contentId) {
        if (mListener != null) {
            mListener.onArtistSelected(contentId);
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
        public void onArtistSelected(String contentId);
    }

    /**
     *
     *
     *
     *
     */
    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private final Context mContext;
        private int mBackground;
        private List<Content> mContentList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public Content mBoundContent;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.artistListItemImageView);
                mTextView = (TextView) view.findViewById(R.id.artistListItemTextView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public Content getValueAt(int position) {
            return mContentList.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<Content> items) {
            if(context != null)
                context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);

            mBackground = mTypedValue.resourceId;
            mContentList = items;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.artist_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //save content for later use
            holder.mBoundContent = mContentList.get(position);

            //set text
            holder.mTextView.setText(holder.mBoundContent.getTitle());

            //download and set image via picasso
            Glide.with(mContext)
                    .load(holder.mBoundContent.getImagePublicUrl())
                    .crossFade()
                    .into(holder.mImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ArtistDetailActivity.class);
                    intent.putExtra(XamoomContentFragment.XAMOOM_CONTENT_ID, holder.mBoundContent.getContentId());

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mContentList.size();
        }
    }
}
