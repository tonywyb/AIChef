package com.example.aichef.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.R;
import com.example.aichef.adapter.FavoriteAdapter;
import com.example.aichef.adapter.SearchHistoryAdapter;
import com.example.aichef.asyncTask.GetFavoriteAsync;
import com.example.aichef.model.GetFavoriteinput;
import com.example.aichef.model.GetFavoriteoutput;
import com.example.aichef.objectClass.HistoryRecord;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static String TAG = "HistoryFragment";
    private ArrayList<HistoryRecord> history_records;
    private SwipeRefreshLayout refresh;
    private RecyclerView historyRecycler;
    private LinearLayoutManager mLayoutManager;
    private SearchHistoryAdapter mAdapter;
    private JsonParser jsonParser;
    private View view_copy;
    private RecyclerView filled;
    private GetFavoriteAsync new_task;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        System.out.println("Come into Favorite");
        view_copy = view;
        refresh = view.findViewById(R.id.favorite_swipe_refresh);
        refresh.setOnRefreshListener(this);

        filled = view.findViewById(R.id.listviewxx);


        //GetFavoriteAsync new_task = new GetFavoriteAsync(filled,getActivity());
        //new_task.execute();

        return view;

    }

    public void clear(){
        if(new_task!=null&&new_task.pre.size()!=0){
            new_task.pre.clear();
            new_task.favo_adaptor.notifyDataSetChanged();
        }
    }



    @Override
    public void onRefresh() {
        new_task = new GetFavoriteAsync(filled,getActivity(),refresh);
        new_task.execute();
        //refresh.setRefreshing(false);

    }

}