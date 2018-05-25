package com.wushiqian.bean;

/**
* 文章bean
* @author wushiqian
* created at 2018/5/25 20:18
*/
public class ArticleListItem {

    private String title;

    private String author;

    private String imageUrl;

    private int itemId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

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

    public ArticleListItem(String title,String author,String imageUrl,int itemId){
        this.imageUrl = imageUrl;
        this.title = title;
        this.author = author;
        this.itemId = itemId;
    }

    public ArticleListItem(){

    }
}
