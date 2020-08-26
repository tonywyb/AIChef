package com.example.aichef.asyncTask;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.model.DeleteFavoriteinput;

public class DeleteFavoriteAsync extends AsyncTask<Void, Void, String> {
    private String user_id;
    private String recipe_id;

    public DeleteFavoriteAsync(String user_id,String recipe_id){
        this.user_id = user_id;
        this.recipe_id = recipe_id;
    }

    @Override
    protected String doInBackground(Void... voids) {
        ApiClientFactory factory = new ApiClientFactory();
        final AIchiefClient client = factory.build(AIchiefClient.class);
        DeleteFavoriteinput flow = new DeleteFavoriteinput();
        flow.setUserId(user_id);
        flow.setRecipeId(recipe_id);
        client.deletefavoritePost(flow);

        return null;

    }
}
