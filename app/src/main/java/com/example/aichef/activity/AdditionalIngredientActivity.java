package com.example.aichef.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.example.aichef.R;
import com.example.aichef.adapter.AddIngredientListAdapter;
import com.example.aichef.asyncTask.GetMenuAsync;

import java.util.Arrays;

public class AdditionalIngredientActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "AdditionalIngredientActivity";
    private String[] recogIngredients;
    private String photoS3Url;

    private ImageView returnButton;
    private TextView toolbarTitle;
    private TextView toolbarNext;

    private com.google.android.material.textfield.TextInputLayout textField;
    private Button addButton;
    private RecyclerView ingredientsRecycler;

    private AddIngredientListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    public static void actionStart(Context context, String[] recognized_ingredients, String photoS3Url) {
        Intent intent = new Intent(context, AdditionalIngredientActivity.class);
        intent.putExtra("Ingredients", recognized_ingredients);
        intent.putExtra("photoS3Url", photoS3Url);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String[] recognized_ingredients, String photoS3Url, boolean modifyRecog) {
        Intent intent = new Intent(context, AdditionalIngredientActivity.class);
        intent.putExtra("Ingredients", recognized_ingredients);
        intent.putExtra("photoS3Url", photoS3Url);
        intent.putExtra("modifyRecog", modifyRecog);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_ingredient);

        Intent intent = getIntent();
        recogIngredients = intent.getStringArrayExtra("Ingredients");
        photoS3Url = intent.getStringExtra("photoS3Url");
        boolean modifyRecog = intent.getBooleanExtra("modifyRecog", false);
        Log.d(TAG, "onCreate: " + Arrays.toString(recogIngredients));

        returnButton = findViewById(R.id.toolbar_return);
        returnButton.setOnClickListener(this);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.additional_ingred);
        toolbarNext = findViewById(R.id.toolbar_next);
        toolbarNext.setOnClickListener(this);

        textField = findViewById(R.id.add_ingred_bar_textField);
        addButton = findViewById(R.id.add_ingred_bar_button);
        addButton.setOnClickListener(this);
        ingredientsRecycler = findViewById(R.id.add_ingred_recycler);
        mLayoutManager = new LinearLayoutManager(this);
        ingredientsRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AddIngredientListAdapter(recogIngredients, modifyRecog);
        ingredientsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_return:
                finish();
                break;
            case R.id.toolbar_next:
                String[] allIngredients = mAdapter.getAllIngredientsArray();
                Log.d(TAG, "onClick: Invoking next activity " + Arrays.toString(allIngredients)+photoS3Url);



                ProgressDialog waitingDialog = new ProgressDialog(this);
                waitingDialog.setTitle("Please wait");
                waitingDialog.setMessage("Waiting");
                waitingDialog.setIndeterminate(true);
                waitingDialog.setCancelable(false);
                waitingDialog.show();
                GetMenuAsync task = new GetMenuAsync(allIngredients, AWSMobileClient.getInstance().getUsername(),photoS3Url,this,waitingDialog);
                task.execute();
                // Start the next activity for search options.
                break;
            case R.id.add_ingred_bar_button:
                String ingredient = textField.getEditText().getText().toString();
//                Toast.makeText(this, ingredient, Toast.LENGTH_LONG).show();
                textField.getEditText().setText("");
                mAdapter.addIngredient(ingredient);
                mLayoutManager.scrollToPositionWithOffset(0,0);
                break;
        }
    }
}
