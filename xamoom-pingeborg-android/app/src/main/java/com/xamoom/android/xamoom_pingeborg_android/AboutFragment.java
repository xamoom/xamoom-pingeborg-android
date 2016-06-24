package com.xamoom.android.xamoom_pingeborg_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;
import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.EnduserApi;
import com.xamoom.android.xamoomsdk.Enums.ContentFlags;
import com.xamoom.android.xamoomsdk.Resource.Content;

import java.util.EnumSet;
import java.util.List;

import at.rags.morpheus.Error;


/**
 * AboutFragment used to display the About-Page from pingeb.org.
 */
public class AboutFragment extends android.support.v4.app.Fragment {

    XamoomContentFragment mFragment;

    /**
     * Use this factory method to create a new instance of
     * AboutFragment.
     *
     * @return A new instance of fragment AboutFragment.
     */
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(Global.DEBUG_TAG, "AboutFragment - onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(Global.DEBUG_TAG, "AboutFragment - onCreateView");
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(Global.DEBUG_TAG, "AboutFragment - onStart");
        setupXamoomContentFragment();
    }

    /**
     * Setup XamoomContentFragment with contentId.
     * After displaying the fragment will load the content and display it.
     */
    public void setupXamoomContentFragment () {
        Log.v(Global.DEBUG_TAG, "AboutFragment - setupXamoomContentFragment");
        mFragment = XamoomContentFragment.newInstance(getResources().getString(R.string.youtubekey));

        EnduserApi.getSharedInstance().getContent(Global.getInstance().getAboutPage(), new APICallback<Content, List<Error>>() {
            @Override
            public void finished(Content result) {
                mFragment.setContent(result);
                getChildFragmentManager().beginTransaction().replace(R.id.aboutContentFrameLayout, mFragment).commit();
            }

            @Override
            public void error(List<Error> error) {
                //TODO errorhandling
            }
        });
    }
}
