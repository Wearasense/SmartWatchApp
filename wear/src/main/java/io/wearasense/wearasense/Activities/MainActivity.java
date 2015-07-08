package io.wearasense.wearasense.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import io.wearasense.wearasense.R;
import io.wearasense.wearasense.Receivers.TimeBroadCastReceiver;
import io.wearasense.wearasense.Utils.VibrationManager;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, SensorEventListener {

    private static final String TAG = MainActivity.class.getName();
    private static final long INTERVAL_MINUTE = 60 * 1000;
    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;
    private static final String LATITUDE = "POI_LATITUDE";
    private static final String LONGITUDE = "POI_LONGITUDE";
    private static final String INTERVAL = "TIME_INTERVAL";
    private static final String NORTH = "NORTH_SELECTION";
    private static final String POI_PATH = "/POI_PATH";
    private static final String TIME_PATH = "/TIME_PATH";
    private static final String NORTH_PATH = "/NORTH_PATH";
    private int counterNorth = 0;
    private AlarmManager alarmMgr;
    private Intent alarmIntent;
    private PendingIntent alarmPendingIntent;
    private Vibrator vibration;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float azimut;
    private double azimutDegrees;

    public enum TYPE_OF_MESSAGE {POI, TIME, NORTH}
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        //        WindowManager.LayoutParams WMLP = getWindow().getAttributes();
        //        WMLP.screenBrightness = 0F;

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(MainActivity.this, TimeBroadCastReceiver.class);
        alarmPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        vibration = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case 0:
                        mTextView.setText("Set to POI");
                        alarmMgr.cancel(alarmPendingIntent);
                        break;
                    case 1:
                        mTextView.setText("Time");
                        break;
                    case 2:
                        mTextView.setText("NORTH");
                        break;
                }
            }
        };

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApiIfAvailable(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "DATA API CONNECTED");
        Wearable.DataApi.addListener(mGoogleApiClient, this);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(NORTH_PATH);
        putDataMapReq.getDataMap().putInt(NORTH, counterNorth++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended " + i);
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "DATA CHANGED");
        Message msg = new Message();
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                final DataItem item = event.getDataItem();
                switch(item.getUri().getPath()){
                    case POI_PATH:
                        msg.what = TYPE_OF_MESSAGE.POI.ordinal();
                        mHandler.sendMessage(msg);
                        break;
                    case TIME_PATH:
                        mSensorManager.unregisterListener(MainActivity.this, accelerometer);
                        mSensorManager.unregisterListener(MainActivity.this, magnetometer);
                        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                INTERVAL_MINUTE,
                                INTERVAL_MINUTE, alarmPendingIntent);

                        msg.what = TYPE_OF_MESSAGE.TIME.ordinal();
                        mHandler.sendMessage(msg);
                        break;
                    case NORTH_PATH:
                        mSensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                        mSensorManager.registerListener(MainActivity.this, magnetometer, SensorManager.SENSOR_DELAY_UI);

                        alarmMgr.cancel(alarmPendingIntent);
                        msg.what = TYPE_OF_MESSAGE.NORTH.ordinal();
                        mHandler.sendMessage(msg);
                        break;
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
//                Log.d(TAG, "orientation " + Math.toDegrees(azimut));
                azimutDegrees = Math.toDegrees(azimut);
                if(azimutDegrees > -93 && azimutDegrees < -87){
                    vibration.vibrate(100);
                }
            } else {
                Log.d(TAG, "unsuccessfull getting rotation matrix");
            }
        } else {
            Log.d(TAG, "gravity " + mGravity + "and geomagnetic " + mGeomagnetic);
        }
    }
}
