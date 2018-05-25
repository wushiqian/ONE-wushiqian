package com.wushiqian.util;

/**
* 网络申请回调接口
* @author wushiqian
* created at 2018/5/25 20:22
*/
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}
