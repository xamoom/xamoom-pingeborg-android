package com.xamoom.android.xamoom_pingeborg_android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType3;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;


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
        XamoomEndUserApi.getInstance().getContentbyIdFull(Global.getInstance().getAboutPage(), false, false, null, true, new APICallback<ContentById>() {
            @Override
            public void finished(ContentById result) {
                //create title and titleImage from content
                ContentBlockType0 title = new ContentBlockType0(result.getContent().getTitle(),true, 0, result.getContent().getDescriptionOfContent());
                ContentBlockType3 image = new ContentBlockType3(null, true, 3, result.getContent().getImagePublicUrl());
                //add title and titleImage to contentBlocks
                result.getContent().getContentBlocks().add(0, title);
                result.getContent().getContentBlocks().add(1, image);

                //Create XamoomContentFragment
                XamoomContentFragment fragment = XamoomContentFragment.newInstance(Global.YOUTUBE_API_KEY, Integer.toHexString(getResources().getColor(R.color.pingeborg_green)));
                fragment.setContentBlocks(result.getContent().getContentBlocks());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.aboutContentFrameLayout, fragment).commit();
            }
        });
    }
}
