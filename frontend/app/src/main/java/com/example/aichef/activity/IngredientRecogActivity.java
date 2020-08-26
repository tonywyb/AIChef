package com.example.aichef.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.aichef.R;
import com.example.aichef.adapter.IngredientListAdapter;
import com.example.aichef.asyncTask.UploadAndRecogImageAsync;


public class IngredientRecogActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "IngredientRecogActivity";
    static TransferUtility transferUtility;
    public String photoS3Url;

    private ImageView returnButton;
    private TextView toolbarTitle;
    public TextView toolbarNext;

    private Bitmap photo;
    public ImageView photoDisplay;
    public LinearLayout progressLayout;

    public LinearLayout resultLayout;
    private ImageView resultPhotoDisplay;
    public RecyclerView resultRecycler;
    public IngredientListAdapter ingredientListAdapter;

    public static void actionStart(Context context, String photoPath) {
        Intent intent = new Intent(context, IngredientRecogActivity.class);
        intent.putExtra("photoPath", photoPath);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_recog);

        Intent intent = getIntent();
        String photoPath = intent.getStringExtra("photoPath");
        Log.d(TAG, "onCreate: " + photoPath);
        photo = BitmapFactory.decodeFile(photoPath);
        int new_height, new_width;
        int height = photo.getHeight();
        int width = photo.getWidth();
        if (height > width){
            new_width = 600;
            new_height = (int)(600*((float)height/width));
        }else{
            new_height = 600;
            new_width = (int)(600*((float)width/height));
        }
        photo = Bitmap.createScaledBitmap(photo,new_width, new_height,true );
        returnButton = findViewById(R.id.toolbar_return);
        returnButton.setOnClickListener(this);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.recognize_ingredients);
        toolbarNext = findViewById(R.id.toolbar_next);
        toolbarNext.setOnClickListener(this);
        toolbarNext.setVisibility(View.GONE);


        photoDisplay = findViewById(R.id.photo_display);
        photoDisplay.setImageBitmap(photo);
        photoDisplay.setOnClickListener(this);
        progressLayout = findViewById(R.id.recog_ingred_progressLayout);

        resultLayout = findViewById(R.id.recog_result_layout);
        resultPhotoDisplay = findViewById(R.id.ingred_recog_result_photo);
        resultPhotoDisplay.setImageBitmap(photo);
        resultRecycler = findViewById(R.id.ingred_recog_result_recycler);
        resultRecycler.setLayoutManager(new LinearLayoutManager(this));

//        uploadAndRecogPhoto(photoPath);
        UploadAndRecogImageAsync uploadImageAsync= new UploadAndRecogImageAsync(this, photoPath, photo);
        uploadImageAsync.execute();
    }

//    private void move_image_animation(){
//        Log.d(TAG, "move_image_animation: "+"Start moving");
//        float scale = (float)(150.0/400.0);
//        ScaleAnimation animation = new ScaleAnimation(1f,scale,1f, scale,Animation.RELATIVE_TO_SELF,.5f, Animation.RELATIVE_TO_SELF, 0f);
//        animation.setDuration(500);
//        animation.setFillAfter(false);
//        animation.setAnimationListener(new MyAnimationListener());
//
//        photoDisplay.startAnimation(animation);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photo_display:
                break;
            case R.id.toolbar_return:
                this.finish();
                break;
            case R.id.toolbar_next:
                Log.d(TAG, "onClick: ingredient set " + ingredientListAdapter.checkedIngredients);
                AdditionalIngredientActivity.actionStart(this, ingredientListAdapter.checkedIngredients.toArray(new String[0]), photoS3Url);

        }
    }

//    private class MyAnimationListener implements Animation.AnimationListener {
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            photoDisplay.clearAnimation();
//            photoDisplay.setVisibility(View.GONE);
//            resultPhotoDisplay.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//
//    }
}
