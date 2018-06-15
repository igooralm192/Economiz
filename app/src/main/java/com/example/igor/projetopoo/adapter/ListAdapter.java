package com.example.igor.projetopoo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public interface ListAdapter<T, VH extends RecyclerView.ViewHolder> {
    VH onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType);
    void onBindViewHolder(List<T> items, @NonNull VH holder, int position);
}
