package com.as.wikiimagesearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.as.wikiimagesearch.network.ImageLoadingRequestQueue;

import java.util.List;

/**
 * Created by PRABHAT on 1/26/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static MyClickListener myClickListener;
    private List<WikiPageEntity> mWikiPageList;
    private Context mContext;
    private ImageLoader mImageLoader;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView mTextView;
        public NetworkImageView mNetworkImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.textView);
            mNetworkImageView = (NetworkImageView)v.findViewById(R.id.networkImageView);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition(), view);
        }
    }

    public RecyclerViewAdapter(Context context) {
        mContext = context;

        // Instantiate the RequestQueue.
        mImageLoader = ImageLoadingRequestQueue.getInstance(mContext.getApplicationContext())
                .getImageLoader();
    }

    public void setWikiPagesList(List<WikiPageEntity> wikiPagesList) {
        mWikiPageList = wikiPagesList;
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mWikiPageList.get(position).getTitle());

        holder.mNetworkImageView.setImageBitmap(null);
        //Image URL -
        Thumbnail thumbnail = mWikiPageList.get(position).getThumbnail();
        if(thumbnail != null) {
            String imageURL = thumbnail.getSource();
                mImageLoader.get(imageURL, ImageLoader.getImageListener(holder.mNetworkImageView,
                        R.mipmap.ic_launcher, R.drawable.not_available));
                holder.mNetworkImageView.setImageUrl(imageURL, mImageLoader);
        } else {
            holder.mNetworkImageView.setDefaultImageResId(R.drawable.not_available);
        }
    }

    @Override
    public int getItemCount() {
        if(mWikiPageList != null) {
            return mWikiPageList.size();
        }
        return 0;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}