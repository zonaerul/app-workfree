package com.chaerulmobdev.data.adapter.data;

public class WorkData {
    String name;
    String price;
    String image;
    String location;
    public WorkData(String name, String price, String image, String location){
        this.name = name;
        this.price = price;
        this.image = image;
        this.location=location;

    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }
}
