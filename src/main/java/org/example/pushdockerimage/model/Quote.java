package org.example.pushdockerimage.model;

public class Quote {
    private int id;
    private String author;
    private String quote;

    public Quote(int id, String author, String quote) {
        this.id = id;
        this.author = author;
        this.quote = quote;
    }

    public int getId() { return id; }
    public String getAuthor() { return author; }
    public String getQuote() { return quote; }
}