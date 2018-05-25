package com.wushiqian.bean;

/**
* 音乐bean
* @author wushiqian
* created at 2018/5/25 20:19
*/
public class Music {

    private String title;

    private String forward;

    private int itemId;

    private String imageUrl;

    private String coverUrl;

    private String titleInfo;

    private  String content;

    private String introauthor;

    private String copyright;

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

    public Music(){
        super();
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(String titleInfo) {
        this.titleInfo = titleInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
