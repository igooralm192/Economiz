package com.example.igor.projetopoo.activity.search;


public class Product {
    private String productName;
    private double price;

    public Product(String name, double price){
        this.productName = name;
        this.price = price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}

