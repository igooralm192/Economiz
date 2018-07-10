package com.example.igor.projetopoo.entities;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Product {
    private String name;
    private String parentCategory;
    private Double averagePrice;
    private List<String> feedbacks;
    private Pair<Double, Double> priceRange;

    public Product(String name, String parentCategory, Double averagePrice, List<String> feedbacks, Pair<Double, Double> priceRange) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.averagePrice = averagePrice;
        this.feedbacks = feedbacks;
        this.priceRange = priceRange;
    }

    public Product(Map<String, Object> map) {
        this((String) map.get("name"), (String) map.get("parent_category"), Double.parseDouble((String) map.get("average_price")), null, null);

        this.feedbacks = (List<String>) map.get("feedbacks");

        Map<String, Object> range = (Map<String, Object>) map.get("price_range");

        Double min = Double.parseDouble( (String) range.get("minimum_price") );
        Double max = Double.parseDouble( (String) range.get("maximum_price") );

        this.priceRange = new Pair<>(min, max);
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getParentCategory() { return parentCategory; }

    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public Double getAveragePrice() { return averagePrice; }

    public void setAveragePrice(Double averagePrice) { this.averagePrice = averagePrice; }

    public List<String> getFeedbacks() { return feedbacks; }

    public void setFeedbacks(List<String> feedbacks) { this.feedbacks = feedbacks; }

    public Pair<Double, Double> getPriceRange() { return priceRange; }

    public void setPriceRange(Pair<Double, Double> priceRange) { this.priceRange = priceRange; }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        try {
            object.put("name", this.getName());
            object.put("parent_category", this.getParentCategory());
            object.put("average_price", this.getAveragePrice());

            JSONArray array = new JSONArray();
            for (String feedback: feedbacks) array.put(feedback);
            object.put("feedbacks", array);

            JSONObject range = new JSONObject();
            range.put("minimum_range", this.getPriceRange().first);
            range.put("maximum_range", this.getPriceRange().second);
            object.put("price_range", range);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static Product toObject(JSONObject object) {
        try {
            JSONArray array = object.getJSONArray("feedbacks");
            List<String> feedbacks = new ArrayList<>();

            for (int i=0; i<array.length(); i++)
                feedbacks.add((String) array.get(i));

            JSONObject range = object.getJSONObject("price_range");
            Pair<Double, Double> priceRange = new Pair<>( range.getDouble("minimum_range"), range.getDouble("maximum_range") );

            return new Product(
                    (String) object.get("name"),
                    (String) object.get("parent_category"),
                    (Double) object.get("average_price"),
                    feedbacks,
                    priceRange
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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