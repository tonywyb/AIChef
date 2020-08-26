package com.example.aichef.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aichef.asyncTask.GetHistoryAsync;
import com.example.aichef.objectClass.HistoryRecord;
import com.example.aichef.R;
import com.example.aichef.adapter.SearchHistoryAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String TAG = "HistoryFragment";
    public ArrayList<HistoryRecord> history_records;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView historyRecycler;
    public LinearLayoutManager mLayoutManager;
    public SearchHistoryAdapter mAdapter;
    private JsonParser jsonParser;

    public HistoryFragment() {
        // Required empty public constructor
        jsonParser = new JsonParser();
        history_records = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        swipeRefreshLayout = view.findViewById( R.id.search_history_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        historyRecycler = view.findViewById(R.id.search_history_recycler);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        historyRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new SearchHistoryAdapter(history_records, swipeRefreshLayout, this.getContext());
        historyRecycler.setAdapter(mAdapter);
        return view;
    }

    public void load(){

        mAdapter.initS3();

        //Should use asynctask for apigateway, this part should be in onpostexecute
        GetHistoryAsync getHistoryAsync = new GetHistoryAsync(this, false, jsonParser);
        getHistoryAsync.execute();
    }

    public void clear(){
        Log.i(TAG, "clear");
        if (history_records.size() != 0){
            history_records.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void reload(){
        //get content from asynctask
        GetHistoryAsync getHistoryAsync = new GetHistoryAsync(this, true, jsonParser);
        getHistoryAsync.execute();
    }
    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: Refresh History Records");
        reload();
    }
}
