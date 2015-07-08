package io.wearasense.wearasense.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.wearasense.wearasense.Adapter.SensesAdapter;
import io.wearasense.wearasense.Base.WearasenseApplication;
import io.wearasense.wearasense.Fragments.NorthSense;
import io.wearasense.wearasense.Fragments.PoiSense;
import io.wearasense.wearasense.Fragments.TimeSense;
import io.wearasense.wearasense.Interfaces.NorthSelect;
import io.wearasense.wearasense.Interfaces.PoiUpdate;
import io.wearasense.wearasense.Interfaces.TimeIntervalUpdate;
import io.wearasense.wearasense.R;
import io.wearasense.wearasense.Models.Category;


public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, TimeIntervalUpdate,
        PoiUpdate, NorthSelect {

    private static final String TAG = MainActivity.class.getName();
    private static final String LATITUDE = "POI_LATITUDE";
    private static final String LONGITUDE = "POI_LONGITUDE";
    private static final String INTERVAL = "TIME_INTERVAL";
    private static final String NORTH = "NORTH_SELECTION";
    private static final String POI_PATH = "/POI_PATH";
    private static final String TIME_PATH = "/TIME_PATH";
    private static final String NORTH_PATH = "/NORTH_PATH";
    private static final String LATITUDE_COUNTER = "LATITUDE_COUNTER";
    private static final String INTERVAL_COUNTER = "INTERVAL_COUNTER";
    private GoogleApiClient mGoogleApiClient;
    private CharSequence mTitle;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.listView_categories)
    ListView drawer;
    @InjectView(R.id.version_app)
    TextView versionApp;
    @InjectView(R.id.left_drawer)
    RelativeLayout drawerHolder;
    private ActionBarDrawerToggle mDrawerToggle;
    private int counterNorth = 0;
    private ArrayList<Category> options = new ArrayList<>();
    private int counterLatitude = 0;
    private int counterInterval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        ((WearasenseApplication) getApplication()).inject(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mTitle);
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApiIfAvailable(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        drawerLayout.setDrawerListener(mDrawerToggle);

        mTitle = getResources().getString(R.string.select_a_sense);
        getActionBar().setTitle(mTitle);

        options.add(new Category(getResources().getString(R.string.time_sense), R.drawable.clock_icon));
        options.add(new Category(getResources().getString(R.string.north_sense), R.drawable.clock_icon));
        options.add(new Category(getResources().getString(R.string.poi_sense), R.drawable.clock_icon));

        SensesAdapter menuAdapter = new SensesAdapter(MainActivity.this, options);

        drawer.setAdapter(menuAdapter);

        drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                switch (position) {
                    case 0:
                        mTitle = getResources().getString(R.string.time_sense);
                        TimeSense timeFragment = TimeSense.getInstance();
                        ft.replace(R.id.fragment_holder, timeFragment);
                        ft.commit();
                        break;
                    case 1:
                        mTitle = getResources().getString(R.string.north_sense);
                        NorthSense northFragment = NorthSense.getInstance();
                        ft.replace(R.id.fragment_holder, northFragment);
                        ft.commit();
                        break;
                    case 2:
                        mTitle = getResources().getString(R.string.poi_sense);
                        PoiSense poiFragment = PoiSense.getInstance();
                        ft.replace(R.id.fragment_holder, poiFragment);
                        ft.commit();
                        break;
                }

                drawer.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerHolder);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "CONNECTED DATA API");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended " + i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "DATA FROM MOBILE");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                switch(item.getUri().getPath()){
                    case POI_PATH:
                        break;
                    case TIME_PATH:
                        break;
                    case NORTH_PATH:
                        break;
                }
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePoi(float latitude, float longitude) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(POI_PATH);
        putDataMapReq.getDataMap().putInt(LATITUDE_COUNTER, counterLatitude++);
        putDataMapReq.getDataMap().putFloat(LATITUDE, latitude);
        putDataMapReq.getDataMap().putFloat(LONGITUDE, longitude);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        Log.d(TAG, "sending POI: " + latitude+ " , " + longitude);
    }

    private void updateTime(int timeInterval) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(TIME_PATH);
        putDataMapReq.getDataMap().putInt(INTERVAL, timeInterval);
        putDataMapReq.getDataMap().putInt(INTERVAL_COUNTER, counterInterval++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    private void updateNorth() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(NORTH_PATH);
        putDataMapReq.getDataMap().putInt(NORTH, counterNorth++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    @Override
    public void poiUpdated(float latitude, float longitude) {
        updatePoi(latitude,longitude);
    }

    @Override
    public void intervalUpdated(int intervalInMinutes) {
        updateTime(intervalInMinutes);
    }

    @Override
    public void northSelected() {
        updateNorth();
    }
}
