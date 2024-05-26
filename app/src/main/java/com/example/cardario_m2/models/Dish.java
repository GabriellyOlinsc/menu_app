package com.example.cardario_m2.models;

public class Dish {
    private long id;
    private String name;
    private double price;
    private String image;

    public Dish() {
    }
    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public Dish(String name, double price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
