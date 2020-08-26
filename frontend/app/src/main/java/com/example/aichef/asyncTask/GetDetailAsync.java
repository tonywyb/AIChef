package com.example.aichef.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.activity.DisplayDetailActivity;
import com.example.aichef.model.GetRecipeDetailinput;
import com.example.aichef.model.GetRecipeDetailoutput;

public class GetDetailAsync extends AsyncTask<Void, Void, String[]> {
    private String menu_id;
    private Context context;
    private String menu_name;
    private ProgressDialog waitingDialog;
    public GetDetailAsync(Context context, String menu_id, String menu_name, ProgressDialog waitingDialog){
        this.menu_id = menu_id;
        this.context = context;
        this.menu_name = menu_name;
        this.waitingDialog = waitingDialog;
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        ApiClientFactory factory = new ApiClientFactory();
        final AIchiefClient client = factory.build(AIchiefClient.class);
        GetRecipeDetailinput flow = new GetRecipeDetailinput();

        flow.setRecipeId(menu_id);
        GetRecipeDetailoutput result = client.getrecipedetailPost(flow);
        String ret_val = result.getBody();

        waitingDialog.dismiss();

        DisplayDetailActivity.actionStart(context,ret_val,menu_id, menu_name,AWSMobileClient.getInstance().getUsername());

        return new String[0];
    }
}
