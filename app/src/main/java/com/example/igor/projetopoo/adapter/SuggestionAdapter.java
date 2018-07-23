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

// Classe para adaptador da lista da barra de pesquisa

public class SuggestionAdapter extends SuggestionsAdapter<Item, SuggestionAdapter.Holder> {
    private SuggestionAdapter.OnItemViewClickListener listener;

    public SuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    public void setOnItemViewClickListener(SuggestionAdapter.OnItemViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindSuggestionHolder(Item suggestion, Holder holder, int position) {
        holder.icon.setImageResource(suggestion.getIdIcon());
        holder.name.setText(suggestion.getName());
        holder.price.setText(suggestion.getPrice());
    }

    @Override
    public int getSingleViewHeight() {
        return 50;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_suggestion, parent, false);

        return new Holder(view, listener);
    }

    public static class Holder extends RecyclerView.ViewHolder {
        protected ImageView icon;
        protected TextView name;
        protected TextView price;

        public Holder(View view, final SuggestionAdapter.OnItemViewClickListener listener) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view);
                }
            });

            icon = view.findViewById(R.id.icon_suggestion);
            name = view.findViewById(R.id.name_suggestion);
            price = view.findViewById(R.id.price_suggestion);
        }
    }

    public interface OnItemViewClickListener {
        void onItemClick(View view);
    }

}
