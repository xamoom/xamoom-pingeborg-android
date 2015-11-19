package com.xamoom.android.xamoom_pingeborg_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;


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
        mFragment = XamoomContentFragment.newInstance(
                Integer.toHexString(getResources().getColor(R.color.pingeborg_green)),
                getResources().getString(R.string.apiKey));
        mFragment.setContentId(Global.getInstance().getAboutPage());
        this.getChildFragmentManager().beginTransaction().replace(R.id.aboutContentFrameLayout, mFragment).commit();
    }
}
