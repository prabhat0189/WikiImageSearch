package com.as.wikiimagesearch.network;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 *  Created by PRABHAT on 1/26/2016
 *  implementation of Volley Request Queue
 */
public class NetworkRequestQueue {

    private static NetworkRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private NetworkRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }
}
