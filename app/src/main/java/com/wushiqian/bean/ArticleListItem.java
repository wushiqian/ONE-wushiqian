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

    private String titleInfo;

    private  String content;

    private String introauthor;

    private String copyright;

    private String forward;

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
        super();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(String titleInfo) {
        this.titleInfo = titleInfo;
    }

    public String getIntroauthor() {
        return introauthor;
    }

    public void setIntroauthor(String introauthor) {
        this.introauthor = introauthor;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }
}
