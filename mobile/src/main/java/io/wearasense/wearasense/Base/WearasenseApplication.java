package io.wearasense.wearasense.Base;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import io.wearasense.wearasense.Dagger.AndroidModule;
import io.wearasense.wearasense.Dagger.CustomModule;

/**
 * Created by goofyahead on 7/07/15.
 */
public class WearasenseApplication extends Application {

    private ObjectGraph graph;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                new AndroidModule(this),
                new CustomModule()// you can add more modules here
        );
    }

    public void inject(Object object) {
        graph.inject(object);
    }

    public static Context getAppContext() {
        return mContext;
    }
}
