package com.example.igor.projetopoo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.igor.projetopoo.entities.Category;

import java.util.List;

public class ListGenericAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Context context;
    private List<T> items;
    private ListAdapter<T, VH> listAdapter;

    public ListGenericAdapter(Context context, List<T> items, ListAdapter<T, VH> listAdapter) {
        this.context = context;
        this.items = items;
        this.listAdapter = listAdapter;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return listAdapter.onCreateViewHolder(context, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        listAdapter.onBindViewHolder(items, holder, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemViewClickListener {
        void onCategoryClick(Category category);
        //void onProductClick(Product product);
    }
}
