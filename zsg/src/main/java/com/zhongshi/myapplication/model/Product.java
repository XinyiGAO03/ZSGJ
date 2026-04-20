package com.zhongshi.myapplication.model;

public class Product {
    private Long id;
    private String title;
    private String price;
    private String conditionTag;
    private String story;
    private String imageUrl;
    private String sellerNickname;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getConditionTag() { return conditionTag; }
    public String getStory() { return story; }
    public String getImageUrl() { return imageUrl; }
    public String getSellerNickname() { return sellerNickname; }
}
