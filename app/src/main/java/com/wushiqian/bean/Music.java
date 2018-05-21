package com.wushiqian.bean;

public class Music {

    private String title;

    private String forward;

    private int itemId;

    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Music(String title,String forward,int itemId,String imageUrl){
        this.forward = forward;
        this.imageUrl = imageUrl;
        this.title = title;
        this.itemId = itemId;
    }
}
