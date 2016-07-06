package com.example.dominique.barcode;

public class BarcodeLayout {

    private String code;
    public String description;
    private String price;
    private String location;

    public BarcodeLayout(String code, String description, String price, String location) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    public String getCode() { return this.code; }

    public String getDescription() {
        return this.description;
    }

    public String getPrice(){ return this.price; }

    public String getLocation() {
        return this.location;
    }
}
