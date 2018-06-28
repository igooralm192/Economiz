package com.example.igor.projetopoo.entities;


import java.io.Serializable;

public class Item implements Serializable {
    private int idIcon;
    private String name;

    public Item(int idIcon, String name){
        this.idIcon = idIcon;
        this.name = name;
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
}

