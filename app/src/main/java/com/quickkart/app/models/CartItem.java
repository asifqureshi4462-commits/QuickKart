package com.quickkart.app.models;

public class CartItem {
    public int cartId;
    public int productId;
    public String name;
    public String imageKey;
    public double price;
    public int quantity;
    public int stock;

    public double getSubtotal() {
        return price * quantity;
    }
}
