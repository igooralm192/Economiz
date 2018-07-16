package com.example.igor.projetopoo.entities;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Item implements Serializable {
    private int idIcon;
    private String name;
    private String type;
    private Object object;
    private String price;

    public Item(int idIcon, String name, String type, Object object){
        this.idIcon = idIcon;
        this.name = name;
        this.type = type;
        this.object = object;
        this.price = "";
    }

    public Item(int idIcon, String name, String type, Object object, String price){
        this(idIcon, name, type, object);
        this.price = price;
    }

    public int getIdIcon() {
        return idIcon;
    }

    public void setIdIcon(int idIcon) {
        this.idIcon = idIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Object getObject() { return object; }

    public void setObject(Object object) { this.object = object; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item item = (Item) obj;

            if (this.getName().equals(item.getName()) && this.getType().equals(item.getType())) return true;
            else return false;
        } else return super.equals(obj);

    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("idIcon", this.getIdIcon());
            object.put("name", this.getName());
            object.put("type", this.getType());
            if (this.getObject() instanceof Category) {
                Category category = (Category) this.getObject();
                object.put("object", category.toJSON());
            } else if (this.getObject() instanceof Product) {
                Product product = (Product) this.getObject();
                object.put("object", product.toJSON());
            } else object.put("object", null);

            object.put("price", this.getPrice());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

}

