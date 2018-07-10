package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.igor.projetopoo.R;

public class Result {
    private int icon;
    private String name;
    private Double price;

    public Result(int icon, String name, Double price) {
        this.icon = icon;
        this.name = name;
        this.price = price;
    }

    public Result(int icon, String name) {
        this(icon, name, Double.valueOf(-1f));
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public final TextView nameResult;
        public final TextView priceResult;
        public final ImageView iconResult;

        public Holder(View view) {
            super(view);

            iconResult = (ImageView) view.findViewById(R.id.icon_result);
            nameResult = (TextView) view.findViewById(R.id.name_result);
            priceResult = (TextView) view.findViewById(R.id.price_result);
        }

    }
}