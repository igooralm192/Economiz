package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

public class Result {
    private int icon;
    private String name;
    private Number price;
    private Object object;

    public Result(int icon, String name, Number price, Object object) {
        this.icon = icon;
        this.name = name;
        this.price = price;
        this.object = object;
    }

    public Result(int icon, String name, Object object) {
        this(icon, name, Double.valueOf(-1f), object);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView nameResult;
        public final TextView priceResult;
        public final ImageView iconResult;

        public Holder(View view) {
            super(view);

            this.view = view;
            iconResult = (ImageView) view.findViewById(R.id.icon_result);
            nameResult = (TextView) view.findViewById(R.id.name_result);
            priceResult = (TextView) view.findViewById(R.id.price_result);
        }

    }
}