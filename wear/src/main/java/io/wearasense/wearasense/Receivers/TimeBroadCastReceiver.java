package io.wearasense.wearasense.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by goofyahead on 6/07/15.
 */
public class TimeBroadCastReceiver extends BroadcastReceiver{
    private static final int INTERVAL = 250;
    private static final long[] PATTERN_MINIMAL = {INTERVAL, INTERVAL, INTERVAL, INTERVAL};
    private static final String TAG = TimeBroadCastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(PATTERN_MINIMAL, -1);
        Log.d(TAG, "MINUTE HAS GONE OF YOUR LIFE");
    }
}
