package com.wushiqian.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
* 网络申请
* @author wushiqian
* created at 2018/5/25 20:21
*/
public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
//                    connection.setDoInput(false);
//                    connection.setDoOutput(false);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if(listener != null){
                        //回调onFinish()方法
                        JSONObject jsonObject = new JSONObject(response.toString());
//                        int res = jsonObject.getInt("res");
//                        if(res != 0) return;
                        String data = jsonObject.getString("data");
                        listener.onFinish(data);
                    }
                } catch (Exception e){
                    if(listener != null){
                        //回调onError方法
                        listener.onError(e);
                    }
                } finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
