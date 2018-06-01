package com.wushiqian.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.wushiqian.one_wushiqian.R;

public class StartActivity extends AppCompatActivity {
//    private Handler handler;
//    private MyThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        handler = new Handler();
//        thread = new MyThread();
//        handler.postDelayed(thread, 3000);
    }
//
//    private class MyThread implements Runnable {
//
//        @Override
//        public void run() {
//            startActivity(new Intent(StartActivity.this, MainActivity.class));
//            finish();
//        }
//
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        handler.removeCallbacks(thread);//移除回调
//    }
}