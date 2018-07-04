package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

public class Product {
    private String name;
    private String price;

    public Product(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView price;

        public Holder(View view) {
            super(view);

            name = view.findViewById(R.id.name_product);
            name = view.findViewById(R.id.price_product);
        }
    }
}
