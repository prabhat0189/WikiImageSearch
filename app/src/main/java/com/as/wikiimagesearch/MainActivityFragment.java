package com.as.wikiimagesearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.as.wikiimagesearch.network.NetworkRequestQueue;
import com.as.wikiimagesearch.network.WikiJsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements Response.Listener,
        Response.ErrorListener {

    private static final String REQUEST_TAG = "WikiImageRequest";
    private final String WIKI_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=100&pilimit=50&generator=prefixsearch&gpssearch=";
    private RequestQueue mQueue;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLocationsLayoutManager;

    List<WikiPageEntity> mWikiPageList;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mQueue = NetworkRequestQueue.getInstance(getActivity().getApplicationContext())
                .getRequestQueue();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((RecyclerViewAdapter) mAdapter).setOnItemClickListener(new RecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(getActivity(), position + "", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error... " + error.getMessage(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(Object response) {
        try {
            JSONObject jsonObjectQuery = ((JSONObject) response).getJSONObject("query");
            JSONObject pages = jsonObjectQuery.getJSONObject("pages");
            //Toast.makeText(getContext(), "Result... " + jsonResult, Toast.LENGTH_LONG).show();

            mWikiPageList = new ArrayList<>();
            Iterator<String> iterator = pages.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();
                Log.d("PRABHAT", "jsonResult: " + key);

                //Convert JSON string to POJO using gson library
                Gson gson = new Gson();
                WikiPageEntity wikiPage = gson.fromJson(pages.getString(key), WikiPageEntity.class);
                mWikiPageList.add(wikiPage);
            }

            //refresh list
            updateRecyclerView();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Exception... " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void initView(View view) {

        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLocationsLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLocationsLayoutManager);

        mAdapter = new RecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    private void performSearch(String query) {
        Toast.makeText(getContext(), "Searching... " + query, Toast.LENGTH_LONG).show();

        final WikiJsonObjectRequest jsonRequest = new WikiJsonObjectRequest(Request.Method
                .GET, WIKI_API_URL + query,
                new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);


        mQueue.add(jsonRequest);
    }

    private void updateRecyclerView() {
        mAdapter.setWikiPagesList(mWikiPageList);
        mAdapter.notifyDataSetChanged();
    }
}
