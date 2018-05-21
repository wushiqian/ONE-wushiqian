package com.wushiqian.bean;

public class Comment {

    private String imageUrl;

    private String userName;

    private String commentTime;

    private String comment;

    private int praisenum;


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPraisenum() {
        return praisenum;
    }

    public void setPraisenum(int praisenum) {
        this.praisenum = praisenum;
    }

    public Comment(String imageUrl,String userName,String commentTime,String comment,int praisenum){
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.commentTime = commentTime;
        this.comment = comment;
        this.praisenum = praisenum;
    }
}
