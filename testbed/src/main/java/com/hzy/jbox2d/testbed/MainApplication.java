package com.hzy.jbox2d.testbed;

import android.app.Application;

import com.blankj.utilcode.util.Utils;


/**
 * Created by huzongyao on 2018/5/15.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
