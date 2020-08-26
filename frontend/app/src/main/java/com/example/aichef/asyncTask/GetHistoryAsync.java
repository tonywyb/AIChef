package com.example.aichef.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.fragment.HistoryFragment;
import com.example.aichef.model.GetSearchHistoryinput;
import com.example.aichef.model.GetSearchHistoryoutput;
import com.example.aichef.objectClass.HistoryRecord;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.ref.WeakReference;

public class GetHistoryAsync extends AsyncTask<Void, Void, JsonElement> {
    private final String TAG = "GetHistoryAsync";
    private boolean reload;
    private JsonParser jsonParser;
    private WeakReference<HistoryFragment> fragmentWeakReference;

    public GetHistoryAsync(HistoryFragment fragment, boolean reload, JsonParser jsonParser){
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.reload = reload;
        this.jsonParser = jsonParser;
    }

    @Override
    protected JsonElement doInBackground(Void... voids) {
        ApiClientFactory factory = new ApiClientFactory();
        final AIchiefClient client = factory.build(AIchiefClient.class);
        GetSearchHistoryinput input = new GetSearchHistoryinput();

        String userId = AWSMobileClient.getInstance().getUsername();
        if(userId==null){
            return null;
        }
        input.setUserId(userId);
        GetSearchHistoryoutput result = client.getsearchhistoryPost(input);
        JsonElement jsonElement;
        jsonElement = jsonParser.parse(result.getBody());
        return jsonElement;
    }

    @Override
    protected void onPostExecute(JsonElement jsonElement) {
        HistoryFragment fragment = fragmentWeakReference.get();
        if (fragment == null || jsonElement == null){
            return;
        }
        if (reload){
            JsonElement cur;
            int added = 0;
            for (int i = 0; i < jsonElement.getAsJsonArray().size(); i++){
                cur = jsonElement.getAsJsonArray().get(i);
                HistoryRecord curItem = new HistoryRecord(cur);
                if (!fragment.history_records.contains(curItem)){
                    fragment.history_records.add(curItem);
                    added ++;
                }
            }
            if (added != 0){
                fragment.mAdapter.notifyItemRangeInserted(0, added);
                fragment.mLayoutManager.scrollToPositionWithOffset(0,0);
            }
            fragment.swipeRefreshLayout.setRefreshing(false);
        }else{
            JsonElement cur;
            for (int i = 0; i < jsonElement.getAsJsonArray().size(); i++){
                cur = jsonElement.getAsJsonArray().get(i);
                Log.d(TAG, "onPostExecute: " + cur);
                HistoryRecord curItem = new HistoryRecord(cur);
                fragment.history_records.add(curItem);
            }
            fragment.mAdapter.notifyDataSetChanged();
        }
    }
}
