package com.example.igor.projetopoo.activity.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.search.Item;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class SuggestionAdapter extends SuggestionsAdapter<Item, SuggestionAdapter.SuggestionHolder> {

    public SuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(Item suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getProductName());
        holder.subtitle.setText(String.valueOf(suggestion.getPrice()));
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.searchbar, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public int getSingleViewHeight() {
        return 0;
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder{

            private final int id;
            protected TextView title;
            protected TextView subtitle;

            public SuggestionHolder(View itemView){
                super(itemView);
                id = R.layout.activity_search;
                title = itemView.findViewById(id);
            }

    }

}
