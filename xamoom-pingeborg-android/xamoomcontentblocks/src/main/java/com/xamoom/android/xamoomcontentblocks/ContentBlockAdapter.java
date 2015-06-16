package com.xamoom.android.xamoomcontentblocks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentBlocks.ContentBlock;
import com.xamoom.android.mapping.ContentBlocks.ContentBlockType0;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

/**
 * Created by raphaelseher on 16.06.15.
 */
public class ContentBlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContentBlock> mContentBlocks;

    public ContentBlockAdapter(Context context, List<ContentBlock> contentBlocks) {
        mContext = context;
        mContentBlocks = contentBlocks;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return mContentBlocks.get(position).getContentBlockType();
    }

    @Override
    public int getItemCount() {
        return mContentBlocks.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_block_1_layout, parent, false);
                return new ViewHolder0(view);
            case 1:
                View view1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder1(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder2(view2);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder3(view3);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder4(view4);
            case 5:
                View view5 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder5(view5);
            case 6:
                View view6 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder6(view6);
            case 7:
                View view7 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder7(view7);
            case 8:
                View view8 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder8(view8);
            case 9:
                View view9 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_layout, parent, false);
                return new ViewHolder9(view9);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.v("pingeborg", "onBindViewHolder: " + holder.getClass());

        ContentBlock cb = mContentBlocks.get(position);

        switch (holder.getClass().toString()) {
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder0":
                ContentBlockType0 cb0 = (ContentBlockType0)cb;
                ViewHolder0 newHolder = (ViewHolder0) holder;

                if(cb0.getTitle() != null)
                    newHolder.mTitleTextView.setText(cb0.getTitle());
                else
                    newHolder.mTitleTextView.setHeight(0);

                if(cb0.getText() != null)
                    newHolder.mContentTextView.setText(Html.fromHtml(cb0.getText()));
                else
                    newHolder.mContentTextView.setHeight(0);

            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder1":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder2":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder3":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder4":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder5":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder6":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder7":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder8":
                Log.v("pingeborg", "Hellyeah");
            case "class com.xamoom.android.xamoomcontentblocks.ViewHolder9":
                Log.v("pingeborg", "Hellyeah");
        }
    }
}

class ViewHolder0 extends RecyclerView.ViewHolder {

    public TextView mTitleTextView;
    public TextView mContentTextView;

    public ViewHolder0(View itemView) {
        super(itemView);
        mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        mContentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
    }

    public static void fuckthis(){}
}


class ViewHolder1 extends RecyclerView.ViewHolder {

    public ViewHolder1(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 1");
    }
}

class ViewHolder2 extends RecyclerView.ViewHolder {

    public ViewHolder2(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 2");
    }
}


class ViewHolder3 extends RecyclerView.ViewHolder {

    public ViewHolder3(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 3");
    }
}

class ViewHolder4 extends RecyclerView.ViewHolder {

    public ViewHolder4(View itemView) {
        super(itemView);
        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 4");
    }
}

class ViewHolder5 extends RecyclerView.ViewHolder {

    public ViewHolder5(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 5");
    }
}

class ViewHolder6 extends RecyclerView.ViewHolder {

    public ViewHolder6(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 6");
    }
}

class ViewHolder7 extends RecyclerView.ViewHolder {

    public ViewHolder7(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 7");
    }
}

class ViewHolder8 extends RecyclerView.ViewHolder {

    public ViewHolder8(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 8");
    }
}

class ViewHolder9 extends RecyclerView.ViewHolder {

    public ViewHolder9(View itemView) {
        super(itemView);

        TextView tv = (TextView) itemView.findViewById(R.id.OMG);

        tv.setText("ContentBlock 9");
    }
}
