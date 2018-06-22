package com.example.igor.projetopoo.activity.search;


public class Item {
    private String productName;
    private int idIcon;

    public Item(String name, int idIcon){
        this.productName = name;
        this.idIcon = idIcon;
    }

    public int getIdIcon() {
        return idIcon;
    }

    public void setIdIcon(int idIcon) {
        this.idIcon = idIcon;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}

