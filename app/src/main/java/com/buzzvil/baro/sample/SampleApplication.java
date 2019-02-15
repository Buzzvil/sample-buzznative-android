package com.buzzvil.baro.sample;

import android.app.Application;
import android.support.multidex.MultiDex;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
