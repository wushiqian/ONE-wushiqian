package com.wushiqian.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static String parseJSON(String data){
//        String title="";
        String message="";
        try {
            //第一步：将从网络字符串jsonData字符串装入JSONObject
            JSONObject jsonObject = new JSONObject(data);
            //第二步：因为单条数据，所以用jsonObject.getString方法直接取出对应键值
//            title = jsonObject.optString("hp_title");
            message = jsonObject.optString("hp_content");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static List<Integer> parseJsonArray(String data){
        List<Integer> idList = new ArrayList<Integer>();
        try{
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsObj =  jsonArray.getJSONObject(i);
                int id = jsObj.getInt("data");
                idList.add(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return idList;
    }

    public static String parseJSONtitle(String data){
        String title = "";
        try {
            //第一步：将从网络字符串jsonData字符串装入JSONObject
            JSONObject jsonObject = new JSONObject(data);
            //第二步：因为单条数据，所以用jsonObject.getString方法直接取出对应键值
            title = jsonObject.optString("hp_title");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }

    public static String parseJSONauthor(String response) {
        String author = "";
        try {
            //第一步：将从网络字符串jsonData字符串装入JSONObject
            JSONObject jsonObject = new JSONObject(response);
            //第二步：因为单条数据，所以用jsonObject.getString方法直接取出对应键值
            author = jsonObject.optString("hp_author");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return author;
    }

    public static String parseJSONIntroAuthor(String response) {
        String introAuthor = "";
        try {
            //第一步：将从网络字符串jsonData字符串装入JSONObject
            JSONObject jsonObject = new JSONObject(response);
            //第二步：因为单条数据，所以用jsonObject.getString方法直接取出对应键值
            introAuthor = jsonObject.optString("hp_author_introduce");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return introAuthor;
    }

    public static String parseJSONcopyright(String response) {
        String copyright = "";
        try {
            //第一步：将从网络字符串jsonData字符串装入JSONObject
            JSONObject jsonObject = new JSONObject(response);
            //第二步：因为单条数据，所以用jsonObject.getString方法直接取出对应键值
            copyright = jsonObject.optString("copyright");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copyright;
    }
}
