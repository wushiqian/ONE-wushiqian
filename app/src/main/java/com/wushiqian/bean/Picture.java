package com.wushiqian.bean;

public class Picture {

    private String imageUrl;

    private int itemId;

    private String message;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Picture(String imageUrl,int itemId,String message){
        this.imageUrl = imageUrl;
        this.itemId = itemId;
        this.message = message;
    }
}
