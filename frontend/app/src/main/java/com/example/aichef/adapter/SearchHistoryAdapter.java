package com.example.aichef.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.example.aichef.objectClass.HistoryRecord;
import com.example.aichef.R;
import com.example.aichef.activity.AdditionalIngredientActivity;

import org.json.JSONException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class SearchHistoryAdapter extends RecyclerView.Adapter {
    private static String TAG = "SearchHistoryAdapter";
    private ArrayList<HistoryRecord> records;

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AmazonS3Client s3Client;
    private String bucketName;

    static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView itemView;
        public RelativeLayout progressLayout;
        public ImageView imageView;
        public TextView timeCreateText;
        public TextView ingrediendtsText;

        public ViewHolder(View view){
            super(view);
            itemView = view.findViewById(R.id.history_item_view);
            progressLayout = view.findViewById(R.id.history_image_progressLayout);
            imageView = view.findViewById(R.id.history_item_image);
            timeCreateText = view.findViewById(R.id.history_item_time);
            ingrediendtsText = view.findViewById(R.id.history_item_ingredients);
        }
    }

    public SearchHistoryAdapter(ArrayList<HistoryRecord> records, SwipeRefreshLayout swipeRefreshLayout, Context context){
        this.context = context;
        this.records = records;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void initS3(){
        try {
            String regionString = new AWSConfiguration(context)
                    .optJsonObject("S3TransferUtility")
                    .getString("Region");
            s3Client = new AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(regionString));
            bucketName = new AWSConfiguration(context)
                    .optJsonObject("S3TransferUtility")
                    .getString("Bucket");
            TransferNetworkLossHandler.getInstance(context);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        final ViewHolder holder =new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!swipeRefreshLayout.isRefreshing()){
                    int position = getInvertPosition(holder.getAdapterPosition());
                    Log.d(TAG, "onClick: History Record clicked. Position: "+ position);
                    HistoryRecord curItem = records.get(position);
                    AdditionalIngredientActivity.actionStart(context, curItem.getIngredients(), curItem.picture_url, true);
                }else{
                    Toast.makeText(context,R.string.wait_refresh_done, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        position = getInvertPosition(position);
        HistoryRecord curItem = records.get(position);
        ((ViewHolder)holder).timeCreateText.setText(curItem.getSearchTimeStr());
        ((ViewHolder)holder).ingrediendtsText.setText(curItem.getIngredientsStr());
        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), curItem.photo_filename);
        if (!file.exists()){
            ((ViewHolder)holder).progressLayout.setVisibility(View.VISIBLE);
            TransferUtility transferUtility = TransferUtility.builder()
                    .context(context)
                    .s3Client(s3Client)
                    .awsConfiguration(new AWSConfiguration(context))
                    .build();
            //onUserStateChanged is activated again by transferUtility
            final WeakReference<RecyclerView.ViewHolder> holderWeakReference = new WeakReference<>(holder);
            final TransferObserver transferObserver = transferUtility.download(bucketName, curItem.photo_filename, file, new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                        Log.d(TAG, "onStateChanged: Download image done");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RecyclerView.ViewHolder temp_holder = holderWeakReference.get();
                                if (temp_holder != null){
                                    ((ViewHolder)holder).progressLayout.setVisibility(View.GONE);
                                    Glide.with(context).load(file).into(((ViewHolder) holder).imageView);
                                }
                            }
                        });
                    } else if (state.equals(TransferState.FAILED)) {
                        Log.d(TAG, "onStateChanged: Failed to download image");
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

                @Override
                public void onError(int id, Exception ex) { }
            });
        }else{
            Glide.with(context).load(file).into(((ViewHolder) holder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private int getInvertPosition(int position){
        return (getItemCount()-1) - position;
    }


}
