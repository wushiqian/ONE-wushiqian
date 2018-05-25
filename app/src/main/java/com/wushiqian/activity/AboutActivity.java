package com.wushiqian.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.wushiqian.one_wushiqian.R;

/**
* 关于界面
* @author wushiqian
* created at 2018/5/25 20:16
*/
public class AboutActivity extends AppCompatActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = findViewById(R.id.toolBar);
        //设置成actionbar
        setSupportActionBar(toolbar);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.back2);
        //返回事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
