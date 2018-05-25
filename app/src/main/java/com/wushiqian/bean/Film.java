package com.wushiqian.bean;

/**
* 电影bean
* @author wushiqian
* created at 2018/5/25 20:19
*/
public class Film {

    private String title;

    private String forward;

    private int itemId;

    private String imageUrl;

    private String content;

    private String copyright;

    private String introauthor;

    private String titleInfo;

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

    public Film(String title,String forward,int itemId,String imageUrl){
        this.forward = forward;
        this.imageUrl = imageUrl;
        this.title = title;
        this.itemId = itemId;
    }

    public String getContent() {
        return content;
    }

    public Film(){
        super();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getIntroauthor() {
        return introauthor;
    }

    public void setIntroauthor(String introauthor) {
        this.introauthor = introauthor;
    }

    public String getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(String titleInfo) {
        this.titleInfo = titleInfo;
    }
}
