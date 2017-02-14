package com.sam_chordas.android.stockhawk.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kartik Sharma on 07/12/16.
 */
public class VolleyInstance {

    private static VolleyInstance mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyInstance(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyInstance getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyInstance(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
