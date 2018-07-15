package com.example.igor.projetopoo.entities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

import java.lang.Double;
import java.util.Date;
import java.util.Map;

public class Feedback {
    private String location;
    private String date;
    private Number price;

    public Feedback(String location, String date, Number price) {
        this.location = location;
        this.date = date;
        this.price = price;
    }

    public Feedback(Map<String, Object> map) {
        this((String) map.get("location"), (String) map.get("date"), (Number) map.get("price"));
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public Number getPrice() {
        return price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) { this.date = date; }

    public void setPrice(Double price) {
        this.price = price;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView location;
        public TextView price;
        public TextView day;

        public Holder(View view) {
            super(view);

            location = view.findViewById(R.id.feedback_location);
            price = view.findViewById(R.id.feedback_price);
            day = view.findViewById(R.id.feedback_date);
        }
    }

}
