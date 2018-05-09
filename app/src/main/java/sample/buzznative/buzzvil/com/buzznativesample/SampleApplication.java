package sample.buzznative.buzzvil.com.buzznativesample;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.buzzvil.buzzad.BuzzSDK;

/**
 * Created by jim on 2018. 4. 24..
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
