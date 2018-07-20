package com.example.igor.projetopoo.entities;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

public abstract class Entitie implements Comparable<Entitie> {
    private String name;
    private String parentCategory;

    public Entitie(String name, String parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
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

    @Override
    public int compareTo(@NonNull Entitie entitie) {
        return this.getName().compareTo(entitie.getName());
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public Entitie entitie;

        public Holder(View view, final ListGenericAdapter.OnItemViewClickListener listener) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getEntitie() instanceof Category)
                        listener.onCategoryClick((Category) getEntitie());
                    else
                        listener.onProductClick((Product) getEntitie());
                }
            });

            name = view.findViewById(R.id.name_entitie);
            price = view.findViewById(R.id.price_entitie);
        }

        public Entitie getEntitie() {
            return entitie;
        }

        public void setEntitie(Entitie entitie) {
            this.entitie = entitie;
        }
    }
}
