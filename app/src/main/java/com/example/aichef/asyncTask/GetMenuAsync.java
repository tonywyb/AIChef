package com.example.aichef.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;

import com.example.aichef.activity.DisplayMenuActivity;
import com.example.aichef.model.SearchRecipeinput;
import com.example.aichef.model.SearchRecipeinputIngredientsItem;
import com.example.aichef.model.SearchRecipeoutput;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;



public class GetMenuAsync extends AsyncTask<Void, Void, String[]> {
    private String[] allIngredients;
    private String search_time;
    private String user_id;
    private String photoS3Url;

    private Context context;
    private ProgressDialog waitingDialog;

    public GetMenuAsync(String[] allIngredients, String user_id, String photoS3Url, Context context, ProgressDialog waitingDialog){
        this.allIngredients = allIngredients;

        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.search_time = dateFormat.format(date);

        this.user_id = user_id;
        this.photoS3Url = photoS3Url;
        this.context = context;
        this.waitingDialog = waitingDialog;
    }


    @Override
    protected String[] doInBackground(Void... voids) {
        ApiClientFactory factory = new ApiClientFactory();
        final AIchiefClient client = factory.build(AIchiefClient.class);
        SearchRecipeinput flow = new SearchRecipeinput();

        List<SearchRecipeinputIngredientsItem> ingredients = new ArrayList<>();
        for(int i=0;i<allIngredients.length;i++){
            SearchRecipeinputIngredientsItem tmp = new SearchRecipeinputIngredientsItem();
            tmp.setName(allIngredients[i]);
            ingredients.add(tmp);
        }

        flow.setIngredients(ingredients);
        flow.setSearchTime(search_time);
        flow.setUserId(user_id);
        flow.setPictureUrl(photoS3Url);

        SearchRecipeoutput result = client.searchrecipePost(flow);
        String final_result = result.getBody();

        waitingDialog.dismiss();
        DisplayMenuActivity.actionStart(context,final_result,photoS3Url);


        return new String[]{final_result};
    }
}

