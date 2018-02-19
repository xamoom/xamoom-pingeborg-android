package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;
import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.APIListCallback;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Enums.ContentFlags;
import com.xamoom.android.xamoomsdk.Enums.ContentSortFlags;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import at.rags.morpheus.Error;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class ArtistListFragment extends Fragment {
  private final static String TAG = ArtistListFragment.class.getSimpleName();
  private final static int PAGE_SIZE = 7;

  private static ArtistListFragment mInstance;
  private View mView;
  private RecyclerView mRecyclerView;
  private LinearLayoutManager mLayoutManager;
  private List<Content> mContentList = new LinkedList<>();
  private String mCursor;
  private boolean isMore = true;
  private boolean isLoading = false;
  private ArtistListInteractionListener listener;

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
    mView = inflater.inflate(R.layout.fragment_artist_list, container, false);
    mRecyclerView = (RecyclerView) mView.findViewById(R.id.artistListRecyclerView);
    setupRecyclerView(mRecyclerView);
    setupContentList();

    return mView;
  }

  @Override
  public void onResume() {
    super.onResume();

    //check if the contentList is already loaded
    if(mContentList.size() > 0) {
      mRecyclerView.getAdapter().notifyDataSetChanged();
    } else {
      downloadArtists();
    }
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


      ArrayList<String> tags = new ArrayList<String>();
      tags.add("artists");
      EnduserApi.getSharedInstance().getContentsByTags(tags, PAGE_SIZE, mCursor, EnumSet.of(ContentSortFlags.NAME_DESC), new APIListCallback<List<Content>, List<Error>>() {
        @Override
        public void finished(List<Content> result, String cursor, boolean hasMore) {
          //unlock 3 artists as a present for the user
          if (Global.getInstance().checkFirstStart()) {
            for (int i = 1; i < 4; i++) {
              Content c = result.get(i);
              Global.getInstance().saveArtist(c.getId());
            }
          }

          //remove loading indicator
          mContentList.remove(mContentList.size() - 1);
          mRecyclerView.getAdapter().notifyItemInserted(mContentList.size());

          mContentList.addAll(result);
          mRecyclerView.getAdapter().notifyDataSetChanged();

          mCursor = cursor;
          isMore = hasMore;
          isLoading = false;
        }

        @Override
        public void error(List<Error> error) {
          Log.e(Global.DEBUG_TAG, "Error:" + error);
          Snackbar snackbar = Snackbar.make(mView, R.string.error_message_api_call_failed, Snackbar.LENGTH_INDEFINITE);
          snackbar.show();
          isLoading = false;
        }
      });
    }
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
        if(Global.getInstance().getSavedArtists().contains(holder.mBoundContent.getId())) {
          //set image normal
          Glide.with(mContext)
                  .load(holder.mBoundContent.getPublicImageUrl())
                  .placeholder(R.drawable.placeholder)
                  .dontAnimate()
                  .into(holder.mImageView);

          holder.mOverlayImageView.setVisibility(View.GONE);

        } else {
          //set image grayscale, and overlayed
          Glide.with(mContext)
                  .load(holder.mBoundContent.getPublicImageUrl())
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
            if (listener != null) {
              listener.didClickContent(holder.mBoundContent);
            }
          }
        });
      }
    }

    @Override
    public int getItemCount() {
      return mContentList.size();
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof ArtistListInteractionListener) {
      listener = (ArtistListInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement ArtistListInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  public interface ArtistListInteractionListener {
    void didClickContent(Content content);
  }
}
