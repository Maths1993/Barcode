package com.example.dominique.barcode;

public class Barcodes {

    private String code;
    private String description;
    private String price;
    private String date;

    public Barcodes(String code) {
        this.code = code;
    }

    public Barcodes(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public Barcodes(String code, String description, String price) {
        this.code = code;
        this.description = description;
        this.price = price;
    }

    public Barcodes(String code, String description, String price, String date) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.date = date;
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

    public String getDate() {
        return this.date;
    }
}
