package com.example.igor.projetopoo.entities;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

import java.util.Map;

public class Product {
    private String name;
    private Float averagePrice;
    private String parentCategory;
    private String[] feedbacks;
    private Pair<Double, Double> priceRange;

    public Product(String name, Float averagePrice, String parentCategory, String[] feedbacks, @Nullable Pair<Double, Double> priceRange) {
        this.name = name;
        this.averagePrice = averagePrice;
        this.parentCategory = parentCategory;
        this.feedbacks = feedbacks;
        this.priceRange = priceRange;
    }

    public Product(Map<String, Object> map) {
        this((String) map.get("name"), (Float) map.get("average_price"), (String) map.get("parent_category"), (String[]) map.get("feedbacks"), null);
        Map<String, Object> range = (Map<String, Object>) map.get("price_range");
        this.priceRange = new Pair<>((Double) map.get("minimum_price"), (Double) map.get("maximum_price"));
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Float getAveragePrice() { return averagePrice; }

    public void setAveragePrice(Float averagePrice) { this.averagePrice = averagePrice; }

    public String getParentCategory() { return parentCategory; }

    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public String[] getFeedbacks() { return feedbacks; }

    public void setFeedbacks(String[] feedbacks) { this.feedbacks = feedbacks; }

    public Pair<Double, Double> getPriceRange() { return priceRange; }

    public void setPriceRange(Pair<Double, Double> priceRange) { this.priceRange = priceRange; }

    public static class Holder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView averagePrice;

        public Holder(View view) {
            super(view);

            name = view.findViewById(R.id.name_product);
            averagePrice = view.findViewById(R.id.price_product);
        }
    }
}