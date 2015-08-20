package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;
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
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentList;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import retrofit.RetrofitError;


/**
 * TODO
 */
public class ArtistListFragment extends Fragment {

    private final static int PAGE_SIZE = 7;

    private static ArtistListFragment mInstance;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Content> mContentList = new LinkedList<>();
    private String mCursor;
    private boolean isMore = true;
    private boolean isLoading = false;

    /**
     * Returns a ArtistListFragment Singleton.
     *
     * @return ArtistListFragment Singleton.
     */
    public static ArtistListFragment getInstance() {
        if(mInstance == null) {
            mInstance = new ArtistListFragment();
        }
        return mInstance;
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
        setupRecyclerView(mRecyclerView);

        //check if the contentList is already loaded
        if(mContentList.size() > 0) {
            setupContentList();
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            downloadArtists();
            setupContentList();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setupRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), mContentList));
    }

    public void setupContentList() {
        //load more on scrolling
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            //load on scroll implementation
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pastVisiblesItems, visibleItemCount, totalItemCount;

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                //true when there are only 2 items until end
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 1) {
                    downloadArtists();
                }
            }
        });
    }

    /**
     * Download contentList from xamoom cloud and add them to recyclerView.
     */
    public void downloadArtists() {
        if(isMore && !isLoading) {
            isLoading = true;

            //add loading indicator
            mContentList.add(null);
            mRecyclerView.getAdapter().notifyItemInserted(mContentList.size() - 1);

            XamoomEndUserApi.getInstance(this.getActivity().getApplicationContext(), getResources().getString(R.string.apiKey)).getContentList(null, PAGE_SIZE, mCursor, new String[]{"artists"}, new APICallback<ContentList>() {
                @Override
                public void finished(final ContentList result) {

                    //unlock 3 artists as a present for the user
                    if (Global.getInstance().checkFirstStart()) {
                        for (int i = 1; i < 4; i++) {
                            Content c = result.getItems().get(i);
                            Global.getInstance().saveArtist(c.getContentId());
                        }
                    }

                    //remove loading indicator
                    mContentList.remove(mContentList.size() - 1);
                    mRecyclerView.getAdapter().notifyItemInserted(mContentList.size());

                    mContentList.addAll(result.getItems());
                    mRecyclerView.getAdapter().notifyDataSetChanged();

                    mCursor = result.getCursor();
                    isMore = result.isMore();
                    isLoading = false;
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                    isLoading = false;
                }
            });
        }
    }

    /**
     * Add XammomContentFragment to View to display the artist.
     *
     * @param mBoundContent A {@link com.xamoom.android.mapping.Content} to display
     */
    public void openArtistDetails(Content mBoundContent) {
        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2), getResources().getString(R.string.apiKey));

        //use contentId, when artist is alreay unlocked
        if(Global.getInstance().getSavedArtists().contains(mBoundContent.getContentId())) {
            fragment.setContentId(mBoundContent.getContentId());
        } else {
            fragment.setContent(mBoundContent);
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_swipe_in, 0, 0, R.anim.bottom_swipe_out)
                .replace(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Adapter for recyclerView to display the artistList.
     *
     * Can display artists (image and text) or a progressBar.
     */
    public class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
        private final static int VIEW_ITEM = 1;
        private final static int VIEW_PROG = 0;

        private final TypedValue mTypedValue = new TypedValue();
        private final Context mContext;
        private int mBackground;
        private List<Content> mContentList;

        public class ViewHolder extends RecyclerView.ViewHolder {
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

                int width = (mContext.getResources().getDisplayMetrics().widthPixels) - (int)(2*mContext.getResources().getDimension(R.dimen.halfFrameLayoutPadding));

                mView.setMinimumHeight((int)(width/2.632));
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
                    //display artist
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.artist_list_item, parent, false);
                    view.setBackgroundResource(mBackground);
                    return new ViewHolder(view);
                case 0:
                    //display progressBar
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
                    //set image normal
                    Glide.with(mContext)
                            .load(holder.mBoundContent.getImagePublicUrl())
                            .placeholder(R.drawable.placeholder)
                            .dontAnimate()
                            .into(holder.mImageView);

                    holder.mOverlayImageView.setVisibility(View.GONE);

                } else {
                    //set image grayscale, and overlayed
                    Glide.with(mContext)
                            .load(holder.mBoundContent.getImagePublicUrl())
                            .placeholder(R.drawable.placeholder)
                            .crossFade()
                            .bitmapTransform(new GrayscaleTransformation(pool))
                            .into(holder.mImageView);

                    holder.mOverlayImageView.setVisibility(View.VISIBLE);

                    holder.mOverlayImageView.setImageResource(android.R.color.transparent);

                    //display "discover me"/"Entdecke mich" image
                    if (position == 0) {
                        Glide.with(mContext)
                                .load(R.drawable.discoverme)
                                .crossFade()
                                .dontTransform() //or the alpha will be ignored
                                .into(holder.mOverlayImageView);
                    }
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openArtistDetails(holder.mBoundContent);
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
