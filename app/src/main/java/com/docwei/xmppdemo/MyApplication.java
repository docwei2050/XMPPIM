package com.docwei.xmppdemo;

import android.app.Application;

/**
 * Created by tobo on 17/3/7.
 */

public class MyApplication extends Application {
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
    public static MyApplication getInstance(){
        return instance;
    }
}
