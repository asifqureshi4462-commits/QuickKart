package com.quickkart.app.models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public int id;
    public int userId;
    public String userName;
    public double totalAmount;
    public String status; // Placed, Dispatched, Delivered, Cancelled
    public String address;
    public String phone;
    public long createdAt;
    public List<OrderItem> items = new ArrayList<>();
}
