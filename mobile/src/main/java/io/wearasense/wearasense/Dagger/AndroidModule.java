package io.wearasense.wearasense.Dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.wearasense.wearasense.Activities.MainActivity;
import io.wearasense.wearasense.Fragments.NorthSense;
import io.wearasense.wearasense.Fragments.PoiSense;
import io.wearasense.wearasense.Fragments.TimeSense;

/**
 * Created by goofyahead on 7/07/15.
 */
@Module(injects = {
        MainActivity.class,
        NorthSense.class,
        PoiSense.class,
        TimeSense.class
        },library = true)
public class AndroidModule {

    private final Context mContext;

    public AndroidModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mContext;
    }

//    @Provides
//    @Singleton
//    NowfieSharedPrefs provideSharedPrefs() {
//        return new NowfieSharedPrefs(mContext);
//    }

}
