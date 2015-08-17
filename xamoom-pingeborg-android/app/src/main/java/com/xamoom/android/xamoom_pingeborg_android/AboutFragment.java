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

    /**
     * Use this factory method to create a new instance of
     * AboutFragment.
     *
     * @return A new instance of fragment AboutFragment.
     */
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
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
        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)), getResources().getString(R.string.apiKey));
        fragment.setContentId(Global.getInstance().getAboutPage());

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.aboutContentFrameLayout, fragment).commit();
    }
}
