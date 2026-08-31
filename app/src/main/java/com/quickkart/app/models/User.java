package com.quickkart.app.models;

public class User {
    public int id;
    public String name;
    public String phone;
    public String email;
    public String password;
    public String address;

    public User() {}

    public User(int id, String name, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}
