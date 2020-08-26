package com.example.aichef.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.HostedUIOptions;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amulyakhare.textdrawable.TextDrawable;
import com.example.aichef.R;
import com.example.aichef.asyncTask.GetUserInfoAsync;
import com.example.aichef.fragment.FavoriteFragment;
import com.example.aichef.fragment.HistoryFragment;
import com.example.aichef.fragment.StartCookingFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";
    private UserState curState;

    private StartCookingFragment startCookingFragment;
    private FavoriteFragment favoriteFragment;
    private HistoryFragment historyFragment;
    private DrawerLayout drawerLayout;
    private ImageView drawerButton;
    private RelativeLayout userProfileImageLayout;
    private CircleImageView userProfileImage;

    private CircleImageView drawerUserImage;
    private TextView drawerUsername;
    private RelativeLayout drawerOptionLayout;
    private LinearLayout drawerStartCooking;
    private LinearLayout drawerHistory;
    private LinearLayout drawerFavorite;
    private LinearLayout drawerSignout;

    private TextDrawable profileImage;
    private LinearLayout prevOptionLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        curState = UserState.UNKNOWN;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        drawerLayout = findViewById(R.id.main_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        drawerButton = findViewById(R.id.toolbar_drawer);
        drawerButton.setOnClickListener(this);

        drawerButton = findViewById(R.id.toolbar_drawer);
        drawerButton.setOnClickListener(this);

        userProfileImageLayout = findViewById(R.id.user_profile_image_layout);
        userProfileImageLayout.setOnClickListener(this);
        userProfileImage = findViewById(R.id.user_profile_image);

        drawerUserImage = findViewById(R.id.drawer_user_profile_image);
        drawerUsername = findViewById(R.id.main_drawer_username);
        drawerOptionLayout = findViewById(R.id.main_drawer_options_layout);
        drawerStartCooking = findViewById(R.id.main_drawer_start_cooking);
        drawerStartCooking.setOnClickListener(this);
        drawerFavorite = findViewById(R.id.main_drawer_favorite);
        drawerFavorite.setOnClickListener(this);
        drawerHistory = findViewById(R.id.main_drawer_history);
        drawerHistory.setOnClickListener(this);
        drawerSignout = findViewById(R.id.main_drawer_logout);
        drawerSignout.setOnClickListener(this);

        initFragment();

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            }
        );
        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails details) {
                UserState newState = details.getUserState();
                Log.d(TAG, "onUserStateChanged: AWS Login status: " + details.getUserState());
                if (newState == curState){
                    return;
                }
                curState = newState;
                switch (details.getUserState()) {
                    case SIGNED_IN:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                              new GetUserInfoAsync(this).execute();
                                String username = AWSMobileClient.getInstance().getUsername();
                                Log.i(TAG, "onResume: " + username);
                                profileImage = TextDrawable.builder()
                                        .beginConfig()
                                        .height(userProfileImage.getLayoutParams().height)
                                        .width(userProfileImage.getLayoutParams().width)
                                        .endConfig()
                                        .buildRound(username.substring(0, 1).toUpperCase(), Color.parseColor("#01B2A1"));

                                userProfileImage.setImageDrawable(profileImage);
                                drawerUserImage.setImageDrawable(profileImage);
                                if (username.length() > 15) {
                                    username = username.substring(0, 15) + "...";
                                }
                                drawerUsername.setText(username);
                                drawerOptionLayout.setVisibility(View.VISIBLE);
                                Log.d(TAG, "run: load: execute");
                                historyFragment.load();
                                favoriteFragment.onRefresh();
                            }
                        });
                        break;
                    case SIGNED_OUT:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userProfileImage.setImageResource(R.drawable.empty_profile_public);
                                drawerUserImage.setImageResource(R.drawable.empty_profile_public);
                                drawerUsername.setText(R.string.not_logged_in);
                                drawerOptionLayout.setVisibility(View.INVISIBLE);
                                drawerLayout.closeDrawer(Gravity.LEFT);
                                drawerStartCooking.callOnClick();
                                historyFragment.clear();
                                favoriteFragment.clear();
                            }
                        });
                        break;
                }
            }
        });
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void initFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        startCookingFragment = new StartCookingFragment();
        transaction.add(R.id.main_view, startCookingFragment);
        drawerStartCooking.setBackgroundColor(Color.parseColor("#dddddd"));
        favoriteFragment = new FavoriteFragment();
        transaction.add(R.id.main_view, favoriteFragment);
        transaction.hide(favoriteFragment);
        historyFragment = new HistoryFragment();
        transaction.add(R.id.main_view, historyFragment);
        transaction.hide(historyFragment);
        transaction.commit();
        prevOptionLayout = drawerStartCooking;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_drawer:
                Log.d(TAG, "onClick: " + "Drawer");
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.user_profile_image_layout:
                Log.d(TAG, "onClick: " + "user");
                if (!AWSMobileClient.getInstance().isSignedIn()){
                    HostedUIOptions hostedUIOptions = HostedUIOptions.builder()
                            .scopes("openid", "email")
                            .build();
                    SignInUIOptions signInUIOptions = SignInUIOptions.builder()
                            .hostedUIOptions(hostedUIOptions)
                            .build();
                    AWSMobileClient.getInstance().showSignIn(this, signInUIOptions, new Callback<UserStateDetails>() {
                        @Override
                        public void onResult(UserStateDetails details) {
                            Log.d(TAG, "onResult: " + details.getUserState());
                        }
                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "onError: ", e);
                        }
                    });
                }else{
                    Toast.makeText(this,"You have already signed in.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_drawer_logout:
                AWSMobileClient.getInstance().signOut(SignOutOptions.builder().invalidateTokens(true).build(), new Callback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        Log.d(TAG, "onResult: ");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });
                break;
            case R.id.main_drawer_start_cooking: {
                prevOptionLayout.setBackgroundColor(Color.WHITE);
                drawerStartCooking.setBackgroundColor(Color.parseColor("#dddddd"));
                prevOptionLayout = drawerStartCooking;
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(favoriteFragment);
                transaction.hide(historyFragment);
                transaction.show(startCookingFragment);
                transaction.commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            }
            case R.id.main_drawer_favorite: {
                prevOptionLayout.setBackgroundColor(Color.WHITE);
                drawerFavorite.setBackgroundColor(Color.parseColor("#dddddd"));
                prevOptionLayout = drawerFavorite;
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(startCookingFragment);
                transaction.hide(historyFragment);
                transaction.show(favoriteFragment);
                transaction.commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            }
            case R.id.main_drawer_history: {
                prevOptionLayout.setBackgroundColor(Color.WHITE);
                drawerHistory.setBackgroundColor(Color.parseColor("#dddddd"));
                prevOptionLayout = drawerHistory;
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.hide(startCookingFragment);
                transaction.hide(favoriteFragment);
                transaction.show(historyFragment);
                transaction.commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favorite) {
//            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
