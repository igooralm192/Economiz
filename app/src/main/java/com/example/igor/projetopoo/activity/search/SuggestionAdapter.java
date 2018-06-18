package com.example.igor.projetopoo.activity.search;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.igor.projetopoo.R;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class SuggestionAdapter extends SuggestionsAdapter<Product, SuggestionHolder> {

    public SuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(Product suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.getProductName());
        holder.subtitle.setText("Com preço médio de: R$ " + suggestion.getPrice());
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

}
