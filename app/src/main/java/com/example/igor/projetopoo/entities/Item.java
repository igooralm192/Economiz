package com.example.igor.projetopoo.entities;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Item implements Serializable {
    private int idIcon;
    private String name;
    private String type;
    private Entity entity;
    private String price;

    public Item(int idIcon, String name, String type, Entity entity){
        this.idIcon = idIcon;
        this.name = name;
        this.type = type;
        this.entity = entity;
        this.price = "";
    }

    public Item(int idIcon, String name, String type, Entity entity, String price){
        this(idIcon, name, type, entity);
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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item item = (Item) obj;

            return this.getName().equals(item.getName()) && this.getType().equals(item.getType());
        } else return super.equals(obj);

    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("idIcon", this.getIdIcon());
            object.put("name", this.getName());
            object.put("type", this.getType());
            if (this.getEntity() instanceof Category) {
                Category category = (Category) this.getEntity();
                object.put("object", category.toJSON());
            } else if (this.getEntity() instanceof Product) {
                Product product = (Product) this.getEntity();
                object.put("object", product.toJSON());
            } else object.put("object", null);

            object.put("price", this.getPrice());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

}

