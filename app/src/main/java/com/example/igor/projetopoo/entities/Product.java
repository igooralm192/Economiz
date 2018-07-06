package com.example.igor.projetopoo.entities;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class Product {
    private String name;
    private Double averagePrice;
    private String parentCategory;
    private String[] feedbacks;
    private Pair<Double, Double> priceRange;

    public Product(String name, Double averagePrice, String parentCategory, String[] feedbacks, Pair<Double, Double> priceRange) {
        this.name = name;
        this.averagePrice = averagePrice;
        this.parentCategory = parentCategory;
        this.feedbacks = feedbacks;
        this.priceRange = priceRange;
    }

    public Product(Map<String, Object> map) {
        this((String) map.get("name"), (Double) map.get("average_price"), (String) map.get("parent_category"), null, null);

        List<String> list = (List<String>) map.get("feedbacks");
        String[] feedbacks = new String[]{};
        this.feedbacks = list.toArray(feedbacks);

        Map<String, Object> range = (Map<String, Object>) map.get("price_range");
        this.priceRange = new Pair<>((Double) map.get("minimum_price"), (Double) map.get("maximum_price"));
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Double getAveragePrice() { return averagePrice; }

    public void setAveragePrice(Double averagePrice) { this.averagePrice = averagePrice; }

    public String getParentCategory() { return parentCategory; }

    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public String[] getFeedbacks() { return feedbacks; }

    public void setFeedbacks(String[] feedbacks) { this.feedbacks = feedbacks; }

    public Pair<Double, Double> getPriceRange() { return priceRange; }

    public void setPriceRange(Pair<Double, Double> priceRange) { this.priceRange = priceRange; }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        try {
            object.put("name", this.getName());
            object.put("parent_category", this.getParentCategory());
            object.put("average_price", this.getAveragePrice());
            object.put("feedbacks", this.getFeedbacks());
            object.put("price_range", this.getPriceRange());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView averagePrice;
        public Product product;

        public Holder(View view, final ListGenericAdapter.OnItemViewClickListener listener) {
            super(view);

            name = view.findViewById(R.id.name_product);
            averagePrice = view.findViewById(R.id.price_product);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onProductClick( Holder.this.getProduct() );
                }
            });
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }
}