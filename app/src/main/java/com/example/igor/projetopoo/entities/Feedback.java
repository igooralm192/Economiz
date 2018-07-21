package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

import java.lang.Double;
import java.util.Map;

public class Feedback {
    private String product;
    private String location;
    private long date;
    private Number price;

    public Feedback(String product, String location, long date, Number price) {
        this.product = product;
        this.location = location;
        this.date = date;
        this.price = price;
    }

    public Feedback(Map<String, Object> map) {
        this((String) map.get("product"), (String) map.get("location"), (long) map.get("date"), (Number) map.get("price"));
    }

    public String getProduct() { return product; }

    public String getLocation() {
        return location;
    }

    public long getDate() {
        return date;
    }

    public Number getPrice() {
        return price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(long date) { this.date = date; }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setProduct(String product) { this.product = product; }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView location;
        public TextView price;
        public TextView date;

        public Holder(View view) {
            super(view);

            location = view.findViewById(R.id.feedback_location);
            price = view.findViewById(R.id.feedback_price);
            date = view.findViewById(R.id.feedback_date);
        }
    }

}
