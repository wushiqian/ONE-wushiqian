package com.wushiqian.bean;

/**
* 插画bean
* @author wushiqian
* created at 2018/5/25 20:20
*/
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

    public Picture(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public Picture(int itemId){
        this.itemId = itemId;
    }

    public Picture(int itemId,String imageUrl){
        this.itemId = itemId;
        this.imageUrl = imageUrl;
    }

}
