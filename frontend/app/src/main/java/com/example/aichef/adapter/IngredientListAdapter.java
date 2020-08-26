package com.example.aichef.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aichef.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IngredientListAdapter extends RecyclerView.Adapter {

    private String[] ingredientsList;
    public Set<String> checkedIngredients;
    private static String TAG = "IngredientListAdapter";
//    private String selfAddress;

    static class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout itemView;
        public TextView name;
        public CheckBox checkBox;
        public ViewHolder(View view){
            super(view);
            itemView =view.findViewById(R.id.ingredient_item_view);
            name = view.findViewById(R.id.ingredient_item_name);
            checkBox = view.findViewById(R.id.ingredient_item_checkbox);
        }
    }

    public IngredientListAdapter(String[] ingredientsList){
        this.ingredientsList = ingredientsList;
        checkedIngredients = new HashSet<String>();
        checkedIngredients.addAll(Arrays.asList(ingredientsList));
//        this.selfAddress = selfAddress;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item, parent, false);
        final ViewHolder holder =new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: getAdapterPosition" + holder.getAdapterPosition() + ingredientsList[ holder.getAdapterPosition()]);
//                Log.d(TAG, "onClick: getLayoutPosition" + holder.getLayoutPosition() + ingredientsList[ holder.getLayoutPosition()]);
//                Log.d(TAG, "onClick: "+holder.name.getText().toString());
//                Log.d(TAG, "onClick: "+checkedIngredients.toString());
                int position = holder.getAdapterPosition();
                if (holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    checkedIngredients.remove(ingredientsList[position]);
                }else{
                    holder.checkBox.setChecked(true);
                    checkedIngredients.add(ingredientsList[position]);
                }
//              Log.d(TAG, "onClick: "+checkedIngredients.toString());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).name.setText(ingredientsList[position]);
        ((ViewHolder)holder).checkBox.setChecked(checkedIngredients.contains(ingredientsList[position]));
    }

    @Override
    public int getItemCount() {
        return ingredientsList.length;
    }
}
