package com.quickkart.app.models;

public class Category {
    public int id;
    public String name;
    public String imageKey; // maps to drawable name, e.g. "cat_mobiles"

    public Category(int id, String name, String imageKey) {
        this.id = id;
        this.name = name;
        this.imageKey = imageKey;
    }
}
