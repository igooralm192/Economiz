package com.example.igor.projetopoo.entities;

import android.support.annotation.NonNull;
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

public class Product extends Entitie implements Serializable {
    private Number averagePrice;
    private Pair<Number, Number> priceRange;

    public Product(String name, String parentCategory, Number backgroundCategory, Number averagePrice, Pair<Number, Number> priceRange) {
        super(name, parentCategory, backgroundCategory);
        this.averagePrice = averagePrice;
        this.priceRange = priceRange;
    }

    public Product(Map<String, Object> map) {

        this((String) map.get("name"), (String) map.get("parent_category"), (Number) map.get("background_category"), (Number) map.get("average_price"), null);

        Map<String, Object> range = (Map<String, Object>) map.get("price_range");

        Number min = (Number) range.get("minimum_price");
        Number max = (Number) range.get("maximum_price");

        this.priceRange = new Pair<>(min, max);
        Log.i("TAG", this.priceRange.toString());
    }

    public Number getAveragePrice() { return averagePrice; }

    public void setAveragePrice(Number averagePrice) { this.averagePrice = averagePrice; }

    public Pair<Number, Number> getPriceRange() { return priceRange; }

    public void setPriceRange(Pair<Number, Number> priceRange) { this.priceRange = priceRange; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            Product product = (Product) obj;

            return this.getName().equals(product.getName()) && this.getParentCategory().equals(product.getParentCategory());
        } else return super.equals(obj);
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        try {
            object.put("name", this.getName());
            object.put("parent_category", this.getParentCategory());
            object.put("background_category", this.getBackgroundCategory().doubleValue());
            object.put("average_price", this.getAveragePrice().doubleValue());

            JSONObject range = new JSONObject();
            range.put("minimum_range", this.getPriceRange().first.doubleValue());
            range.put("maximum_range", this.getPriceRange().second.doubleValue());
            object.put("price_range", range);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static Product toObject(String json) {
        try {
            JSONObject object = new JSONObject(json);

            JSONObject range = object.getJSONObject("price_range");
            Pair<Number, Number> priceRange = new Pair<>( (Number) range.getDouble("minimum_range"), (Number) range.getDouble("maximum_range") );

            return new Product(
                    (String) object.get("name"),
                    (String) object.get("parent_category"),
                    (Number) object.get("background_category"),
                    (Number) object.get("average_price"),
                    priceRange
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}