package com.example.environmentalstudies.User.Model;

public class bookBuyModel {
    String uri;
    String bookName;
    String autherName;
    float price;
    String weburi;


    public bookBuyModel(String uri, String bookName, String autherName, float price, String weburi) {
        this.uri = uri;
        this.bookName = bookName;
        this.autherName = autherName;
        this.price = price;
        this.weburi = weburi;
    }

    public bookBuyModel() {
    }

    public String getWeburi() {
        return weburi;
    }

    public void setWeburi(String weburi) {
        this.weburi = weburi;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAutherName() {
        return autherName;
    }

    public void setAutherName(String autherName) {
        this.autherName = autherName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
