package com.example.igor.projetopoo.entities;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

import java.io.Serializable;

public abstract class Entity implements Comparable<Entity>, Serializable {
    private String id;
    private String name;
    private String parentCategory;
    private Number backgroundCategory;

    public Entity(String id, String name, String parentCategory, Number backgroundCategory) {
        this.id = id;
        this.name = name;
        this.parentCategory = parentCategory;
        this.backgroundCategory = backgroundCategory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Number getBackgroundCategory() {
        return backgroundCategory;
    }

    public void setBackgroundCategory(Number backgroundCategory) {
        this.backgroundCategory = backgroundCategory;
    }

    @Override
    public int compareTo(@NonNull Entity entity) {
        return this.getName().compareTo(entity.getName());
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public Entity entity;

        public Holder(View view, final ListGenericAdapter.OnItemViewClickListener listener) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getEntity() instanceof Category)
                        listener.onCategoryClick((Category) getEntity());
                    else
                        listener.onProductClick((Product) getEntity());
                }
            });

            name = view.findViewById(R.id.name_entity);
            price = view.findViewById(R.id.price_entity);
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }
    }
}
