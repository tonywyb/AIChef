package com.example.aichef.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.R;
import com.example.aichef.asyncTask.GetDetailAsync;
import com.example.aichef.model.SearchRecipeinput;
import com.example.aichef.model.SearchRecipeinputIngredientsItem;
import com.example.aichef.model.SearchRecipeoutput;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayMenuActivity extends AppCompatActivity {

    List<Map<String,String>> pre = new ArrayList<>();
    private ImageView returnButton;
    private TextView toolbarTitle;
    private TextView toolbarNext;

    public static void actionStart(Context context,String allIngredients,String photoS3Url){
        Intent intent = new Intent(context, DisplayMenuActivity.class);
        intent.putExtra("ingredients",allIngredients);
        intent.putExtra("photoS3Url",photoS3Url);
        context.startActivity(intent);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_display_menu);

        returnButton = findViewById(R.id.toolbar_return);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("show menus");
        toolbarNext = findViewById(R.id.toolbar_next);

        final ListView menushow = findViewById(R.id.menu_card);
        final String user_id = AWSMobileClient.getInstance().getUsername();

        Intent intent = getIntent();
        final String final_result = intent.getStringExtra("ingredients");


        JSONObject json = JSONObject.parseObject(final_result);
        System.out.println(json);
        Object jsonArray = json.get("recipe_list");


        System.out.println("LOOK"+jsonArray);
        List<Recipe> list = JSON.parseArray(jsonArray+"", Recipe.class);
        for (Recipe recipe : list) {
            Map<String, String> showitem = new HashMap<String, String>();
            showitem.put("recipe_id", recipe.getrecipe_id());
            showitem.put("name", recipe.getname());
            showitem.put("minutes", recipe.getminutes()+"min");
            showitem.put("tags", recipe.gettags());
            pre.add(showitem);
        }


        SimpleAdapter myAdapter = new SimpleAdapter(this, pre, R.layout.favorite_item, new String[]{"name","minutes","tags"}, new int[]{R.id.textView13, R.id.textView14,R.id.textView15});
        menushow.setAdapter(myAdapter);

        final Context copyone = this;

        menushow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> show_click_item = pre.get(i);

                ProgressDialog waitingDialog = new ProgressDialog(copyone);
                waitingDialog.setTitle("Please wait");
                waitingDialog.setMessage("Waiting");
                waitingDialog.setIndeterminate(true);
                waitingDialog.setCancelable(false);
                waitingDialog.show();
                GetDetailAsync new_task = new GetDetailAsync(copyone,show_click_item.get("recipe_id"),show_click_item.get("name"),waitingDialog);
                new_task.execute();
            }
        });


        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                finish();
            }
        });

        toolbarNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Map<String, String> show_click_item = pre.get(0);

                ProgressDialog waitingDialog = new ProgressDialog(copyone);
                waitingDialog.setTitle("Please wait");
                waitingDialog.setMessage("Waiting");
                waitingDialog.setIndeterminate(true);
                waitingDialog.setCancelable(false);
                waitingDialog.show();
                GetDetailAsync new_task = new GetDetailAsync(copyone,show_click_item.get("recipe_id"),show_click_item.get("name"),waitingDialog);
                new_task.execute();
            }
        });

    }

}

class Recipe {
    private String recipe_id;
    private String minutes;
    private String name;
    private String tags;

    public Recipe(String recipe_id, String minutes,String name,String tags) {
        this.recipe_id = recipe_id;
        this.minutes = minutes;
        this.name = name;
        this.tags = tags;
    }

    public Recipe() {
    }

    public String getminutes() {
        return minutes;
    }

    public void setminutes(String minutes) {
        this.minutes = minutes;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String gettags() {
        String tmp = "";
        JSONArray jsonArray_ingre = JSON.parseArray(tags);
        for(int i=0;i<jsonArray_ingre.size();i++){
            tmp += jsonArray_ingre.get(i)+"  ";
        }
        return tmp;
    }

    public void settags(String tags) {
        this.tags = tags;
    }

    public String getrecipe_id() {
        return recipe_id;
    }

    public void setrecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }
}