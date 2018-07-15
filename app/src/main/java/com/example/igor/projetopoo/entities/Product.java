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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Product implements Serializable {
    private String name;
    private String parentCategory;
    private Number averagePrice;
    private List<String> feedbacks;
    private Pair<Number, Number> priceRange;

    public Product(String name, String parentCategory, Number averagePrice, List<String> feedbacks, Pair<Number, Number> priceRange) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.averagePrice = averagePrice;
        this.feedbacks = feedbacks;
        this.priceRange = priceRange;
    }

    public Product(Map<String, Object> map) {
        this((String) map.get("name"), (String) map.get("parent_category"), (Double) map.get("average_price"), null, null);
        this.feedbacks = (List<String>) map.get("feedbacks");

        Map<String, Object> range = (Map<String, Object>) map.get("price_range");

        Number min = (Number) range.get("minimum_price");
        Number max = (Number) range.get("maximum_price");

        this.priceRange = new Pair<>(min, max);
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getParentCategory() { return parentCategory; }

    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public Number getAveragePrice() { return averagePrice; }

    public void setAveragePrice(Number averagePrice) { this.averagePrice = averagePrice; }

    public List<String> getFeedbacks() { return feedbacks; }

    public void setFeedbacks(List<String> feedbacks) { this.feedbacks = feedbacks; }

    public Pair<Number, Number> getPriceRange() { return priceRange; }

    public void setPriceRange(Pair<Number, Number> priceRange) { this.priceRange = priceRange; }

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
            range.put("minimum_range", this.getPriceRange().first.doubleValue());
            range.put("maximum_range", this.getPriceRange().second.doubleValue());
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
            Pair<Number, Number> priceRange = new Pair<>( (Number) range.getDouble("minimum_range"), (Number) range.getDouble("maximum_range") );

            return new Product(
                    (String) object.get("name"),
                    (String) object.get("parent_category"),
                    (Number) object.get("average_price"),
                    feedbacks,
                    priceRange
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}