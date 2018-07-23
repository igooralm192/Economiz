package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

public class Category extends Entity implements Serializable {
    public Category(String id, String name, String parentCategory, Number backgroundCategory) {
        super(id, name, parentCategory, backgroundCategory);
    }

    public Category(String id, Map<String, Object> map) {
        this(id, (String) map.get("name"), (String) map.get("parent_category"), (Number) map.get("background_category"));
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", this.getId());
            object.put("name", this.getName());
            object.put("parent_category", this.getParentCategory());
            object.put("background_category", this.getBackgroundCategory());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static Category toObject(String json) {
        try {
            JSONObject object = new JSONObject(json);

            return new Category(
                    (String) object.get("id"),
                    (String) object.get("name"),
                    (String) object.get("parent_category"),
                    (Integer) object.get("background_category")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final ImageView background;
        public Category category;

        public Holder(View view, final ListGenericAdapter.OnItemViewClickListener listener) {
            super(view);

            name = (TextView) view.findViewById(R.id.name_main_category);
            background = (ImageView) view.findViewById(R.id.back_main_category);

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