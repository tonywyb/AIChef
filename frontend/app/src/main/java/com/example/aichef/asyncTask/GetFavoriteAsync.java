package com.example.aichef.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.adapter.FavoriteAdapter;
import com.example.aichef.model.GetFavoriteinput;
import com.example.aichef.model.GetFavoriteoutput;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetFavoriteAsync extends AsyncTask<Void, Void, String> {
    private WeakReference<RecyclerView> filled;
    private WeakReference<Context> pre_text;
    private WeakReference<SwipeRefreshLayout> refresh;
    public List<Map<String,String>> pre;
    public FavoriteAdapter favo_adaptor;

    public GetFavoriteAsync(RecyclerView filled, Context pre_text){
        this.filled = new WeakReference<>(filled);
        this.pre_text = new WeakReference<>(pre_text);
        this.refresh = null;
        this.pre = new ArrayList<>();
        this.favo_adaptor = new FavoriteAdapter(pre,this.pre_text.get());
    }

    public GetFavoriteAsync(RecyclerView filled, Context pre_text,SwipeRefreshLayout refresh){
        this.filled = new WeakReference<>(filled);
        this.pre_text = new WeakReference<>(pre_text);
        this.refresh = new WeakReference<>(refresh);
        this.pre = new ArrayList<>();
        this.favo_adaptor = new FavoriteAdapter(pre,this.pre_text.get());
    }


    @Override
    protected String doInBackground(Void... voids) {
        ApiClientFactory factory = new ApiClientFactory();
        final AIchiefClient client = factory.build(AIchiefClient.class);
        GetFavoriteinput flow = new GetFavoriteinput();

        String ret_val = "";

        String user_id = AWSMobileClient.getInstance().getUsername();
        if(user_id==null){
            ret_val = "";
            return null;
        }
        flow.setUserId(user_id);
        GetFavoriteoutput result = client.getfavoritePost(flow);
        ret_val = result.getBody();


        JSONObject json = JSONObject.parseObject(ret_val);
        System.out.println(json);
        Object jsonArray = json.get("result");
        System.out.println(jsonArray+"");

        List<recipe_detail> list = JSON.parseArray(jsonArray+"", recipe_detail.class);
        for (recipe_detail recipe : list) {
            Map<String, String> showitem = new HashMap<String, String>();
            showitem.put("recipe_id", recipe.recipe_id);
            showitem.put("name", recipe.name);
            showitem.put("minutes", recipe.minutes+"min");
            showitem.put("tags", recipe.gettags());
            pre.add(showitem);
        }

        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        //SimpleAdapter myAdapter = new SimpleAdapter(this.getActivity(), pre, R.layout.favorite_delete_item, new String[]{"title", "description","time"}, new int[]{R.id.textView13, R.id.textView14,R.id.textView15});
        //filled.setAdapter(myAdapter);

        FavoriteAdapter favo_adaptor = new FavoriteAdapter(pre,pre_text.get());

        LinearLayoutManager favo_layoutmanager = new LinearLayoutManager(pre_text.get());
        filled.get().setLayoutManager(favo_layoutmanager);
        filled.get().setAdapter(favo_adaptor);
        if(refresh!=null) {
            refresh.get().setRefreshing(false);
        }


    }
}

class recipe_detail{

    String recipe_id;
    String minutes;
    String name;
    String tags;
    public recipe_detail(){}

    public void setrecipe_id(String recipe_id){
        this.recipe_id = recipe_id;
    }
    public String getrecipe_id(){return recipe_id;}

    public void setminutes(String minutes){
        this.minutes = minutes;
    }
    public String getminutes(){return minutes;}

    public void setname(String name){
        this.name = name;
    }
    public String getname(){return name;}

    public void settags(String tags){
        this.tags = tags;
    }
    public String gettags(){
        tags = tags.replace("'","");
        tags = tags.replace("[","");
        tags = tags.replace("]","");
        tags = tags.replace(" ","");
        tags = tags.replace(",","   ");
        return tags;
    }

}
