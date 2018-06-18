package com.example.igor.projetopoo.activity.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.igor.projetopoo.R;

public class SuggestionHolder extends RecyclerView.ViewHolder {

    private final int id;
    protected TextView title;
    protected TextView subtitle;

    public SuggestionHolder(View itemView){
        super(itemView);
        id = R.layout.activity_search;
        title = itemView.findViewById(id);
    }

}
