package com.example.aichef.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.example.aichef.AIchiefClient;
import com.example.aichef.R;
import com.example.aichef.asyncTask.DeleteFavoriteAsync;
import com.example.aichef.asyncTask.GetDetailAsync;
import com.example.aichef.model.DeleteFavoriteinput;

import java.util.List;
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter{



    List<Map<String,String>> pre;
    Context pre_text;


    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView13;
        public TextView textView14;
        public TextView textView15;
        public Button delete_favorite_button;
        public Button detail_favorite_button;

        public ViewHolder(View view){
            super(view);
            textView13 = view.findViewById(R.id.textView13);
            textView14 = view.findViewById(R.id.textView14);
            textView15 = view.findViewById(R.id.textView15);
            delete_favorite_button = view.findViewById(R.id.delete_favorite_button);
            detail_favorite_button = view.findViewById(R.id.detail_favorite_button);
        }
    }


    public FavoriteAdapter(List<Map<String,String>> pre, Context pre_text){
        this.pre = pre;
        this.pre_text = pre_text;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_delete_item, parent, false);
        final FavoriteAdapter.ViewHolder holder = new FavoriteAdapter.ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FavoriteAdapter.ViewHolder)holder).textView13.setText(pre.get(position).get("name"));
        ((FavoriteAdapter.ViewHolder)holder).textView14.setText(pre.get(position).get("minutes"));
        ((FavoriteAdapter.ViewHolder)holder).textView15.setText(pre.get(position).get("tags"));
        ((FavoriteAdapter.ViewHolder)holder).delete_favorite_button.setText("delete");
        ((FavoriteAdapter.ViewHolder)holder).detail_favorite_button.setText("detail");

        final FavoriteAdapter.ViewHolder tmp_holder = (FavoriteAdapter.ViewHolder)holder;
        ((FavoriteAdapter.ViewHolder)holder).delete_favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tmp_holder.getAdapterPosition();
                DeleteFavoriteAsync new_task = new DeleteFavoriteAsync(AWSMobileClient.getInstance().getUsername(),pre.get(position).get("recipe_id"));
                new_task.execute();
                pre.remove(position);
                notifyItemRemoved(position);
            }
        });

        ((FavoriteAdapter.ViewHolder)holder).detail_favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tmp_holder.getAdapterPosition();

                ProgressDialog waitingDialog = new ProgressDialog(pre_text);
                waitingDialog.setTitle("Please wait");
                waitingDialog.setMessage("Waiting");
                waitingDialog.setIndeterminate(true);
                waitingDialog.setCancelable(false);
                waitingDialog.show();
                GetDetailAsync new_task = new GetDetailAsync(pre_text,pre.get(position).get("recipe_id"),pre.get(position).get("name"),waitingDialog);
                new_task.execute();
            }
        });


    }

    @Override
    public int getItemCount() {
        return pre.size();
    }
}
