package com.example.igor.projetopoo.entities;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Category {
    private String name;
    private String parentCategory;
    private Boolean haveSubcategories;

    public Category(String name, String parentCategory, Boolean haveSubcategories) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.haveSubcategories = haveSubcategories;
    }

    public Category(Map<String, Object> map) {
        this((String) map.get("name"), (String) map.get("parent_category"), (Boolean) map.get("have_subcategories"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCategory() { return parentCategory; }

    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public Boolean getHaveSubcategories() { return haveSubcategories; }

    public void setHaveSubcategories(Boolean haveSubcategories) {
        this.haveSubcategories = haveSubcategories;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("name", this.getName());
            object.put("parent_category", this.getParentCategory());
            object.put("have_subcategories", this.getHaveSubcategories());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public Category category;

        public Holder(View view, final ListGenericAdapter.OnItemViewClickListener listener) {
            super(view);

            name = view.findViewById(R.id.name_category);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCategoryClick( Holder.this.getCategory() );
                }
            });
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }
    }
}