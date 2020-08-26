package com.example.aichef.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.R;
import com.example.aichef.model.AddFavoriteinput;
import com.example.aichef.model.GetRecipeDetailinput;
import com.example.aichef.model.GetRecipeDetailoutput;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayDetailActivity extends AppCompatActivity {

    private String detail = "";
    private String menu_id = "";
    private String menu_name = "";
    private String user_id = "";


    private ImageView returnButton;
    private TextView toolbarTitle;
    private TextView toolbarNext;
    private TextView view_title;
    private TextView view_ingredients;
    private TextView view_description;
    private ListView view_step;
    private Button add_favorite;

    private Context text_this;


    public static void actionStart(Context context, String details,String menu_id,String menu_name,String user_id){
        Intent intent = new Intent(context, DisplayDetailActivity.class);
        intent.putExtra("details",details);
        intent.putExtra("menu_id",menu_id);
        intent.putExtra("menu_name",menu_name);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_detail);
        text_this = this;

        Intent intent = getIntent();
        detail = intent.getStringExtra("details");
        menu_id = intent.getStringExtra("menu_id");
        menu_name = intent.getStringExtra("menu_name");
        user_id = intent.getStringExtra("user_id");

        returnButton = findViewById(R.id.toolbar_return);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("show details");
        toolbarNext = findViewById(R.id.toolbar_next);
        toolbarNext.setText("finish");
        view_title = findViewById(R.id.textview_title);
        view_ingredients = findViewById(R.id.textview_ingredients);
        view_description = findViewById(R.id.textView_description);
        view_step = findViewById(R.id.listView_step);
        add_favorite = findViewById(R.id.button_favorite);



        //////////////////////////////

        JSONObject json = JSONObject.parseObject(detail);



        String json_description = ""+json.get("description");
        String json_ingredients = "";
        //JSONArray jsonArray = json.get("steps");
        JSONArray jsonArray = JSON.parseArray(json.get("steps")+"");
        JSONArray jsonArray_ingre = JSON.parseArray(json.get("ingredients")+"");
        for(int i=0;i<jsonArray_ingre.size();i++){
            json_ingredients += jsonArray_ingre.get(i)+"  ";
        }


        ArrayList<String> filt = new ArrayList<>();
        System.out.println("LOOK"+jsonArray);
        String json_steps = "";
        for(int i=0;i<jsonArray.size();i++){
            json_steps += (i+1)+"  "+jsonArray.get(i)+"\n";
            filt.add((i+1)+"  "+jsonArray.get(i));
        }


        view_title.setText(menu_name);
        view_ingredients.setText(json_ingredients);
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.simple_step_item, filt);
        view_step.setAdapter(adapter);
        view_description.setText(json_description);

        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                finish();
            }
        });

        toolbarNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                List<Activity> all_activities = getAllActivitys();
                for(int i=all_activities.size()-1;i>=0;i--){
                    if(all_activities.get(i).getClass().getName().equals("com.example.aichef.activity.MainActivity")){
                        continue;
                    }
                    all_activities.get(i).finish();
                }

            }
        });


        add_favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                // connect to backend
                showNormalDialog();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        ApiClientFactory factory = new ApiClientFactory();
                        final AIchiefClient client = factory.build(AIchiefClient.class);
                        AddFavoriteinput flow = new AddFavoriteinput();

                        flow.setUserId(user_id);
                        flow.setRecipeId(menu_id);
                        client.addfavoritePost(flow);

                        return null;
                    }


                }.execute();

            }
        });


    }

    private void showNormalDialog(){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setTitle("SUCCESS");
        normalDialog.setIcon(R.mipmap.ic_launcher_round);
        normalDialog.setMessage("You have successfully added it to your favorite!");
        normalDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(text_this,"confirm",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        normalDialog.create().show();
    }


    public List<Activity> getAllActivitys(){
        List<Activity> list=new ArrayList<>();
        try {
            Class<?> activityThread=Class.forName("android.app.ActivityThread");
            Method currentActivityThread=activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            //get object from main thread
            Object activityThreadObject=currentActivityThread.invoke(null);
            Field mActivitiesField = activityThread.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Map<Object,Object> mActivities = (Map<Object,Object>) mActivitiesField.get(activityThreadObject);
            for (Map.Entry<Object,Object> entry:mActivities.entrySet()){
                Object value = entry.getValue();
                Class<?> activityClientRecordClass = value.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Object o = activityField.get(value);
                list.add((Activity) o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
