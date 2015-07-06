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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentList;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;


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
    private ProgressBar mProgressBar;

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

        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.artistListRecyclerView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.loadingArtistsProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        setupRecyclerView(mRecyclerView);
        return view;
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        final List<Content> mContentList = new LinkedList<Content>();
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), mContentList));

        final String[] mCursor = new String[1];
        final boolean[] isLoading = {false};
        final boolean[] isMore = new boolean[1];

        XamoomEndUserApi.getInstance().getContentList(null, 7, null, new String[]{"artists"}, new APICallback<ContentList>() {
            @Override
            public void finished(final ContentList result) {
                //stop the progress indicator on activity
                mProgressBar.setVisibility(View.GONE);

                //save 3 artists as present for the user
                if (Global.getInstance().getSavedArtists() == null) {
                    for (int i = 1; i < 4; i++) {
                        Content c = result.getItems().get(i);
                        Global.getInstance().saveArtist(c.getContentId());
                    }
                }

                //recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), result.getItems()));
                mContentList.addAll(result.getItems());
                recyclerView.getAdapter().notifyDataSetChanged();

                mCursor[0] = result.getCursor();
                isMore[0] = result.isMore();

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
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 1) {

                            if (!isLoading[0] && isMore[0]) {
                                isLoading[0] = true;

                                //add loading indicator
                                mContentList.add(null);
                                recyclerView.getAdapter().notifyItemInserted(result.getItems().size() - 1);

                                XamoomEndUserApi.getInstance().getContentList(null, 7, mCursor[0], new String[]{"artists"}, new APICallback<ContentList>() {
                                    @Override
                                    public void finished(ContentList resultReload) {
                                        Analytics.getInstance(getActivity()).sendEvent("UX","Loaded more artists","The user loaded more artists");
                                        mCursor[0] = resultReload.getCursor();
                                        isMore[0] = resultReload.isMore();

                                        //remove loading indicator
                                        mContentList.remove(mContentList.size() - 1);
                                        recyclerView.getAdapter().notifyItemInserted(mContentList.size());

                                        //add items to get displayed
                                        mContentList.addAll(resultReload.getItems());
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
    }

    /**
     *
     *
     *
     *
     */
    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
        private final static int VIEW_ITEM = 1;
        private final static int VIEW_PROG = 0;

        private final TypedValue mTypedValue = new TypedValue();
        private final Context mContext;
        private int mBackground;
        private List<Content> mContentList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public Content mBoundContent;

            public final View mView;
            public final ImageView mImageView;
            public final ImageView mOverlayImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.artistListItemImageView);
                mTextView = (TextView) view.findViewById(R.id.artistListItemTextView);
                mOverlayImageView = (ImageView) view.findViewById(R.id.artistListItemOverlayImageView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public Content getValueAt(int position) {
            return mContentList.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mContentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
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

            switch (viewType) {
                case 1:
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.artist_list_item, parent, false);
                    view.setBackgroundResource(mBackground);
                    return new ViewHolder(view);
                case 0:
                    View progressView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.progress_layout, parent, false);
                    return new ViewHolder(progressView);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            if (holder.getItemViewType() == VIEW_ITEM) {
                //save content for later use
                holder.mBoundContent = mContentList.get(position);

                //set text
                holder.mTextView.setText(holder.mBoundContent.getTitle());

                BitmapPool pool = Glide.get(mContext).getBitmapPool();
                //download and set image via Glide
                if(Global.getInstance().getSavedArtists().contains(holder.mBoundContent.getContentId())) {
                    Glide.with(mContext)
                            .load(holder.mBoundContent.getImagePublicUrl())
                            .crossFade()
                            .into(holder.mImageView);

                    holder.mOverlayImageView.setVisibility(View.GONE);

                } else {
                    Glide.with(mContext)
                            .load(holder.mBoundContent.getImagePublicUrl())
                            .crossFade()
                            .bitmapTransform(new GrayscaleTransformation(pool))
                            .into(holder.mImageView);

                    holder.mOverlayImageView.setVisibility(View.VISIBLE);

                    holder.mOverlayImageView.setImageResource(android.R.color.transparent);
                    if (position == 0) {
                        Glide.with(mContext)
                                .load(R.drawable.discoverable)
                                .dontTransform() //or the alpha will be ignored
                                .into(holder.mOverlayImageView);
                    }
                }

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
        }

        @Override
        public int getItemCount() {
            return mContentList.size();
        }
    }
}
