package com.example.dominique.barcode;

public class Barcodes {

    private String code;
    private String description;
    private String price;
    private String location;

    public Barcodes(String code, String description, String price, String location) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPrice(){
        return this.price;
    }

    public String getLocation() {
        return this.location;
    }
}
