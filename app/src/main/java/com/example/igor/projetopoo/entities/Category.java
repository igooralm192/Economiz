package com.example.igor.projetopoo.entities;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

import java.util.Map;

public class Category {
    private String name;
    private String parentCategory;

    public Category(String name, String parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    public Category(Map<String, Object> map) {
        this((String) map.get("name"), (String) map.get("parent_category"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCategory() { return parentCategory; }

    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView name;

        public Holder(View view) {
            super(view);

            name = view.findViewById(R.id.name_category);
        }
    }
}