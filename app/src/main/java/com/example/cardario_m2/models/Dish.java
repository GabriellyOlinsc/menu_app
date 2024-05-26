package com.example.cardario_m2.models;

public class Dish {
    private String name;
    private double price;
    // private String image;

    public Dish(String name, double price/*, String image*/) {
        this.name = name;
        this.price = price;
        //this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
