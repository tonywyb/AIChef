package com.example.aichef.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.example.aichef.R;

public class AuthIntentHandlingActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "AuthIntentHandlingActivity";

    private TextView result;
    private boolean done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_intent_handling);
        done = false;

        TextView title = findViewById(R.id.toolbar_title);
        title.setText("");
        TextView next = findViewById(R.id.toolbar_next);
        next.setVisibility(View.INVISIBLE);
        ImageView returnButton = findViewById(R.id.toolbar_return);
        returnButton.setOnClickListener(this);
        result = findViewById(R.id.auth_result);
        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails details) {
                Log.d(TAG, "onUserStateChanged: AWS Login status: " + details.getUserState());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (AWSMobileClient.getInstance().isSignedIn()){
                            done = true;
                            String greeting = "Welcome, \n"+ AWSMobileClient.getInstance().getUsername();
                            result.setText(greeting);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent activityIntent = getIntent();
        if (activityIntent.getData() != null &&
                "myapp".equals(activityIntent.getData().getScheme())) {
            Log.d(TAG, "onResume: " + "Executing AWS Login steps");
            AWSMobileClient.getInstance().handleAuthResponse(activityIntent);
            if (activityIntent.getData().toString().contains("signout")){
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        done = true;
                        result.setText(R.string.signed_out_success);
                    }
                }, 2 * 1000);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 5 * 1000);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_return){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (done){
            finish();
        }
    }
}
