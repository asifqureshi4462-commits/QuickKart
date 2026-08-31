package com.quickkart.app.models;

public class Product {
    public int id;
    public int categoryId;
    public String categoryName;
    public String name;
    public String description;
    public double price;
    public int stock;
    public String imageKey; // maps to drawable name, e.g. "product_1"
    public long createdAt;

    public Product() {}

    public Product(int id, int categoryId, String name, String description,
                    double price, int stock, String imageKey) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageKey = imageKey;
    }
}
