package com.example.igor.projetopoo.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.entities.Item;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

public class SuggestionAdapter extends SuggestionsAdapter<Item, SuggestionAdapter.Holder> {

    public SuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(Item suggestion, Holder holder, int position) {
        holder.icon.setImageResource(suggestion.getIdIcon());
        holder.name.setText(suggestion.getName());
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_suggestion, parent, false);

        return new Holder(view);
    }

    public static class Holder extends RecyclerView.ViewHolder {
        protected ImageView icon;
        protected TextView name;

        public Holder(View view) {
            super(view);

            icon = view.findViewById(R.id.icon_suggestion);
            name = view.findViewById(R.id.name_suggestion);
        }
    }
}
