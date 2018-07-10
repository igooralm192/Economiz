package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

public class Category {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView name;

        public Holder(View view) {
            super(view);

            name = view.findViewById(R.id.name_category);
        }
    }
}
