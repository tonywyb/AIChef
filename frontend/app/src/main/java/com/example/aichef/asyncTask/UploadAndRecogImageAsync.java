package com.example.aichef.asyncTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.aichef.AIchiefClient;
import com.example.aichef.R;
import com.example.aichef.activity.IngredientRecogActivity;
import com.example.aichef.adapter.IngredientListAdapter;
import com.example.aichef.model.FoodRecognitioninput;
import com.example.aichef.model.FoodRecognitionoutput;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class UploadAndRecogImageAsync extends AsyncTask<Void, Void, String[]> {
    private static String TAG = "UploadAndRecogImageAsync";

    private WeakReference<IngredientRecogActivity> activityWeakReference;
    private TransferState transferState;
    private String photoPath;
    private Bitmap photo;
    private String bucketName;
    private String s3FileName;
    private Boolean done = false;
    private String photoS3Url;

    public UploadAndRecogImageAsync(IngredientRecogActivity activity, String photoPath, Bitmap photo){
        this.activityWeakReference = new WeakReference<>(activity);
        this.photoPath = photoPath;
        this.photo = photo;
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        Activity activity = activityWeakReference.get();
        try{
            File file = new File(photoPath);
            file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
            return null;
        }
        try {
            String regionString = new AWSConfiguration(activity)
                    .optJsonObject("S3TransferUtility")
                    .getString("Region");
            AmazonS3 s3 = new AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(regionString));
            TransferUtility transferUtility = TransferUtility.builder()
                    .context(activity)
                    .s3Client(s3)
                    .awsConfiguration(new AWSConfiguration(activity))
                    .build();
            bucketName = new AWSConfiguration(activity)
                    .optJsonObject("S3TransferUtility")
                    .getString("Bucket");
            s3FileName = AWSMobileClient.getInstance().getUsername() + "_" + Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis() + ".jpg";
            Log.i(TAG, "onCreate: Uploading "+ s3FileName + " to " + bucketName + " at " + regionString);
            final TransferObserver transferObserver = transferUtility.upload(bucketName, s3FileName, new File(photoPath));
            TransferNetworkLossHandler.getInstance(activity);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)){
                        Log.i(TAG, "onStateChanged: completed");
                        transferState = state;
                        done = true;
                    }else if (state.equals(TransferState.FAILED)){
                        Log.i(TAG, "onStateChanged: failed");
                        transferState = state;
                        done = true;
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

                @Override
                public void onError(int id, Exception ex) {
                    transferState = TransferState.FAILED;
                    ex.printStackTrace();
                    done = true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        while (!done){
            try {
//                Log.d(TAG, "doInBackground: waiting");
                Thread.sleep(10);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        Log.i(TAG, "doInBackground: Upload succeeded" );
        photoS3Url = "https://s3.amazonaws.com/"+ bucketName+ "/" + s3FileName;
        ApiClientFactory factory = new ApiClientFactory();
        final AIchiefClient client = factory.build(AIchiefClient.class);
        FoodRecognitioninput input = new FoodRecognitioninput();
        input.setPictureUrl(photoS3Url);
        input.setUserId(AWSMobileClient.getInstance().getUsername());
        FoodRecognitionoutput returnVal = client.foodrecognitionPost(input);
        JsonElement jsonElement =  new JsonParser().parse(returnVal.getBody());
        Log.d(TAG, "doInBackground: " + jsonElement);
        List<String> result = new ArrayList<String>();
        for (JsonElement e : jsonElement.getAsJsonArray()) {
            result.add(e.getAsJsonObject().get("name").getAsString());
        }
        return result.toArray(new String[0]);
//        String[] result = {"Red Onion", "Arrowroot", "Amaranth Leaves","Bamboo Shoots", "Brussel Sprouts", "Crookneck", "Cucumber","Mushroom", "Cabbage", "Asparagus"};
//        return result;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute: " + Arrays.toString(result));
        IngredientRecogActivity activity = activityWeakReference.get();
        if (activity != null){
            Log.d(TAG, "onPostExecute: not null");
            if (result == null){
                Toast.makeText(activity, R.string.upload_image_fail,Toast.LENGTH_LONG).show();
                activity.finish();
            }
            if (transferState.equals(TransferState.COMPLETED)) {
                Log.i(TAG, "onPostExecute: Recog and upload done");
                activity.photoS3Url = photoS3Url;
                activity.photoDisplay.setVisibility(View.GONE);
                activity.progressLayout.setVisibility(View.GONE);
                activity.ingredientListAdapter = new IngredientListAdapter(result);
                activity.resultRecycler.setAdapter(activity.ingredientListAdapter);
                activity.resultLayout.setVisibility(View.VISIBLE);
                activity.toolbarNext.setVisibility(View.VISIBLE);
            } else if (transferState.equals(TransferState.FAILED)) {
                Log.i(TAG, "onStateChanged: Upload failed" );
                Toast.makeText(activity, R.string.upload_image_fail,Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }else{
            Log.d(TAG, "onPostExecute: null");
        }
    }
}
