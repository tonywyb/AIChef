package com.example.aichef.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.example.aichef.R;
import com.example.aichef.activity.MainActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class GetUserInfoAsync extends AsyncTask<Void, Void, Map<String, String>> {
    private static String TAG = "GetUserInfoAsync";

    private WeakReference<Activity> activityWeakReference;

    public GetUserInfoAsync(Activity activity){
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Map<String, String> doInBackground(Void... voids) {
        try{
            Map<String, String> userAttributes = AWSMobileClient.getInstance().getUserAttributes();

            Log.d(TAG, "doInBackground: " + userAttributes );
            return userAttributes;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Map<String, String> stringStringMap) {

        String email;
        if (stringStringMap != null){
            email = stringStringMap.get("email");
        }else{
            email = "Failed to get email address.";
        }

        if (activityWeakReference != null){
//            TextView drawerEmail = activityWeakReference.get().findViewById(R.id.main_drawer_email);
//            drawerEmail.setText(email);
        }
    }
}
