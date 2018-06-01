package com.wushiqian.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.wushiqian.one_wushiqian.R;

/**
* 关于界面
* @author wushiqian
* created at 2018/5/25 20:16
*/
public class AboutActivity extends BaseActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        super.setToolbar();
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("关于");
        //设置成actionbar
        setSupportActionBar(toolbar);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //返回事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
