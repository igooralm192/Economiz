package com.example.igor.projetopoo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.igor.projetopoo.R;

public class ListFragment extends Fragment {
    private RecyclerView list;
    private OnListSettingsListener onListSettingsListener;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment getInstance() { return new ListFragment(); }

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
        if (onListSettingsListener != null) list = onListSettingsListener.onListSettings(list);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListSettingsListener) {
            onListSettingsListener = (OnListSettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListSettingsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onListSettingsListener = null;
    }

    public interface OnListSettingsListener {
        RecyclerView onListSettings(RecyclerView lista);
    }


}
