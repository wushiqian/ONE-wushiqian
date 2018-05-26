package com.wushiqian.util;

import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.bean.Author;
import com.wushiqian.bean.Comment;
import com.wushiqian.bean.Film;
import com.wushiqian.bean.Music;
import com.wushiqian.bean.Picture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
* JSON解析
* @author wushiqian
* created at 2018/5/25 20:23
*/
public class JSONUtil {

    public static Picture praseJSONPictureDetail(JSONObject jsonObject){
        Picture picture = new Picture();
        try{
            String message = jsonObject.getString("hp_author");
            String imageUrl = jsonObject.getString("hp_img_url");
            String content = jsonObject.getString("hp_content");
            String text = jsonObject.getString("text_authors");
            picture.setImageUrl(imageUrl);
            picture.setMessage(message);
            picture.setText(text);
            picture.setContent(content);
            return picture;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  null;
    }

    public static List<Comment> parseJSONComment(JSONObject jsonComment){
        List<Comment> commentList = new ArrayList<>();
        try {
            String data = jsonComment.getString("data");
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String user = jsonObject.getString("user");
                JSONObject jo = new JSONObject(user);
                String imageUrl = jo.getString("web_url");
                String userName = jo.getString("user_name");
                String time = jsonObject.getString("input_date");
                String scomment = jsonObject.getString("content");
                int praiseNum = jsonObject.getInt("praisenum");
                Comment comment = new Comment(imageUrl, userName, time, scomment, praiseNum);
                commentList.add(comment);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return commentList;
    }

    public static Author parseJSONAuthor(JSONObject jsonObj,String type){
        try {
            String author = "";
            String authorImaUrl = "";
            String authorDesc = "";
            Author mAuthor = new Author();
            if(type.equals("music")){
                String data = jsonObj.getString("story_author");
                JSONObject jsonObject = new JSONObject(data);
                author = jsonObject.getString("user_name");
                authorImaUrl = jsonObject.getString("web_url");
                authorDesc = jsonObject.getString("desc");
            }else if(type.equals("movie")) {
                String data = jsonObj.getString("data");
                JSONArray jsonArray = new JSONArray(data);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String authorData = jsonObject.getString("user");
                JSONObject jsonObject1 = new JSONObject(authorData);
                author = jsonObject1.getString("user_name");
                authorImaUrl = jsonObject1.getString("web_url");
                authorDesc = jsonObject1.getString("desc");
            }else {
                String data = jsonObj.getString("author");
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    author = jsonObject.getString("user_name");
                    authorImaUrl = jsonObject.getString("web_url");
                    authorDesc = jsonObject.getString("desc");
                }
            }
            mAuthor.setAuthor(author);
            mAuthor.setAuthorDesc(authorDesc);
            mAuthor.setAuthorImaUrl(authorImaUrl);
            return mAuthor;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArticleListItem parseJSONArticleDetail(JSONObject jsonObject){
        try{
            ArticleListItem article = new ArticleListItem();
            String title = jsonObject.optString("hp_title");
            String content = jsonObject.optString("hp_content");
            String author = jsonObject.optString("hp_author");
            String titleInfo = "文/" + author;
            String introauthor = jsonObject.optString("hp_author_introduce");
            String copyright = jsonObject.optString("copyright");
            article.setTitle(title);
            article.setContent(content);
            article.setTitleInfo(titleInfo);
            article.setIntroauthor(introauthor);
            article.setCopyright(copyright);
            return article;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Film parseJSONMovieDetail(JSONObject jsonObject){
        try{
            Film film = new Film();
            String data = jsonObject.getString("data");
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            String user = jsonObj.getString("user");
            JSONObject jsonObject1 = new JSONObject(user);
            String title = jsonObj.getString("title");
            String content = jsonObj.getString("content");
            String userName = jsonObject1.getString("user_name");
            String titleInfo = "文/" + userName;
            String introauthor = jsonObj.getString("charge_edt");
            String copyright = jsonObj.getString("copyright");
            film.setTitle(title);
            film.setContent(content);
            film.setTitleInfo(titleInfo);
            film.setIntroauthor(introauthor);
            film.setCopyright(copyright);
            return film;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Music parseJSONMusicDetail(JSONObject jsonObject){
        try{
            Music music = new Music();
            String title = jsonObject.getString("story_title");
            String content = jsonObject.getString("story");
            String coverUrl = jsonObject.getString("cover");
            String info = jsonObject.getString("info");
            String musicTitle = jsonObject.getString("title");
            String titleInfo = musicTitle + "\n" + info;
            String introauthor = jsonObject.getString("charge_edt");
            String copyright = jsonObject.getString("copyright");
            music.setTitle(title);
            music.setContent(content);
            music.setCoverUrl(coverUrl);
            music.setTitleInfo(titleInfo);
            music.setIntroauthor(introauthor);
            music.setCopyright(copyright);
            return music;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Music parseJSONmusic(JSONObject jsonObject){
        try {
            String title = jsonObject.getString("title");
            String musicName = jsonObject.getString("music_name");
            String musicer = jsonObject.getString("audio_author");
            String forward = "" + musicName + "     歌手/" + musicer;
            String imageUrl = jsonObject.getString("img_url");
            int itemId = jsonObject.getInt("item_id");
            Music music = new Music(title, forward, itemId, imageUrl);
            return music;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
        }

    public static Film parseJSONFilm(JSONObject jsonObject){
        try{
            String title = jsonObject.getString("title");
            String forward = "《" + jsonObject.getString("subtitle") + "》";
            String imageUrl = jsonObject.getString("img_url");
            int itemId = jsonObject.getInt("item_id");
            Film film = new Film(title,forward,itemId,imageUrl);
            return film;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArticleListItem parseJSONArticle(JSONObject jsonObject){
        try {
            String title = jsonObject.getString("title");
            String author = jsonObject.getString("author");
            JSONObject jo = new JSONObject(author);
            String userName = jo.getString("user_name");
            String imageUrl = jsonObject.getString("img_url");
            int itemId = jsonObject.getInt("item_id");
            ArticleListItem articleListItem = new ArticleListItem(title,userName,imageUrl,itemId);
            return articleListItem;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Picture praseJSONMainPicture(JSONObject jsonObject) {
        Picture picture = new Picture();
        try{
            String author = jsonObject.getString("hp_author");
            String imageUrl = jsonObject.getString("hp_img_url");
            String imageAuthor = jsonObject.getString("image_authors");
            String message = "" + author + "|" + imageAuthor;
            String  content = jsonObject.getString("hp_content");
            String text = jsonObject.getString("text_authors");
            picture.setImageUrl(imageUrl);
            picture.setMessage(message);
            picture.setText(text);
            picture.setContent(content);
            return picture;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  null;
    }

    public static ArticleListItem parseJSONMainArticle(JSONObject jsonObj){
        ArticleListItem articleListItem = new ArticleListItem();
        try {
            String articleTitle = jsonObj.getString("hp_title");
            String articleForward = jsonObj.getString("guide_word");
            String jo = jsonObj.getString("author");
            JSONArray jArray = new JSONArray(jo);
            JSONObject jObject = jArray.getJSONObject(0);
            String articleAuthor = jObject.getString("user_name");
            articleListItem.setAuthor(articleAuthor);
            articleListItem.setTitle(articleTitle);
            articleListItem.setForward(articleForward);
            return articleListItem;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  null;
    }
}
