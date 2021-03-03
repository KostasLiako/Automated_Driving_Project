package com.example.android_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_app.mqttHandler.MqttHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Hashtable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    static final String TAG = "MAP_ACTIVITY";
    private final String UPDATE_MAP = "com.example.android_app.UPDATE_MAP";
    private final String UPDATE_PREDICTION_MAP = "com.example.android_app.UPDATE_PREDICTION_MAP";

    private String serverUrl;
    private int maxTime;

    private MqttHandler handler;
    private GoogleMap mMap;
    private BroadcastReceiver updateReceiver =  new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            // custom fields where the marker location is stored
            double timestep = Double.parseDouble(intent.getStringExtra("timestep"));
            double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
            double longitude = Double.parseDouble(intent.getStringExtra("longitude"));
            double rssi = Double.parseDouble(intent.getStringExtra("rssi"));
            double throughput = Double.parseDouble(intent.getStringExtra("throughput"));
            String speed = intent.getStringExtra("speed");
            String angle = intent.getStringExtra("angle");

            if (messageFromServer(speed, angle))
                //Log.i(TAG, "");
                addPredictionMarker(timestep, latitude, longitude, rssi, throughput);
            else
                addMarker(timestep, latitude, longitude, rssi, throughput);
                //Log.i(TAG, "");

        }
    };

    private Boolean messageFromServer(String speed, String angle) {
        if (speed.startsWith("-1") && angle.startsWith("-1"))
            return TRUE;
        else
            return FALSE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        serverUrl = getIntent().getStringExtra("SERVER_URL");
        maxTime = Integer.parseInt(getIntent().getStringExtra("MAX_TIME"));

        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_MAP);
        registerReceiver(updateReceiver, filter);

        initializeConnection();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeConnection() {
        handler = new MqttHandler(getApplicationContext(), this.serverUrl, this.maxTime, mMap);
    }

    public void stopPublishing(View view) {
        handler.stopPublishing();
    }

    public void goToMainActivity(View view) {
        handler.stopPublishing();
        finish();
    }

    public void addMarker(double timestep, double latitude, double longitude, double rssi, double throughput) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.title("Timestep: " + timestep + "\n");
        markerOptions.snippet("Lat: " + String.valueOf(latitude) +
                " Long: " + String.valueOf(longitude) +
                " Rssi: " + String.valueOf(rssi) +
                " Through: " + String.valueOf(throughput)
        );
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        Log.i(TAG, "Adding marker for real position! Timestep:" + timestep + " latitude: " + latitude + " longitude: " + longitude + " rssi: " + rssi + " throughput: " + throughput);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 200) ,200, null);
        mMap.addMarker(markerOptions);
    }

    public void addPredictionMarker(double timestep, double latitude, double longitude, double rssi, double throughput) {
//        if(timestep > 0) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.title("Prediction for Timestep: " + timestep);
        markerOptions.snippet("Lat: " + String.valueOf(latitude) +
                "|Long: " + String.valueOf(longitude) +
                "|Rssi: " + String.valueOf(rssi) +
                "|Through: " + String.valueOf(throughput)
        );
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        Log.i(TAG, "Adding marker for predicted position! Timestep:" + timestep + " Predicted latitude: " + latitude + " Predicted longitude: " + longitude + " Predicted rssi: " + rssi + " Predicted throughput: " + throughput);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 200), 200, null);
        mMap.addMarker(markerOptions);
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}