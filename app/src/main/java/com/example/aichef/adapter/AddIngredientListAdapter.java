package com.example.aichef.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aichef.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AddIngredientListAdapter extends RecyclerView.Adapter {

    private String[] recogIngredientsList;
    private ArrayList<String> addIngredientsList;
    private static String TAG = "IngredientListAdapter";
//    private String selfAddress;

    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView deleteIcon;
        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.ingredient_item_name);
            deleteIcon = view.findViewById(R.id.ingredient_item_delete);
        }
    }

    public AddIngredientListAdapter(String[] recogIngredientsList, boolean modifyRecog){
        if (!modifyRecog){
            this.recogIngredientsList = recogIngredientsList;
            this.addIngredientsList = new ArrayList<String>();
        }else{
            this.recogIngredientsList = new String[0];
            this.addIngredientsList = new ArrayList<String>();
            Collections.addAll(this.addIngredientsList, recogIngredientsList);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ingredient_list_item, parent, false);
        final ViewHolder holder =new ViewHolder(view);
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int len1 = addIngredientsList.size();
                int position = holder.getAdapterPosition();
                addIngredientsList.remove((len1-1)-position);
                notifyItemRemoved(position);
            }
        });

        return holder;
    }

    //virtually concatenate recogIngredientsList after addIngredientsList.
    //addIngredientsList is displayed in reverse order so that the newly add item is on top
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int len1 = addIngredientsList.size();
        if (position < len1){
            ((ViewHolder)holder).name.setText(addIngredientsList.get((len1-1)-position));
            ((ViewHolder)holder).deleteIcon.setVisibility(View.VISIBLE);
        }else{
            ((ViewHolder)holder).name.setText(recogIngredientsList[position - len1]);
            ((ViewHolder)holder).deleteIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addIngredientsList.size() + recogIngredientsList.length;
    }

    public void addIngredient(String ingredient){
        addIngredientsList.add(ingredient);
        notifyItemInserted(0);

    }

    public String[] getAllIngredientsArray(){
        String[] addIngredientsArray = addIngredientsList.toArray(new String[0]);
        String[] allIngredients = new String[addIngredientsArray.length + recogIngredientsList.length];
        System.arraycopy(recogIngredientsList, 0, allIngredients, 0, recogIngredientsList.length);
        System.arraycopy(addIngredientsArray, 0, allIngredients, recogIngredientsList.length, addIngredientsArray.length);
        return allIngredients;
    }
}
