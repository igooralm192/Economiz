package com.example.igor.projetopoo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.igor.projetopoo.R;

public class ListFragment extends Fragment {
    private RecyclerView list;
    private OnListFragmentSettings onListFragmentSettings;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment getInstance(OnListFragmentSettings onListFragmentSettings) {
        ListFragment listFragment = new ListFragment();
        listFragment.onListFragmentSettings = onListFragmentSettings;
        return listFragment;
    }

    public RecyclerView getList() {
        return list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        list = view.findViewById(R.id.list_fragment);
        list = onListFragmentSettings.setList(list);

        return view;
    }

    public interface OnListFragmentSettings {
        public RecyclerView setList(RecyclerView lista);
    }

}
