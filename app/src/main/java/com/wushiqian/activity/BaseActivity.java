package com.wushiqian.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.wushiqian.one_wushiqian.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    private static List<Activity> activities = new ArrayList<>();
    protected Toolbar mToolbar;    //定制toolbar
    public Context context;
    public Activity activity;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        activities.add(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
    }

    protected void setToolbar(){
        mToolbar = findViewById(R.id.toolBar);
        mToolbar.setTitle("");   //将原本的标题栏清空，而用一个新的TextView代替
        setSupportActionBar(mToolbar);
    }

    public void finishActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    /**
     * 跳转到指定的Activity
     *
     * @param clz 指定的Activity对应的class
     */
    public void goTo(Class<?> clz) {
        if (clz.equals(MainActivity.class)) {
            finishActivity();
        } else {
            for (int i = activities.size() - 1; i >= 0; i--) {
                if (clz.equals(activities.get(i).getClass())) {
                    break;
                } else {
                    activities.get(i).finish();
                }
            }
        }
    }

}
