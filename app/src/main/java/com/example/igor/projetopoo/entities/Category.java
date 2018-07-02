package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.igor.projetopoo.R;

public class Category {

    private String nameCategory;
    private int backCategory;

    public Category(String nameCategory, int backCategory) {
        this.nameCategory = nameCategory;
        this.backCategory = backCategory;
    }

    public String getName() {
        return nameCategory;
    }

    public int getBackground() {
        return backCategory;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public final TextView nameCategory;
        public final ImageView backCategory;

        public Holder(View view) {
            super(view);
            nameCategory = (TextView) view.findViewById(R.id.text_main_category2);
            backCategory = (ImageView) view.findViewById(R.id.image_main_category2);
        }

    }
}

