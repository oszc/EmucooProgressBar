package com.emucoo.emucooprogressbar;

import android.app.Application;

/**
 * 2018/8/22  4:07 PM
 * Created by Zhang.
 */
public class App extends Application {

    private static Application mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Application getInstance(){
        return mInstance;
    }
}
