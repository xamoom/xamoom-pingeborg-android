package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import retrofit.RetrofitError;


/**
 * TODO
 */
public class AboutFragment extends android.support.v4.app.Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    public AboutFragment() {
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
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        setupXamoomContentFragment();
        return view;
    }

    public void setupXamoomContentFragment () {
        //load content with contentId
        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)));
        fragment.setContentId(Global.getInstance().getAboutPage());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.aboutContentFrameLayout, fragment).commit();
    }
}
