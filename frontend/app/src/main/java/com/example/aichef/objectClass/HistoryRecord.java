package com.example.aichef.objectClass;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;

import java.util.Arrays;

public class HistoryRecord {
    public String[] ingredients;
    public String photo_filename;
    public String picture_url;
    public String search_time;

    public HistoryRecord(JsonElement cur){
        String url = cur.getAsJsonObject().get("picture_url").getAsString();
        photo_filename = url.substring(url.lastIndexOf('/') + 1);;
        picture_url = url;
        setIngredients(cur.getAsJsonObject().get("ingredients"));
        search_time = cur.getAsJsonObject().get("search_time").getAsString();
    }
    public String[] getIngredients() {
        return ingredients;
    }
    public String getIngredientsStr(){
        StringBuilder builder = new StringBuilder();
        for (String value : ingredients) {
            builder.append(value);
            builder.append(", ");
        }
        builder.delete(builder.length() -2, builder.length());
        return builder.toString();
    }
    public String getSearchTimeStr(){
        return "Time created: " + search_time;
    }
    public void setIngredients(JsonElement jsonArray) {
        Log.d("HistoryRecord", "setIngredients: " + jsonArray);
        ingredients = new String[jsonArray.getAsJsonArray().size()];
        for (int i = 0; i < jsonArray.getAsJsonArray().size(); i++){
            ingredients[i] = jsonArray.getAsJsonArray().get(i).getAsString();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null){
            return false;
        }else if (obj.getClass() != this.getClass()){
            return false;
        }
        return ((HistoryRecord)obj).picture_url.equals(this.picture_url) && ((HistoryRecord)obj).search_time.equals(this.search_time) ;
    }
}
